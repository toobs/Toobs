package org.toobsframework.pres.url;

import java.util.StringTokenizer;

public class UrlMapping {

  private String pattern;
  private String contentType;
  private String componentId;
  private String layoutId;
  private String doItId;
  private boolean wildcardMatching = true;
  private String[] pathParts;

  public void setPattern(String pattern) {
    this.pattern = pattern;
    this.pathParts = UrlMappingUtil.tokenizePath(pattern);
    
    if (pathParts.length > 0 && pathParts[pathParts.length - 1].equals("*")) {
      wildcardMatching = true;
    }
  }
  
  /**
   * Returns true if the request path parts matches the path parts
   * for this mapping (i.e., the request should be dispatched to the
   * controller specified in this mapping).
   */
  public boolean matches(String[] requestPathParts) {
    boolean matches = true;
    if (pathParts.length == requestPathParts.length || (wildcardMatching && requestPathParts.length >= pathParts.length - 1)) {
      for (int i = 0; i < requestPathParts.length && matches; i++) {
        if (i < pathParts.length && !partMatches(pathParts[i], requestPathParts[i])) {
          matches = false;
        }
      }
    } else {
      matches = false;
    }

    return matches;
  }
  
  /**
   * A part matches if it starts with ':' or is a '*' or is identical to the requestPart 
   * @param part is the current part
   * @param requestPart is the part from the request
   * @return
   */
  private boolean partMatches(String part, String requestPart) {
    return part.startsWith(":") || part.equals("*") || part.equals(requestPart);
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
  
  public boolean isWildcardMatching() {
    return wildcardMatching;
  }

  public void setWildcardMatching(boolean wildcardMatching) {
    this.wildcardMatching = wildcardMatching;
  }

  public void init() {
    
  }

}
