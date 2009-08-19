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
package org.toobsframework.pres.app.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BaseAppRequest implements AppRequest {
  private static final Log log = LogFactory.getLog(BaseAppRequest.class);

  private String appName;
  private String viewName;
  private AppRequestTypeEnum requestType;
  private Map<String,String> urlParams;
  private String contentType;
  
  public BaseAppRequest() {
    this(null,null);
  }

  public BaseAppRequest(String appName, String viewName) {
    this(appName,viewName, "xhtml", AppRequestTypeEnum.LAYOUT);
  }

  public BaseAppRequest(String appName, String viewName, String contentType, AppRequestTypeEnum requestType) {
    this(appName, viewName, requestType, null);
  }

  public BaseAppRequest(String appName, String viewName, AppRequestTypeEnum requestType, Map<String, String> urlParams) {
    super();
    this.appName = appName;
    this.viewName = viewName;
    this.requestType = requestType;
    if (urlParams != null) {
      this.urlParams = urlParams;
    } else {
      urlParams = new HashMap<String,String>();
    }
  }

  public String getAppName() {
    return appName;
  }

  public String getUrlParam(String name) {
    return urlParams.get(name);
  }

  public Map<String, String> getUrlParams() {
    return new HashMap<String,String>(urlParams);
  }

  public String getViewName() {
    return viewName;
  }

  public void removeUrlParam(String name) {
    urlParams.remove(name);
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public void setUrlParam(String name, String value) {
    urlParams.put(name, value);
  }

  public void setViewName(String viewName) {
    this.viewName = viewName;
  }

  public void debugUrlParams() {
    if (log.isDebugEnabled()) {
      Iterator<Map.Entry<String, String>> iterator = urlParams.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry<String, String> entry = iterator.next();
        log.debug("Url Param name: [" + entry.getKey() + "] value: [" + entry.getValue() + "]");
      }
    }
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getContentType() {
    return contentType;
  }

  public void setRequestType(AppRequestTypeEnum requestType) {
    this.requestType = requestType;
  }

  public AppRequestTypeEnum getRequestType() {
    return requestType;
  }


}
