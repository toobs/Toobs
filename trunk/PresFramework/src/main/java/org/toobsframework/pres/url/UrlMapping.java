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
package org.toobsframework.pres.url;

import java.util.StringTokenizer;

public class UrlMapping {

  public static final String ANYTHING = "*";
  public static final String VARIABLE_PREFIX = ":";
  private String pattern;
  private String contentType;
  private String componentId;
  private String layoutId;
  private String doItId;
  private boolean wildcardMatching = true;
  private String[] pathParts;
  private String controllerBeanName;

  public void setPattern(String pattern) {
    this.pattern = pattern;
    this.pathParts = UrlMappingUtil.tokenizePath(pattern);
    
    if (pathParts.length > 0 && pathParts[pathParts.length - 1].equals(ANYTHING)) {
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
    return part.startsWith(VARIABLE_PREFIX) || part.equals(ANYTHING) || part.equals(requestPart);
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

  public String[] getPathParts() {
    return pathParts;
  }

  public void setPathParts(String[] pathParts) {
    this.pathParts = pathParts;
  }

  public String getControllerBeanName() {
    return controllerBeanName;
  }

  public void setControllerBeanName(String controllerBeanName) {
    this.controllerBeanName = controllerBeanName;
  }

  public void init() {
    
  }

}
