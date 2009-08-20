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

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xalan.trace.TraceListener;
import org.apache.xerces.impl.Version;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.utils.DefaultErrorHandler;
import org.apache.xml.utils.XMLReaderManager;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public abstract class BaseXMLTransformer implements IXMLTransformer {
  protected final Log log = LogFactory.getLog(getClass());

  protected Properties outputProperties = null;
  protected boolean trace = false;
  protected boolean doReload = false;

  protected URIResolver uriResolver;
  protected TraceListener paramListener;

  protected SAXTransformerFactory saxTFactory;
  protected Map<String, Templates> templateCache;

  public void init() {
    if (uriResolver == null) {
      throw new RuntimeException("uriResolver property must be set");
    }
    TransformerFactory tFactory = TransformerFactory.newInstance();
    setFactoryResolver(tFactory);
    tFactory.setAttribute("http://xml.apache.org/xalan/features/incremental", java.lang.Boolean.TRUE);

    if (tFactory.getFeature(SAXSource.FEATURE) && tFactory.getFeature(SAXResult.FEATURE)) {
      // Cast the TransformerFactory to SAXTransformerFactory.
      saxTFactory = ( (SAXTransformerFactory) tFactory);
    }
    if (outputProperties == null) {
      outputProperties = OutputPropertiesFactory.getDefaultMethodProperties("html");
    }
    templateCache = new ConcurrentHashMap<String, Templates>();

  }

  protected void setFactoryResolver(TransformerFactory tFactory) {
    if (uriResolver == null) {
      throw new RuntimeException("uriResolver property must be set");
    }
    tFactory.setURIResolver(uriResolver);
    tFactory.setErrorListener(new DefaultErrorHandler(true));
  }

  protected void debugParams(Map<String,Object> inputParams) {
    if (log.isDebugEnabled()) {
      log.debug("TRANSFORM XML STARTED");
      log.debug("Get input XMLs");
      Iterator<Map.Entry<String,Object>> iter = inputParams.entrySet().iterator();
      while (iter.hasNext()) {
        Map.Entry<String,Object> entry = (Map.Entry<String,Object>)iter.next();
        log.debug("  Transform Param - name: " + entry.getKey() + " value: " + entry.getValue());
      }
    }
  }

  protected Templates getTemplates(String xsl, XMLReader reader) throws TransformerConfigurationException, TransformerException, IOException, SAXException {
    Templates templates = null;
    if (templateCache.containsKey(xsl) && !doReload) {
      templates = templateCache.get(xsl);
    } else {

      // Get an XMLReader.
      if (reader == null) {
        reader = XMLReaderManager.getInstance().getXMLReader();
      }
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

  public void transformStream(
      OutputStream finalOutputStream,
      List<String> inputXSLs,
      Object xmlObject,
      Map<String,Object> inputParams,
      IXMLTransformerHelper transformerHelper) throws XMLTransformerException {
    log.info("Um...dumbass didnt implement me yet");
  }

  public void setParamListener(TraceListener paramListener) {
    this.paramListener = paramListener;
  }

  public void setURIResolver(URIResolver resolver) {
    this.uriResolver = resolver;
  }

  public boolean isTrace() {
    return trace;
  }

  public void setTrace(boolean trace) {
    this.trace = trace;
  }

  public boolean isDoReload() {
    return doReload;
  }

  public void setDoReload(boolean doReload) {
    this.doReload = doReload;
  }

}
