package org.toobsframework.transformpipeline.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ArrayList;
import java.util.TooManyListenersException;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xalan.trace.TraceListener;
import org.apache.xalan.trace.TraceManager;
import org.apache.xalan.transformer.TransformerHandlerImpl;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xerces.impl.Version;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.apache.xml.utils.XMLReaderManager;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class CachingChainedTransformer extends BaseXMLTransformer implements ErrorListener {

  private static Log log = LogFactory.getLog(CachingChainedTransformer.class);

  private Properties outputProperties = null;
  protected SAXTransformerFactory saxTFactory;
  protected Map<String, Templates> templateCache;
  protected boolean trace = false;

  public CachingChainedTransformer() {
    this(null);
  }

  public CachingChainedTransformer(URIResolver resolver) {
    this.uriResolver = resolver;
    TransformerFactory tFactory = TransformerFactory.newInstance();
    setFactoryResolver(tFactory);
    tFactory.setAttribute("http://xml.apache.org/xalan/features/incremental", java.lang.Boolean.TRUE);

    if (tFactory.getFeature(SAXSource.FEATURE) && tFactory.getFeature(SAXResult.FEATURE)) {
      // Cast the TransformerFactory to SAXTransformerFactory.
      saxTFactory = ( (SAXTransformerFactory) tFactory);
    }
    outputProperties = OutputPropertiesFactory.getDefaultMethodProperties("html");
    templateCache = new ConcurrentHashMap<String, Templates>();
    if ("true".equals(System.getProperty("toobs.trace"))) 
      this.trace = true;
  }

  public List<String> transform(
      List<String> inputXSLs,
      List<String> inputXMLs,
      Map<String, Object> inputParams,
      IXMLTransformerHelper transformerHelper) throws XMLTransformerException {

    List<String> resultingXMLs = new ArrayList<String>();
    for (int i = 0; i < inputXMLs.size(); i++) {
      resultingXMLs.add(transform(inputXSLs, (String)inputXMLs.get(i), inputParams, transformerHelper));
    }
    return resultingXMLs;
  }

  private String transform(
      List<String> inputXSLs,
      String inputXML,
      Map<String, Object> inputParams,
      IXMLTransformerHelper transformerHelper) throws XMLTransformerException {

    String outputXML = null;
    ByteArrayInputStream  xmlInputStream = null;
    ByteArrayOutputStream xmlOutputStream = null;
    XMLReader reader = null;
    List<TransformerHandler> tHandlers = null;
    try {
      if (saxTFactory != null) {

        // Get an XMLReader.
        reader = XMLReaderManager.getInstance().getXMLReader();

        Serializer serializer = SerializerFactory.getSerializer(outputProperties);

        tHandlers = new ArrayList<TransformerHandler>();
        TransformerHandler tHandler = null;

        String xslFile = null;
        TraceListener tl = new TraceListenerImpl(); 
        for (int it = 0; it < inputXSLs.size(); it++) {
          String source = inputXSLs.get(it);
          tHandler = saxTFactory.newTransformerHandler(getTemplates(source, reader));
          if (xslFile == null)
            xslFile = (String) source;

          Transformer transformer = tHandler.getTransformer();
          transformer.setOutputProperty("encoding", "UTF-8");
          transformer.setErrorListener(saxTFactory.getErrorListener());
          TraceManager trMgr = ((TransformerImpl) transformer).getTraceManager();
          try {
            if (paramListener != null) {
              TraceListener pl = paramListener.getClass().newInstance();
              trMgr.addTraceListener(pl);
            }
            if (trace) {
              trMgr.addTraceListener(tl);
            }
          } catch (TooManyListenersException e) {
          } catch (InstantiationException e) {
            throw new XMLTransformerException(e);
          } catch (IllegalAccessException e) {
            throw new XMLTransformerException(e);
          }

          if(inputParams != null) {
            Iterator<Map.Entry<String, Object>> paramIt = inputParams.entrySet().iterator();
            while (paramIt.hasNext()) {
              Map.Entry<String, Object> thisParam = paramIt.next();
              transformer.setParameter( thisParam.getKey(), thisParam.getValue());
            }
          }
          if (transformerHelper != null) {
            transformer.setParameter(TRANSFORMER_HELPER, transformerHelper);
          }
          tHandlers.add(tHandler);
        }
        tHandler = null;
        for (int th = 0; th < tHandlers.size(); th++) {
          tHandler = (TransformerHandler)tHandlers.get(th);
          if (th==0) {
            reader.setContentHandler(tHandler);
            reader.setProperty("http://xml.org/sax/properties/lexical-handler", tHandler);
          } else {
            tHandlers.get(th-1).setResult(new SAXResult(tHandler));
          }
        }

        // Parse the XML input document. The input ContentHandler and output ContentHandler
        // work in separate threads to optimize performance.
        InputSource xmlSource = null;
        xmlInputStream = new ByteArrayInputStream((inputXML).getBytes("UTF-8"));
        if (log.isTraceEnabled()) {
          log.trace("Input XML:\n" + inputXML);
        }
        xmlSource = new InputSource(xmlInputStream);
        xmlOutputStream = new ByteArrayOutputStream();
        serializer.setOutputStream(xmlOutputStream);

        tHandlers.get(tHandlers.size()-1).setResult(new SAXResult(serializer.asContentHandler()));

        Date timer = new Date();
        reader.parse(xmlSource);
        Date timer2 = new Date();

        outputXML = xmlOutputStream.toString("UTF-8");
        if (log.isDebugEnabled()) {
          long diff = timer2.getTime() - timer.getTime();
          log.debug("Time to transform: " + diff + " mS XSL: " + xslFile);
          ((TraceListenerImpl)tl).report();
          if (log.isTraceEnabled()) {
            log.trace("Output XML:\n" + outputXML);
          }
        }
      }
    } catch (IOException ex) {
      throw new XMLTransformerException(ex);
    } catch (IllegalArgumentException ex) {
      throw new XMLTransformerException(ex);
    } catch (SAXException ex) {
      throw new XMLTransformerException(ex);
    } catch (TransformerConfigurationException ex) {
      throw new XMLTransformerException(ex);
    } catch (TransformerFactoryConfigurationError ex) {
      throw new XMLTransformerException(ex);
    } catch (TransformerException ex) {
      throw new XMLTransformerException(ex);
    } finally {
      try {
        if (reader != null) {
          XMLReaderManager.getInstance().releaseXMLReader(reader);
        }
        if (xmlInputStream != null) {
          xmlInputStream.close();
          xmlInputStream = null;
        }
        if (xmlOutputStream != null) {
          xmlOutputStream.close();
          xmlOutputStream = null;
        }
      } catch (IOException ex) {
      }
    }
    return outputXML;
  }

  protected Templates getTemplates(String xsl, XMLReader reader) throws TransformerConfigurationException, TransformerException, IOException, SAXException {
    Templates templates = null;
    if (templateCache.containsKey(xsl)) {
      templates = templateCache.get(xsl);
    } else {

      TemplatesHandler templatesHandler = saxTFactory.newTemplatesHandler();
      reader.setContentHandler(templatesHandler);

      try {
        reader.parse( getInputSource(xsl) );
      } catch (MalformedURLException e) {
        log.info("Xerces Version: " +  Version.getVersion());
        throw e;
      }
      templates = templatesHandler.getTemplates();

      templateCache.put(xsl, templates);
    }

    return templates;
  }

  protected InputSource getInputSource(String xsl) throws TransformerException {
    StreamSource source = ((StreamSource)uriResolver.resolve(xsl + ".xsl", ""));
    InputSource iSource = new InputSource(source.getInputStream());
    iSource.setSystemId(source.getSystemId());
    return iSource;
  }

  public void setOutputProperties(Properties outputProperties) {
    this.outputProperties = outputProperties;
  }

  public void error(TransformerException exception) throws TransformerException {
    // TODO Auto-generated method stub
    
  }

  public void fatalError(TransformerException exception) throws TransformerException {
    // TODO Auto-generated method stub
    
  }

  public void warning(TransformerException exception) throws TransformerException {
    // TODO Auto-generated method stub
    
  }

}
