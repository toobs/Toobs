package org.toobsframework.pres.url.mapping;

import java.util.Map;

import org.toobsframework.pres.url.UrlMapping;

public class RestDispatchInfo {

  private UrlMapping mapping;
  private Map<String, String> params;
  private String resourceId;

  public RestDispatchInfo(UrlMapping mapping, Map<String, String> params) {
    this.mapping = mapping;
    this.params = params;
    init();
  }

  private void init() {
    if (mapping.getLayoutId() != null) {
      resourceId = mapping.getLayoutId();
    } else if (mapping.getComponentId() != null) {
      resourceId = mapping.getComponentId();
    } else {
      resourceId = mapping.getDoItId();
    }
  }

  public UrlMapping getMapping() {
    return mapping;
  }

  public void setMapping(UrlMapping mapping) {
    this.mapping = mapping;
  }

  public Map<String, String> getParams() {
    return params;
  }

  public void setParams(Map<String, String> params) {
    this.params = params;
  }

  public String getResourceId() {
    return resourceId;
  }

  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
  }

}
