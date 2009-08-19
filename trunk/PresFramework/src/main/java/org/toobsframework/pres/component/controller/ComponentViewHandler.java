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
package org.toobsframework.pres.component.controller;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import org.toobsframework.exception.ParameterException;
import org.toobsframework.pres.base.HandlerBase;
import org.toobsframework.pres.base.strategy.DefaultMissingResourceStrategy;
import org.toobsframework.pres.component.Component;
import org.toobsframework.pres.component.ComponentException;
import org.toobsframework.pres.component.ComponentInitializationException;
import org.toobsframework.pres.component.ComponentNotFoundException;
import org.toobsframework.pres.component.ComponentNotInitializedException;
import org.toobsframework.pres.component.manager.IComponentManager;
import org.toobsframework.pres.layout.ComponentLayoutInitializationException;
import org.toobsframework.pres.layout.ComponentLayoutNotFoundException;
import org.toobsframework.pres.url.UrlDispatchInfo;
import org.toobsframework.pres.util.ComponentRequestManager;
import org.toobsframework.pres.util.PresConstants;
import org.toobsframework.transformpipeline.domain.IXMLTransformerHelper;
import org.toobsframework.util.IRequest;

/**
* Controller that transforms the virtual filename at the end of a URL
* into a component request.  It then renders that component and dumps 
* the result into the response.
*
* <p>Example: "/index" -> "index"
* Example: "/index.comp" -> "index"
* 
* @author Sean
*/
public class ComponentViewHandler extends HandlerBase implements IComponentViewHandler {

  private IComponentManager componentManager = null;
  private IXMLTransformerHelper transformerHelper = null;

  public void afterPropertiesSet() throws Exception {
    super.afterPropertiesSet();
    if (this.getMissingResourceStrategy() == null) {
      this.setMissingResourceStrategy(new DefaultMissingResourceStrategy());
    }
  }

  @Override
  protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response, UrlDispatchInfo dispatchInfo) throws Exception {
  
    String componentId = dispatchInfo.getResourceId();
    if (log.isDebugEnabled()) {
      log.debug("Rendering component '" + componentId + "' for lookup path: " + dispatchInfo.getOriginalPath());
    }

    // Set expectResponse to true so a security strategy can return params if needed
    IRequest componentRequest = this.setupComponentRequest(dispatchInfo, request, response, true);

    Date startTime = null;
    if (log.isDebugEnabled()) {
      startTime = new Date();
    }

    Component component = null;

    try {

      try {
        //Set the output format for the layout
        request.setAttribute("outputFormat", dispatchInfo.getContentType());

        component = this.componentManager.getComponent(componentId);
        this.renderSecureComponent(componentRequest, component);

      } catch (ComponentNotFoundException cnfe) {
        log.warn("Component " + dispatchInfo.getResourceId() + " not found.");
        this.renderMissingComponent(componentRequest, dispatchInfo.getResourceId());
      }

    } catch (Exception e) {
      this.renderErrorComponent(componentRequest, e );
    } finally {
      this.componentRequestManager.unset();
    }

    if (log.isDebugEnabled()) {
      Date endTime = new Date();
      log.debug("Time [" + componentId + "] - " + (endTime.getTime() - startTime.getTime()));
    }

    return null;
  }

  private void renderSecureComponent(IRequest componentRequest, Component component) throws ComponentException, ParameterException, IOException, ComponentInitializationException, ComponentNotFoundException, ComponentNotInitializedException {

    boolean hasAccess = true;
    if (this.getResourceSecurityStrategy() != null) {
      hasAccess = this.getResourceSecurityStrategy().hasAccess(componentRequest, component.getId());
    }

    if (!hasAccess) {
      component = this.getNoAccessComponent(componentRequest);
    }

    this.renderComponent(componentRequest, component);
  }

  protected void renderMissingComponent(IRequest componentRequest, String componentId) throws ComponentException, ParameterException, ComponentNotInitializedException, ComponentInitializationException, ComponentNotFoundException, IOException {
    componentRequest.getParams().put(PresConstants.TOOBS_EXCEPTION_ATTR_NAME, new ComponentNotFoundException(componentId));
    this.renderComponent(componentRequest, this.getMissingComponent(componentRequest));
  }

  protected void renderErrorComponent(IRequest componentRequest, Exception e) throws ComponentException, ParameterException, ComponentNotInitializedException, ComponentInitializationException, ComponentNotFoundException, IOException {
    componentRequest.getParams().put(PresConstants.TOOBS_EXCEPTION_ATTR_NAME, e);
    this.renderComponent(componentRequest, getErrorComponent(componentRequest));
  }

  protected void renderComponent(IRequest componentRequest, Component component) throws ComponentException, ParameterException, IOException, ComponentNotInitializedException {
    component.renderStream(getOutputStream(componentRequest), componentRequest, componentRequest.getDispatchInfo().getContentType(), transformerHelper);
  }

  /**
   * Get the id of the error component to use based on information in the component request
   * and return the associated component
   * 
   * Default method returns a constant value
   * 
   * @param request - the httpRequest
   * @param response - the httpResponse
   * @throws ComponentLayoutInitializationException 
   * @throws ComponentLayoutNotFoundException 
   */
  protected Component getErrorComponent(IRequest componentRequest) throws ComponentInitializationException, ComponentNotFoundException {
    try {
      return this.componentManager.getComponent(configuration.getErrorComponentName());
    } catch (ComponentNotFoundException e) {
      return this.getMissingComponent(componentRequest);
    }
  }

  protected Component getNoAccessComponent(IRequest componentRequest) throws ComponentInitializationException, ComponentNotFoundException {
    try {
      String componentId = this.getResourceSecurityStrategy().resolveNoAccessLayout(componentRequest);
      return this.componentManager.getComponent(componentId);
    } catch (ComponentNotFoundException e) {
      return this.getMissingComponent(componentRequest);
    }
  }

  protected Component getMissingComponent(IRequest componentRequest) throws ComponentInitializationException, ComponentNotFoundException {
    try {
      String componentId = this.getMissingResourceStrategy().resolveMissingLayout(componentRequest);
      return this.componentManager.getComponent(componentId);
    } catch (ComponentNotFoundException e) {
      throw e;
    }
  }

  public void setComponentManager(IComponentManager componentManager) {
    this.componentManager = componentManager;
  }

  public void setComponentRequestManager(ComponentRequestManager componentRequestManager) {
    this.componentRequestManager = componentRequestManager;
  }

  /**
   * @param transformerHelper the transformerHelper to set
   */
  public void setTransformerHelper(IXMLTransformerHelper transformerHelper) {
    this.transformerHelper = transformerHelper;
  }

}
