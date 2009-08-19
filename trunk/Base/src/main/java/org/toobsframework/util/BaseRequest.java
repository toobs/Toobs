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
package org.toobsframework.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.toobsframework.pres.url.UrlDispatchInfo;

public class BaseRequest implements IRequest {
  private final UrlDispatchInfo dispatchInfo;
  private final HttpServletRequest httpRequest;
  private final HttpServletResponse httpResponse;
  private Map<String,Object> params;
  private Map<String,Object> responseParams;

  public BaseRequest(UrlDispatchInfo dispatchInfo, HttpServletRequest httpRequest, HttpServletResponse httpResponse, Map<String,Object> params, boolean expectResponse) {
    this.dispatchInfo = dispatchInfo;
    this.httpRequest = httpRequest;
    this.httpResponse = httpResponse;
    this.params = params;
    if (expectResponse) {
      this.setResponseParams(new HashMap<String,Object>());
    }
  }
  

  public Map<String,Object> getParams() {
    return params;
  }

  public void setParams(Map<String, Object> params) {
    this.params = params;
  }

  public HttpServletRequest getHttpRequest() {
    return httpRequest;
  }

  public Boolean getSingleBooleanParam(String paramName) {
    Object param = this.params.get(paramName);
    if (param != null && param.getClass().isArray()) {
      param = ((Object[])param)[0];
    }
    if (param == null || param.equals("")) {
      return null;
    }
    boolean paramVal = false;
    if (param.equals("true")) {
      paramVal = true;
    }
    return new Boolean(paramVal);
  }

  public Object getParam(String paramName) {
    return this.params.get(paramName);
  }

  public Object putParam(String paramName, Object paramValue) {
    return this.params.put(paramName, paramValue);
  }

  public String getString(String paramName) {
    Object param = this.params.get(paramName);
    if (param != null && param.getClass().isArray()) {
      param = ((Object[])param)[0];
    }
    return (String)param;
  }

  public String[] getStringArray(String paramName) {
    Object param = this.params.get(paramName);
    if (param != null && param.getClass().isArray()) {
      return (String[])param;
    } else {
      return new String[] {(String)param};
    }
  }

  public HttpServletResponse getHttpResponse() {
    return httpResponse;
  }

  public Map<String,Object> getResponseParams() {
    return responseParams;
  }

  public void setResponseParams(Map<String,Object> responseParams) {
    this.responseParams = responseParams;
  }

  public UrlDispatchInfo getDispatchInfo() {
    return dispatchInfo;
  }
}
