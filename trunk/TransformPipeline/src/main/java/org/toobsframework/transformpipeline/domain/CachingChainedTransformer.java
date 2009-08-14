package org.toobsframework.transformpipeline.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ArrayList;
import java.util.TooManyListenersException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.xalan.trace.TraceListener;
import org.apache.xalan.trace.TraceManager;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.apache.xml.utils.XMLReaderManager;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class CachingChainedTransformer extends BaseXMLTransformer {

  public List<String> transform(
      List<String> inputXSLs,
      List<String> inputXMLs,
      Map<String, Object> inputParams,
      IXMLTransformerHelper transformerHelper) throws XMLTransformerException {

    List<String> resultingXMLs = new ArrayList<String>();
    for (int i = 0; i < inputXMLs.size(); i++) {
      ByteArrayOutputStream xmlOutputStream = new ByteArrayOutputStream();
      transformStream(xmlOutputStream, inputXSLs, (String)inputXMLs.get(i), inputParams, transformerHelper);
      resultingXMLs.add(xmlOutputStream.toString());
    }
    return resultingXMLs;
  }

  @Override
  public void transformStream(
      OutputStream finalOutputStream, 
      List<String> inputXSLs, 
      Object xmlObject,
      Map<String, Object> inputParams, 
      IXMLTransformerHelper transformerHelper) throws XMLTransformerException {

    try {
      String inputXML = (String)(xmlObject);
      if (log.isTraceEnabled()) {
        log.trace("Input XML:\n" + inputXML);
      }
      InputSource xmlSource = new InputSource(new ByteArrayInputStream(inputXML.getBytes("UTF-8")));
      StreamResult xmlResult = new StreamResult(finalOutputStream);

      this.transform(inputXSLs, xmlSource, inputParams, xmlResult, transformerHelper);
    } catch (UnsupportedEncodingException e) {
      throw new XMLTransformerException(e);
    }

  }

  private void transform(
      List<String> inputXSLs,
      InputSource xmlSource,
      Map<String, Object> inputParams,
      StreamResult xmlResult,
      IXMLTransformerHelper transformerHelper) throws XMLTransformerException {

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

        serializer.setOutputStream(xmlResult.getOutputStream());

        tHandlers.get(tHandlers.size()-1).setResult(new SAXResult(serializer.asContentHandler()));

        Date timer = new Date();
        reader.parse(xmlSource);
        Date timer2 = new Date();

        //outputXML = xmlOutputStream.toString("UTF-8");
        if (log.isDebugEnabled()) {
          long diff = timer2.getTime() - timer.getTime();
          log.debug("Time to transform: " + diff + " mS XSL: " + xslFile);
          ((TraceListenerImpl)tl).report();
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
      if (tHandlers != null) {
        int i = 0;
        for (TransformerHandler tHandler : tHandlers) {
          Thread t = ((TransformerImpl)tHandler.getTransformer()).getTransformThread();
          if (t != null) {
            log.info("TThread-" + (i++) + " " + t + ((TransformerImpl)tHandler.getTransformer()).hasTransformThreadErrorCatcher());
            t.interrupt();
          }
        }
      }
      if (reader != null) {
        XMLReaderManager.getInstance().releaseXMLReader(reader);
      }
    }
  }

  public void setOutputProperties(Properties outputProperties) {
    this.outputProperties = outputProperties;
  }

}
