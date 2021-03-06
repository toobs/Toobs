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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ArrayList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.parsers.SAXParser;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

@SuppressWarnings("unchecked")
public class ChainedXSLTransformer extends BaseXMLTransformer {

  /**
   * To get the logger instance
   */
  private static Log log = LogFactory.getLog(ChainedXSLTransletTransformer.class);

  private Properties outputProperties = null;

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

  private String transform(
      List inputXSLs,
      String inputXML,
      Map inputParams,
      IXMLTransformerHelper transformerHelper) throws XMLTransformerException {

    String outputXML = null;
    ByteArrayInputStream  xmlInputStream = null;
    ByteArrayOutputStream xmlOutputStream = null;
    try {
      TransformerFactory tFactory = TransformerFactory.newInstance();
      setFactoryResolver(tFactory);

      if (tFactory.getFeature(SAXSource.FEATURE) &&
          tFactory.getFeature(SAXResult.FEATURE)) {
        // Cast the TransformerFactory to SAXTransformerFactory.
        SAXTransformerFactory saxTFactory = ( (SAXTransformerFactory) tFactory);

        // Create a TransformerHandler for each stylesheet.
        ArrayList tHandlers = new ArrayList();
        TransformerHandler tHandler = null;

        // Create an XMLReader.
        XMLReader reader = new SAXParser();

        // transformer3 outputs SAX events to the serializer.
        if (outputProperties == null) {
          outputProperties = OutputPropertiesFactory.getDefaultMethodProperties("html");
        }
        Serializer serializer = SerializerFactory.getSerializer(outputProperties);
        String xslFile = null;
        for (int it = 0; it < inputXSLs.size(); it++) {
          Object source = inputXSLs.get(it);
          if (source instanceof StreamSource) {
            tHandler = saxTFactory.newTransformerHandler((StreamSource)source);
            if (xslFile == null)
              xslFile = ((StreamSource)source).getSystemId();
          } else {
            //tHandler = saxTFactory.newTransformerHandler(new StreamSource(getXSLFile((String) source)));
            tHandler = saxTFactory.newTransformerHandler(uriResolver.resolve((String) source + ".xsl", ""));
            if (xslFile == null)
              xslFile = (String) source;
          }
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
        if (log.isTraceEnabled()) {
          log.trace("Input XML:\n" + inputXML);
        }
        xmlSource = new InputSource(xmlInputStream);
        xmlOutputStream = new ByteArrayOutputStream();
        serializer.setOutputStream(xmlOutputStream);
        ((TransformerHandler)tHandlers.get(tHandlers.size()-1)).setResult(new SAXResult(serializer.asContentHandler()));

        Date timer = new Date();
        reader.parse(xmlSource);
        Date timer2 = new Date();
        outputXML = xmlOutputStream.toString("UTF-8");
        if (log.isDebugEnabled()) {
          long diff = timer2.getTime() - timer.getTime();
          log.debug("Time to transform: " + diff + " mS XSL: " + xslFile);
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
