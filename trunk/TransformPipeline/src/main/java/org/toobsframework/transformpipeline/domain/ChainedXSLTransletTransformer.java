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

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.TransformerHandler;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xalan.xsltc.trax.TransformerFactoryImpl;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;

import java.util.List;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@SuppressWarnings("unchecked")
public class ChainedXSLTransletTransformer extends BaseXMLTransformer {

  /**
   * To get the logger instance
   */
  private static Log log = LogFactory.getLog(ChainedXSLTransletTransformer.class);

  private Properties outputProperties = null;

  /**
   * Implementation of the transform() method. This method first checks some
   * input parameters. Then it creates a Source object and invoces the
   * {@link #makeTransformation makeTransformation()}method.
   *
   */
  public List transform(
      List inputXSLs,
      List inputXMLs,
      Map inputParams,
      IXMLTransformerHelper transformerHelper) throws XMLTransformerException {

    ArrayList resultingXMLs = new ArrayList();
    for (int i = 0; i < inputXMLs.size(); i++) {
      resultingXMLs.add(transform(inputXSLs, (String)inputXMLs.get(i), inputParams, transformerHelper));
    }
    return resultingXMLs;
  }

  public String transform(
      List inputXSLs,
      String inputXML,
      Map inputParams,
      IXMLTransformerHelper transformerHelper) throws XMLTransformerException {

    String outputXML = null;
    ByteArrayInputStream  xmlInputStream = null;
    ByteArrayOutputStream xmlOutputStream = null;
    try {
      TransformerFactory tFactory = new org.apache.xalan.xsltc.trax.TransformerFactoryImpl();
      try {
        //tFactory.setAttribute("use-classpath", Boolean.TRUE);
        tFactory.setAttribute("auto-translet", Boolean.TRUE);
      } catch (IllegalArgumentException iae) {
        log.error("Error setting XSLTC specific attribute", iae);
        throw new XMLTransformerException(iae);
      }
      setFactoryResolver(tFactory);

      TransformerFactoryImpl traxFactory = (TransformerFactoryImpl)tFactory;

      // Create a TransformerHandler for each stylesheet.
      ArrayList tHandlers = new ArrayList();
      TransformerHandler tHandler = null;

      // Create a SAX XMLReader.
      XMLReader reader = new org.apache.xerces.parsers.SAXParser();

      // transformer3 outputs SAX events to the serializer.
      if (outputProperties == null) {
        outputProperties = OutputPropertiesFactory.getDefaultMethodProperties("html");
      }
      Serializer serializer = SerializerFactory.getSerializer(outputProperties);
      for (int it = 0; it < inputXSLs.size(); it++) {
        String xslTranslet = (String) inputXSLs.get(it);
        Source source = uriResolver.resolve(xslTranslet + ".xsl", "");

        String tPkg = source.getSystemId().substring(0, source.getSystemId().lastIndexOf("/")).replaceAll("/", ".").replaceAll("-", "_");

        // Package name needs to be set for each TransformerHandler instance
        tFactory.setAttribute("package-name", tPkg);
        tHandler = traxFactory.newTransformerHandler(source);

        // Set parameters and output encoding on each handlers transformer
        Transformer transformer = tHandler.getTransformer();
        transformer.setOutputProperty("encoding", "UTF-8");
        transformer.setErrorListener(tFactory.getErrorListener());
        if(inputParams != null) {
          Iterator paramIt = inputParams.entrySet().iterator();
          while (paramIt.hasNext()) {
            Map.Entry thisParam = (Map.Entry) paramIt.next();
            transformer.setParameter( (String) thisParam.getKey(),
                                      (String) thisParam.getValue());
          }
        }
        if (transformerHelper != null) {
          transformer.setParameter(TRANSFORMER_HELPER, transformerHelper);
        }
        tHandlers.add(tHandler);
      }
      tHandler = null;
      // Link the handlers to each other and to the reader
      for (int th = 0; th < tHandlers.size(); th++) {
        tHandler = (TransformerHandler)tHandlers.get(th);
        if (th==0) {
          reader.setContentHandler(tHandler);
          reader.setProperty("http://xml.org/sax/properties/lexical-handler", tHandler);
        } else {
          ((TransformerHandler)tHandlers.get(th-1)).setResult(new SAXResult(tHandler));
        }
      }
      // Parse the XML input document. The input ContentHandler and output ContentHandler
      // work in separate threads to optimize performance.
      InputSource xmlSource = null;
      xmlInputStream = new ByteArrayInputStream((inputXML).getBytes("UTF-8"));
      xmlSource = new InputSource(xmlInputStream);
      xmlOutputStream = new ByteArrayOutputStream();
      serializer.setOutputStream(xmlOutputStream);
      // Link the last handler to the serializer
      ((TransformerHandler)tHandlers.get(tHandlers.size()-1)).setResult(new SAXResult(serializer.asContentHandler()));
      reader.parse(xmlSource);
      outputXML = xmlOutputStream.toString("UTF-8");
    }
    catch (Exception ex) {
      log.error("Error performing chained transformation: " + ex.getMessage(), ex);
      throw new XMLTransformerException(ex);
    } finally {
      try {
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

  public void setOutputProperties(Properties outputProperties) {
    this.outputProperties = outputProperties;
  }
}
