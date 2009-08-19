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

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.URIResolver;

import org.apache.xalan.trace.TraceListener;

/**
 * This is the Base Interface for all datatable domain objects
 *
 * @author gtian
 * @version 1.0
 */
public interface IXMLTransformer {

  /** Holds the constant reference to the request parameter for storing the input xml vector. */
  public static final String INPUT_XML_REQUEST_PARAM = "inputXMLVec";

  /** Holds the constant reference to the request parameter for storing the input xml vector. */
  public static final String INPUT_XSL_REQUEST_PARAM = "inputXSLVec";

  /** Holds the constant reference to the request parameter for storing the input params used
   * in the xsl transforms. */
  public static final String INPUT_PARAM_REQUEST_PARAM = "inputXSLParamVec";

  public static final String USE_TRANSLETS = "xmlpipeline.UseTranslets";

  public static final String USE_CHAIN = "xmlpipeline.UseChain";

  public static final String CACHE_XSL_FILES = "xmlpipeline.CacheXSLFiles";
  
  public static final String TRANSFORMER_HELPER = "transformerHelper";

  public static final String COMPONENT_REQUEST = "componentRequest";

  /**
   * Contains the transformation logic.
   *
   * @param request
   * @param response
   * @param session
   * @param servlet
       * @param document the XML Document object create by the StrutsCXDocumentBuilder
   * @param notransform indicates if XML document should not get transformed
   *
   * @throws XMLTransformerException
   */
  public List<String> transform(
      List<String> inputXSLs,
      List<String> inputXMLs,
      Map<String, Object> params, IXMLTransformerHelper transformerHelper) throws XMLTransformerException;

  public void transformStream(
      OutputStream finalOutputStream,
      List<String> inputXSLs,
      Object xmlObject,
      Map<String,Object> inputParams,
      IXMLTransformerHelper transformerHelper) throws XMLTransformerException;

  public void setOutputProperties(Properties outputProperties);
  
  public void setURIResolver(URIResolver uriResolver);

  public void setParamListener(TraceListener paramListener);

  public void init();

  public void setTrace(boolean trace);
  public void setDoReload(boolean doReload);
}

