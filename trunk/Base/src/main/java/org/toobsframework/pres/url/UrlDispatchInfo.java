package org.toobsframework.pres.url;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UrlDispatchInfo {

  private final String originalPath;
  private final String resourceId;
  private final String contentType;
  private final Map<String,Object> pathParameterMap;

  private boolean error;

  public UrlDispatchInfo(String originalPath, String resourceId, String contentType) {
    this.originalPath = originalPath;
    this.resourceId = resourceId;
    this.contentType = contentType;
    this.pathParameterMap = new HashMap<String,Object>();;
  }

  public UrlDispatchInfo(String originalPath, String resourceId, String contentType, Map<String,Object> pathParameterMap) {
    this.originalPath = originalPath;
    this.resourceId = resourceId;
    this.contentType = contentType;
    this.pathParameterMap = pathParameterMap;
  }

  public String getResourceId() {
    return resourceId;
  }

  public Map<String,Object> getPathParameterMap() {
    return Collections.unmodifiableMap( pathParameterMap );
  }

  public String getOriginalPath() {
    return originalPath;
  }

  public String getContentType() {
    return contentType;
  }

  public void setError(boolean error) {
    this.error = error;
  }

  public boolean isError() {
    return error;
  }

}
