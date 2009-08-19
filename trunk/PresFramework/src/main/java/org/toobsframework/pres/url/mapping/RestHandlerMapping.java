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
package org.toobsframework.pres.url.mapping;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.toobsframework.pres.component.controller.ComponentViewController;
import org.toobsframework.pres.doit.controller.DoItController;
import org.toobsframework.pres.layout.controller.ComponentLayoutController;
import org.toobsframework.pres.url.UrlMapping;
import org.toobsframework.pres.url.UrlMappingUtil;
import org.toobsframework.pres.url.manager.IUrlManager;
import org.toobsframework.pres.url.mapping.strategy.DispatchStrategy;


public class RestHandlerMapping extends AbstractHandlerMapping {

  private ComponentViewController componentController;
  private ComponentLayoutController componentLayoutController;
  private DoItController doItController;
  private IUrlManager urlManager;

  @Override
  protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
    String[] paths = UrlMappingUtil.tokenizePath(request.getPathInfo());
    UrlMapping mapping = urlManager.getUrlMapping(paths);
    
    if (mapping == null) {
      return null;
    }
    
    RestDispatchInfo dispatchInfo = createDispatchInfo(paths, mapping);
    
    createRequestAttributes(request, dispatchInfo);
    return getController(dispatchInfo);
  }

  private void createRequestAttributes(HttpServletRequest request, RestDispatchInfo dispatchInfo) {
    Map <String, String> parameters = dispatchInfo.getParams();
    // 1. set the attribute names for the recognized parameters
    for (String key : parameters.keySet()) {
      String value = parameters.get(key);
      request.setAttribute(key, value);
    }
    // 2. set the attribute names for the ids and content type
    request.setAttribute(DispatchStrategy.DISPATCH_RESOURCE_ID_ATTRIBUTE, dispatchInfo.getResourceId());
    request.setAttribute(DispatchStrategy.DISPATCH_CONTENT_TYPE_ATTRIBUTE, dispatchInfo.getMapping().getContentType());
    request.setAttribute(DispatchStrategy.DISPATCH_PATTERN_ATTRIBUTE, dispatchInfo.getMapping().getPattern());
  }

  // 
  /**
   * Compute the controller name or object that will manage a given request
   * @param dispatchInfo is the previously computed dispatch information
   */
  private Object getController(RestDispatchInfo dispatchInfo) {
    UrlMapping mapping = dispatchInfo.getMapping();
    String controllerBeanName = mapping.getControllerBeanName();
    if (controllerBeanName != null && controllerBeanName.length() > 0) {
      return controllerBeanName;
    } else if (mapping.getLayoutId() != null) {
      return componentLayoutController;
    } else if (mapping.getDoItId() != null) {
      return doItController;
    } else {
      return componentController;
    }
  }

  /**
   * Returns a DispatchInfo containing the controllerBean to dispatch to and a map of the dispatch
   * parameters.
   * The client MUST ensure that this.matches(requestPathParts) is true before calling. The
   * normal client flow is to find a ControllerUrlMapping that matches and then call this method.
   */
  private RestDispatchInfo createDispatchInfo(String[] requestPathParts, UrlMapping mapping) {
    Map<String, String> params = new HashMap<String, String>();

    String lastParamName = null;
    String[] pathParts = mapping.getPathParts();
    for (int i = 0; i < requestPathParts.length; i++) {
      String requestPathPart = requestPathParts[i];
      if (i < pathParts.length && !pathParts[i].equals(UrlMapping.ANYTHING)) {
        String paramName = pathParts[i];
        if (paramName.startsWith(UrlMapping.VARIABLE_PREFIX)) {
          paramName = paramName.substring(1);
          params.put(paramName, requestPathPart);
          lastParamName = paramName;
        }
      } else {
        if (lastParamName != null) {
          params.put(lastParamName, params.get(lastParamName) + "/" + requestPathPart);
        }
      }
    }

    return new RestDispatchInfo(mapping, params);
  }

  public ComponentViewController getComponentController() {
    return componentController;
  }

  public void setComponentController(ComponentViewController componentController) {
    this.componentController = componentController;
  }

  public ComponentLayoutController getComponentLayoutController() {
    return componentLayoutController;
  }

  public void setComponentLayoutController(ComponentLayoutController componentLayoutController) {
    this.componentLayoutController = componentLayoutController;
  }

  public DoItController getDoItController() {
    return doItController;
  }

  public void setDoItController(DoItController doItController) {
    this.doItController = doItController;
  }

  public IUrlManager getUrlManager() {
    return urlManager;
  }

  public void setUrlManager(IUrlManager urlManager) {
    this.urlManager = urlManager;
  }

}
