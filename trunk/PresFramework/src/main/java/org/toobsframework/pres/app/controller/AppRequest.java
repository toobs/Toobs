package org.toobsframework.pres.app.controller;

import java.util.Map;

public interface AppRequest {

  public String getAppName();

  public String getUrlParam(String name);

  public Map<String, String> getUrlParams();

  public String getViewName();

  public String getContentType();

  public AppRequestTypeEnum getRequestType();

  public void debugUrlParams();
}
