/*
 * This file is licensed to the Toobs Framework Group under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Toobs Framework Group licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file 
 * except in compliance with the License.  You may obtain a copy of the 
 * License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

  private XMLTransformerFactory() {
    this.outputPropertiesMap = new HashMap();
    Properties xmlProps = OutputPropertiesFactory.getDefaultMethodProperties("xml");
    xmlProps.setProperty("omit-xml-declaration", "yes");
    this.outputPropertiesMap.put("xml", xmlProps);

    Properties htmlProps = OutputPropertiesFactory.getDefaultMethodProperties("html");
    this.outputPropertiesMap.put("html", htmlProps);

  }

  public IXMLTransformer getDefaultTransformer(URIResolver resolver, Configuration configuration) throws XMLTransformerException {
    IXMLTransformer transformer = null;

    Class transformerClass = null;
    try {
      boolean useTranslets = configuration.useTranslets();
      boolean useChain = configuration.useChain();

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
      if (resolver == null) {
        throw new RuntimeException("uriResolver property must be set");
      }
      transformer.setURIResolver(resolver);
      transformer.init();
      transformer.setDoReload(configuration.doReload());
      transformer.setTrace(configuration.isDebug());

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

  public IXMLTransformer getChainTransformer(String outputMethod, URIResolver resolver, TraceListener paramListener, Configuration configuration) throws XMLTransformerException {
    IXMLTransformer transformer = null;

    Class transformerClass = null;
    try {
      boolean useTranslets = configuration.useTranslets();
      boolean useChain = configuration.useChain();
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
      if (resolver == null) {
        throw new RuntimeException("uriResolver property must be set");
      }
      transformer.setURIResolver(resolver);
      transformer.setParamListener(paramListener);
      transformer.init();
      transformer.setDoReload(configuration.doReload());
      transformer.setTrace(configuration.isDebug());

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

}
