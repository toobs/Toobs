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
package org.toobsframework.pres.component.dataprovider.api;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.toobsframework.util.IRequest;

/**
 * This class contains the data being interchanged with dispatch receivers in data providers
 * @author jaimeg@yahoo-inc.com
 *
 */
public class DispatchContextEx {
  /**
   * unstructured name of the action being taken, i.e. update, insert, register, etc. 
   */
  private String action;
  
  /**
   * The guidParam in the definition specifies the name where the guid can be obtained, and that 
   * name is searched to obtain the actual guid being passed.  The guid is searched in the request,
   * session and toobs parameters.  It usually comes in the request.<p>
   * <ol>
   * <li>toobs parameters override request parameters
   * <li>request parameters override session parameters
   * </ol>
   */
  String guid;
  
  /**
   * unstructured name of the permission requirement for the action, as specified in the action
   * i.e. USER_TELLER
   */
  String permissionContext;
  /**
   * ???
   */
  String namespace;
  /**
   * parameters are defined as part of actions, session and request, in the following order
   * <ol>
   * <li>Session attrbutes are placed first</li>
   * <li>Request parameters are placed next, overriding any existing ones (by name)</li>
   * <li>Request attributes are placed next, overriding any existing ones (by name)</li>
   * <li>"httpQueryString" parameter is added next</li>
   * <li>TODO:investigate the meaning of "multiaction.instance"</li>
   * <li>action parameters are then added
   *   <ul>
   *     <li>if the parameter is static, the "path" value is used, with the resolution indicated below</li>
   *     <li>if the parameter is NOT static, the path value is resolved as indicated (using jxpath) searching in the request attributes, request parameters and in the session, in that order</li>
   *   </ul>
   * </li>
   * </ol>
   * <h3>resolution algorithm</h3>
   * if the first letter of the parameter is one of the following, the resolution is as indicated:
   * <ul>
   *   <li> '#' - the environment of the system where the JVM resides is searched with the rest of the parameter name
   *   <li> '%' - a function is executed
   *     <ul>
   *       <li>%now - returns the current date
   *     </ul>'
   *   <li> '$' - one-level indirection - the parameter value obtain contains the name of another paramreter
   * </ul>
   */
  Map<String, Object> inputParameters;
  /**
   * On a service request, this map is initially empty. At the end of a service call within the request for an Action the following algorithm is applied
   * <ul>
   *   <li>consult each of the OutputParameters of the Action, and get its name and path
   *   <ol>
   *     <li>apply the path (through jxpath on the returned object) to obtain a value<br>
   *       <pre><code>value=jxpath(returnedObject,path)</code></pre>
   *     <li>insert the new value into the input params with the Action/OutputParameters/Parameter/@name obtained above
   *       <pre><code>outputParameters.put(name, value)</code></pre>
   *   </ol>
   * </ul>
   * 
   * This is useful in that in two-action/multi-action cases, the output parameters accumulate consecutive parameter values that can be used in the service layer
   * to detect its context.
   * <p>
   * <strong>WARNING</strong>: This method is considered an advanced topic and its use is discouraged since sessions are better implemented as independent entities.
   * <p>
   * In addition to that, the session layer could add additional values to these output parameters, but caution should be observed when doing so.
   */
  Map<String, Object> outputParameters;
  
  /**
   * Placeholder for objects to be maintained by the service layer.  This is the object that can be used for jxpath context.
   * 
   * NOTE: For camel routes, since the routes will be mostly constructed by pojos returning DispatchContext objects, provisions have
   * been made such that the return object is inferred from this property instead.
   */
  Object contextObject = null;

  private IRequest request;

  /**
   * @return the action
   */
  public String getAction() {
    return action;
  }

  /**
   * @param action the action to set
   */
  public void setAction(String action) {
    this.action = action;
  }

  /**
   * @return the guid
   */
  public String getGuid() {
    return guid;
  }

  /**
   * @param guid the guid to set
   */
  public void setGuid(String guid) {
    this.guid = guid;
  }

  /**
   * @return the permissionContext
   */
  public String getPermissionContext() {
    return permissionContext;
  }

  /**
   * @param permissionContext the permissionContext to set
   */
  public void setPermissionContext(String permissionContext) {
    this.permissionContext = permissionContext;
  }

  /**
   * @return the namespace
   */
  public String getNamespace() {
    return namespace;
  }

  /**
   * @param namespace the namespace to set
   */
  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  /**
   * @return the inputParameters
   */
  public Map<String, Object> getInputParameters() {
    return inputParameters;
  }

  /**
   * @param inputParameters the inputParameters to set
   */
  public void setInputParameters(Map<String, Object> inputParameters) {
    this.inputParameters = inputParameters;
  }

  /**
   * @return the outputParameters
   */
  public Map<String, Object> getOutputParameters() {
    return outputParameters;
  }

  /**
   * @param outputParameters the outputParameters to set
   */
  public void setOutputParameters(Map<String, Object> outputParameters) {
    this.outputParameters = outputParameters;
  }

  /**
   * @return the contextObject
   */
  public Object getContextObject() {
    return contextObject;
  }

  /**
   * @param contextObject the contextObject to set
   */
  public void setContextObject(Object contextObject) {
    this.contextObject = contextObject;
  }

  public void setRequest(IRequest request) {
    this.request = request;    
  }
  
  /**
   * @return the servlet request from the internal structures
   */
  public HttpServletRequest getHttpServletRequest() {
    return request.getHttpRequest();
  }

  /**
   * @return the servlet response from the internal structures
   */
  public HttpServletResponse getHttpServletResponse() {
    return request.getHttpResponse();
  }

}
