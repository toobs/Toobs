package org.toobsframework.transformpipeline.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.transform.URIResolver;

import org.apache.xalan.trace.TraceListener;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.toobsframework.util.Configuration;


@SuppressWarnings("unchecked")
public class XMLTransformerFactory implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = -1019033346323233299L;

  /** private singleton application gateway object */
  private static XMLTransformerFactory xmlTransformerFactorySingleton =
      new XMLTransformerFactory();

  /** Holds Constant for Dynamic Transformer */
  public static final String DYNAMIC_XSL = "org.toobsframework.transformpipeline.domain.StaticXSLTransformer";
  /** Holds Constant for Static Transformer */
  public static final String STATIC_XSL = "org.toobsframework.transformpipeline.domain.StaticXSLTransformer";
  /** Holds Constant for Translet Transformer */
  public static final String TRANSLET_XSL = "org.toobsframework.transformpipeline.domain.TransletTransformer";

  //public static final String CHAIN_XSL = "org.toobsframework.transformpipeline.domain.ChainedXSLTransformer";
  public static final String CHAIN_XSL = "org.toobsframework.transformpipeline.domain.CachingChainedTransformer";

  public static final String TRANSLET_CHAIN_XSL = "org.toobsframework.transformpipeline.domain.ChainedXSLTransletTransformer";

  public static final String OUTPUT_FORMAT_XML = "xml";

  public static final String OUTPUT_FORMAT_HTML = "html";

  private HashMap outputPropertiesMap = null;

  private boolean useTranslets = false;
  private boolean useChain = false;
  private URIResolver uriResolver;
  
  /**
   * Creates a new DatatableFactory object.
   */
  private XMLTransformerFactory() {
    this.outputPropertiesMap = new HashMap();
    Properties xmlProps = OutputPropertiesFactory.getDefaultMethodProperties("xml");
    xmlProps.setProperty("omit-xml-declaration", "yes");
    this.outputPropertiesMap.put("xml", xmlProps);

    Properties htmlProps = OutputPropertiesFactory.getDefaultMethodProperties("html");
    this.outputPropertiesMap.put("html", htmlProps);

    useTranslets = Configuration.getInstance().getUseTranslets();
    useChain = Configuration.getInstance().getUseChain();

    uriResolver = new XSLUriResolverImpl();
  }

  public IXMLTransformer getDefaultTransformer(URIResolver resolver) throws XMLTransformerException {
    IXMLTransformer transformer = null;

    Class transformerClass = null;
    try {
      if (useTranslets && useChain) {
        transformerClass = java.lang.Class.forName(TRANSLET_XSL);
      } else if (useTranslets) {
        transformerClass = java.lang.Class.forName(TRANSLET_XSL);
      } else if (useChain) {
        transformerClass = java.lang.Class.forName(STATIC_XSL);
      } else {
        transformerClass = java.lang.Class.forName(STATIC_XSL);
      }
      transformer = (IXMLTransformer) transformerClass.newInstance();
      transformer.setURIResolver(resolver == null ? this.uriResolver : resolver);
    } catch(InstantiationException ie) {
      throw new XMLTransformerException("The transformer class " + transformerClass
                                        + " can not be instantiated");
    } catch(IllegalAccessException iae) {
      throw new XMLTransformerException("The transformer class " + transformerClass
                                        + " can not be accessed");
    } catch (ClassNotFoundException e) {
      throw new XMLTransformerException("The transformer class " + transformerClass
          + " can not be found");
    }
    return transformer;
  }

  public IXMLTransformer getChainTransformer(String outputMethod, URIResolver resolver, TraceListener paramListener) throws XMLTransformerException {
    IXMLTransformer transformer = null;

    Class transformerClass = null;
    try {
      if (useTranslets && useChain) {
        transformerClass = java.lang.Class.forName(TRANSLET_CHAIN_XSL);
      } else if (useTranslets) {
        transformerClass = java.lang.Class.forName(TRANSLET_XSL);
      } else if (useChain) {
        transformerClass = java.lang.Class.forName(CHAIN_XSL);
      } else {
        transformerClass = java.lang.Class.forName(STATIC_XSL);
      }
      transformer = (IXMLTransformer) transformerClass.newInstance();
      transformer.setOutputProperties((Properties)outputPropertiesMap.get(outputMethod));
      transformer.setURIResolver(resolver == null ? this.uriResolver : resolver);
      transformer.setParamListener(paramListener);
    } catch(InstantiationException ie) {
      throw new XMLTransformerException("The transformer class " + transformerClass
                                        + " can not be instantiated");
    } catch(IllegalAccessException iae) {
      throw new XMLTransformerException("The transformer class " + transformerClass
                                        + " can not be accessed");
    } catch (ClassNotFoundException e) {
      throw new XMLTransformerException("The transformer class " + transformerClass
          + " can not be found");
    }
    return transformer;
  }

  /**
   * Gets the singleton instance of the DatatableFactory
   *
   * @return The singleton instance of the DatatableFactory
   */
  public static XMLTransformerFactory getInstance() {
    return xmlTransformerFactorySingleton;
  }

  /**
   * Returns a datatable object
   *
   * @param userName String
   *
   * @return a datatable object IDatatable
   *
   * @throws DatatableException DatatableException
   */
  public IXMLTransformer getXMLTransformer(String type)
    throws XMLTransformerException {

    IXMLTransformer transformer = null;

    try {
      if (null != type) {
        Class transformerClass = Class.forName(type);
        transformer = (IXMLTransformer) transformerClass.newInstance();
      }
      else {
      }
    } catch(ClassNotFoundException cnfe) {
      throw new XMLTransformerException("The transformer class " + type
                                        + " can not be found");

    } catch(InstantiationException ie) {
      throw new XMLTransformerException("The transformer class " + type
                                        + " can not be instantiated");

    } catch(IllegalAccessException iae) {
      throw new XMLTransformerException("The transformer class " + type
                                        + " can not be accessed");

    }



    return transformer;
  }

  public boolean isUseTranslets() {
    return useTranslets;
  }

  public void setUseTranslets(boolean useTranslets) {
    this.useTranslets = useTranslets;
  }

  public boolean isUseChain() {
    return useChain;
  }

  public void setUseChain(boolean useChain) {
    this.useChain = useChain;
  }

}
