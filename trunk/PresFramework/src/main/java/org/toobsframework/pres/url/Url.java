package org.toobsframework.pres.url;

public class Url {

  private String pattern;
  private String contentType;
  private String componentId;
  private String layoutId;
  private String doItId;

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public String getPattern() {
    return this.pattern;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getComponentId() {
    return componentId;
  }

  public void setComponentId(String componentId) {
    this.componentId = componentId;
  }

  public String getLayoutId() {
    return layoutId;
  }

  public void setLayoutId(String layoutId) {
    this.layoutId = layoutId;
  }

  public String getDoItId() {
    return doItId;
  }

  public void setDoItId(String doItId) {
    this.doItId = doItId;
  }
  
  public void init() {
    
  }

}
