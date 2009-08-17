package org.toobsframework.pres.layout.controller;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import org.toobsframework.pres.base.HandlerBase;
import org.toobsframework.pres.base.strategy.DefaultMissingResourceStrategy;
import org.toobsframework.exception.ParameterException;
import org.toobsframework.pres.component.ComponentException;
import org.toobsframework.pres.layout.ComponentLayoutInitializationException;
import org.toobsframework.pres.layout.ComponentLayoutNotFoundException;
import org.toobsframework.pres.layout.RuntimeLayout;
import org.toobsframework.pres.layout.controller.strategy.LayoutEventStrategy;
import org.toobsframework.pres.layout.manager.IComponentLayoutManager;
import org.toobsframework.pres.url.UrlDispatchInfo;
import org.toobsframework.pres.util.ComponentRequestManager;
import org.toobsframework.pres.util.IComponentRequest;
import org.toobsframework.pres.util.PresConstants;
import org.toobsframework.transformpipeline.domain.IXMLTransformerHelper;
import org.toobsframework.util.IRequest;

public class ComponentLayoutHandler extends HandlerBase implements IComponentLayoutHandler {

  private IComponentLayoutManager componentLayoutManager = null;
  private IXMLTransformerHelper transformerHelper = null;

  private LayoutEventStrategy layoutEventStrategy;

  public void afterPropertiesSet() throws Exception {
    super.afterPropertiesSet();
    if (this.getMissingResourceStrategy() == null) {
      this.setMissingResourceStrategy(new DefaultMissingResourceStrategy());
    }
  }

  /**
   * 
   * Retrieves the URL path to use for lookup and delegates to
   * <code>getViewNameForUrlPath</code>.
   * 
   * @throws Exception Exception fetching or rendering component.
   * @see #getViewNameForUrlPath
   * 
   */
  protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response, UrlDispatchInfo dispatchInfo) throws Exception {

    Date startTime = null;
    if (log.isDebugEnabled()) {
      startTime = new Date();
    }

    if (log.isDebugEnabled()) {
      log.debug("Rendering component layout '" + dispatchInfo.getResourceId() + "' for lookup path: " + dispatchInfo.getOriginalPath());
    }

    // Set expectResponse to true so a security strategy can return params if needed
    IComponentRequest componentRequest = this.setupComponentRequest(dispatchInfo, request, response, true);

    String layoutId = dispatchInfo.getResourceId();
    if (dispatchInfo.isError() || layoutId == null) {
      this.renderLayout(componentRequest, getErrorLayout(componentRequest) );
      return null;
    }

    RuntimeLayout layout = null;
    try {

      try {
        //Set the output format for the layout
        request.setAttribute("outputFormat", dispatchInfo.getContentType());

        layout = this.componentLayoutManager.getLayout(layoutId);
        this.renderSecureLayout(componentRequest, layout);

        if (this.layoutEventStrategy != null) {
          this.layoutEventStrategy.processPostEvent(componentRequest, layout.getDoItRef());
        }

      } catch (ComponentLayoutNotFoundException cnfe) {
        log.warn("Component Layout " + dispatchInfo.getResourceId() + " not found.");
        this.renderMissingLayout(componentRequest, dispatchInfo.getResourceId());
      }

    } catch (Exception e) {
      this.renderErrorLayout(componentRequest, e );
    } finally {
      this.componentRequestManager.unset();
    }

    if (log.isDebugEnabled()) {
      Date endTime = new Date();
      log.debug("Time [" + layoutId + "] - " + (endTime.getTime() - startTime.getTime()));
    }
    return null;

  }

  private void renderSecureLayout(IComponentRequest componentRequest, RuntimeLayout layout) 
    throws ComponentLayoutInitializationException, ComponentLayoutNotFoundException, ComponentException, ParameterException, IOException {

    boolean hasAccess = true;
    if (this.getResourceSecurityStrategy() != null) {
      hasAccess = this.getResourceSecurityStrategy().hasAccess(componentRequest, layout.getId());
    }

    if (!hasAccess) {
      layout = this.getNoAccessLayout(componentRequest);
    }

    this.renderLayout(componentRequest, layout);
  }

  protected void renderMissingLayout(IComponentRequest componentRequest, String layoutId) throws ComponentException, ParameterException, ComponentLayoutInitializationException, ComponentLayoutNotFoundException, IOException {
    componentRequest.getParams().put(PresConstants.TOOBS_EXCEPTION_ATTR_NAME, new ComponentLayoutNotFoundException(layoutId));
    this.renderLayout(componentRequest, getMissingLayout(componentRequest));
  }

  protected void renderErrorLayout(IComponentRequest componentRequest, Exception e) throws ComponentException, ParameterException, ComponentLayoutInitializationException, ComponentLayoutNotFoundException, IOException {
    componentRequest.getParams().put(PresConstants.TOOBS_EXCEPTION_ATTR_NAME, e);
    this.renderLayout(componentRequest, getErrorLayout(componentRequest));
  }

  protected void renderLayout(IComponentRequest componentRequest, RuntimeLayout layout) 
    throws ComponentException, ParameterException, IOException {

    layout.renderStream(getOutputStream(componentRequest), componentRequest, componentRequest.getDispatchInfo().getContentType(), transformerHelper);
  }

  /**
   * Get the id of the error layout to use based on information in the component request
   * and return the associated layout
   * 
   * Default method returns a constant value
   * 
   * @param request - the httpRequest
   * @param response - the httpResponse
   * @throws ComponentLayoutInitializationException 
   * @throws ComponentLayoutNotFoundException 
   */
  protected RuntimeLayout getErrorLayout(IRequest componentRequest) throws ComponentLayoutInitializationException, ComponentLayoutNotFoundException {
    try {
      return this.componentLayoutManager.getLayout(PresConstants.DEFAULT_ERROR_LAYOUT);
    } catch (ComponentLayoutNotFoundException e) {
      return this.getMissingLayout(componentRequest);
    }
  }

  protected RuntimeLayout getNoAccessLayout(IRequest componentRequest) throws ComponentLayoutInitializationException, ComponentLayoutNotFoundException {
    try {
      String layoutId = this.getResourceSecurityStrategy().resolveNoAccessLayout(componentRequest);
      return this.componentLayoutManager.getLayout(layoutId);
    } catch (ComponentLayoutNotFoundException e) {
      return this.getMissingLayout(componentRequest);
    }
  }

  protected RuntimeLayout getMissingLayout(IRequest componentRequest) throws ComponentLayoutInitializationException, ComponentLayoutNotFoundException {
    try {
      String layoutId = this.getMissingResourceStrategy().resolveMissingLayout(componentRequest);
      return this.componentLayoutManager.getLayout(layoutId);
    } catch (ComponentLayoutNotFoundException e) {
      throw e;
    }
  }

  public void setComponentLayoutManager(IComponentLayoutManager componentLayoutManager) {
    this.componentLayoutManager = componentLayoutManager;
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
