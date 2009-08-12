package org.toobsframework.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IRequest {

  public abstract Map<String, Object> getParams();

  public abstract void setParams(Map<String, Object> params);

  public abstract HttpServletRequest getHttpRequest();

  public abstract void setHttpRequest(HttpServletRequest httpRequest);

  public abstract HttpServletResponse getHttpResponse();
  
  public abstract void setHttpResponse(HttpServletResponse httpResponse);

  public abstract Boolean getSingleBooleanParam(String paramName);

  public abstract String getString(String paramName);

  public abstract String[] getStringArray(String paramName);
  
  public abstract Object getParam(String paramName);

  public abstract Object putParam(String paramName, Object paramValue);
}