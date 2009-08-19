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

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Date;
import java.util.Map;

/**
 */
public class StaticXSLTransformer extends BaseXMLTransformer {

  /**
   * Implementation of the transform() method. This method first checks some
   * input parameters. Then it creates a Source object and invoces the
   * {@link #makeTransformation makeTransformation()}method.
   *
   */
  public List<String> transform(
      List<String> inputXSLs,
      List<String> inputXMLs,
      Map<String,Object> inputParams,
      IXMLTransformerHelper transformerHelper) throws XMLTransformerException {

    debugParams(inputParams);

    Iterator<String> xmlIterator = inputXMLs.iterator();

    List<String> resultingXMLs = new ArrayList<String>();

    ByteArrayOutputStream xmlOutputStream = null;

    while (xmlIterator.hasNext()) {
      try {
        Object xmlObject = xmlIterator.next();

        xmlOutputStream = new ByteArrayOutputStream();

        this.transformStream(
            xmlOutputStream, 
            inputXSLs, 
            xmlObject, 
            inputParams, 
            transformerHelper);

        resultingXMLs.add(xmlOutputStream.toString("UTF-8"));
      } catch (UnsupportedEncodingException uee) {
        log.error("Error creating output string", uee);
        throw new XMLTransformerException(uee);
      } finally {
        try {
          if (xmlOutputStream != null) {
            xmlOutputStream.close();
            xmlOutputStream = null;
          }
        } catch (IOException ex) {
        }
      }

    }

    return resultingXMLs;
  }

  /**
   * Implementation of the transform() method. This method first checks some
   * input parameters. Then it creates a Source object and invoces the
   * {@link #makeTransformation makeTransformation()}method.
   *
   */
  public void transformStream(
      OutputStream finalOutputStream,
      List<String> inputXSLs,
      Object xmlObject,
      Map<String,Object> inputParams,
      IXMLTransformerHelper transformerHelper) throws XMLTransformerException {

    debugParams(inputParams);

    Iterator<String> xslIterator = inputXSLs.iterator();

    ByteArrayInputStream  xmlInputStream = null;
    // In this implementation we need to use an intermediary
    OutputStream xmlOutputStream = null;
    boolean useTempStream = inputXSLs.size() > 1 && ! (finalOutputStream instanceof ByteArrayOutputStream);
    if (useTempStream) {
      xmlOutputStream = new ByteArrayOutputStream();
    } else {
      xmlOutputStream = finalOutputStream;
    }

    try {
      while (xslIterator.hasNext()) {
        String xslFile = (String) xslIterator.next();
        if (xmlInputStream == null) {
          if (xmlObject instanceof org.w3c.dom.Node) {
            TransformerFactory tf=TransformerFactory.newInstance();
            //identity
            Transformer t=tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            t.transform(new DOMSource( (org.w3c.dom.Node)xmlObject ), new StreamResult( os ));
            xmlInputStream = new ByteArrayInputStream(os.toByteArray());
            //xmlString = os.toString("UTF-8");
            if (log.isTraceEnabled()) {
              log.trace("Input XML for " + xslFile + " : " + os.toString("UTF-8"));
            }
          } else {
            //xmlString = (String) xmlObject;
            xmlInputStream = new ByteArrayInputStream(((String) xmlObject).getBytes("UTF-8"));
            if (log.isTraceEnabled()) {
              log.trace("Input XML for " + xslFile + " : " + xmlObject);
            }
          }
        }
        StreamSource xmlSource = new StreamSource(xmlInputStream);
        StreamResult xmlResult = new StreamResult(xmlOutputStream);

        this.doTransform(xslFile, xmlSource, inputParams, transformerHelper, xmlResult, xslFile);

        if (useTempStream) {
          xmlInputStream = new ByteArrayInputStream(((ByteArrayOutputStream) xmlResult.getOutputStream()).toByteArray());
          log.debug("First Pass: \n" + new String(((ByteArrayOutputStream) xmlResult.getOutputStream()).toByteArray()));
        }
      }
      if (useTempStream) {
        ((ByteArrayOutputStream) xmlOutputStream).writeTo(finalOutputStream);
      }
    } catch (UnsupportedEncodingException uee) {
      log.error("Error creating output string", uee);
      throw new XMLTransformerException(uee);
    } catch (TransformerException te) {
      log.error("Error creating input xml: " + te.getMessage(), te);
      throw new XMLTransformerException(te);
    } catch (IOException ioe) {
      log.error("Error creating output stream", ioe);
      throw new XMLTransformerException(ioe);
    } finally {
      try {
        if (xmlInputStream != null) {
          xmlInputStream.close();
          xmlInputStream = null;
        }
      } catch (IOException ex) {
      }
    }
  }

  /**
   * This method actually does all the XML Document transformation.
   * <p>
   * @param xslSource
   *          holds the xslFile
   * @param xmlSource
   *          holds the xmlFile
   * @param params
   *          holds the params needed to do this transform
   * @param xmlResult
   *          holds the streamResult of the transform.
   */
  @SuppressWarnings("unchecked")
  protected void doTransform(
      String xslSource,
      Source xmlSource,
      Map params,
      IXMLTransformerHelper transformerHelper,
      StreamResult xmlResult,
      String xslFile) throws XMLTransformerException {

    try {
      Transformer transformer = getTemplates(xslFile, null).newTransformer();

      transformer.setErrorListener(saxTFactory.getErrorListener());
      
      // 2.2 Set character encoding for all transforms to UTF-8.
      transformer.setOutputProperty("encoding", "UTF-8");

      // 2.5 Set Parameters necessary for transformation.
      if(params != null) {
        Iterator paramIt = params.entrySet().iterator();
        while (paramIt.hasNext()) {
          Map.Entry thisParam = (Map.Entry) paramIt.next();
          transformer.setParameter( (String) thisParam.getKey(), thisParam.getValue());
        }
      }

      if (transformerHelper != null) {
        transformer.setParameter(TRANSFORMER_HELPER, transformerHelper);
      }

      // 3. Use the Transformer to transform an XML Source and send the
      //    output to a Result object.
      Date timer = new Date();
      transformer.transform(xmlSource, xmlResult);
      Date timer2 = new Date();
      if (log.isDebugEnabled()) {
        long diff = timer2.getTime() - timer.getTime();
        log.debug("Time to transform: " + diff + " mS XSL: " + xslFile);
      }
    } catch(TransformerConfigurationException tce) {
      throw new XMLTransformerException(tce);
    } catch(TransformerException te) {
      throw new XMLTransformerException(te);
    } catch (IOException ioe) {
      throw new XMLTransformerException(ioe);
    } catch (SAXException se) {
      throw new XMLTransformerException(se);
    }

  }

  public void setOutputProperties(Properties outputProperties) {
  }

}
