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
