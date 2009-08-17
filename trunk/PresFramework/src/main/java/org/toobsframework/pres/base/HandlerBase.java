package org.toobsframework.pres.base;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.toobsframework.pres.base.strategy.MissingResourceStrategy;
import org.toobsframework.pres.base.strategy.ResourceSecurityStrategy;
import org.toobsframework.pres.url.DispatchException;
import org.toobsframework.pres.url.UrlDispatchInfo;
import org.toobsframework.pres.url.mapping.strategy.DispatchStrategy;
import org.toobsframework.pres.url.mapping.strategy.DefaultDispatchStrategy;
import org.toobsframework.pres.util.ComponentRequestManager;
import org.toobsframework.pres.util.IComponentRequest;
import org.toobsframework.pres.util.ParameterUtil;
import org.toobsframework.pres.util.PresConstants;
import org.toobsframework.util.Configuration;
import org.toobsframework.util.IRequest;

public abstract class HandlerBase extends AbstractController implements InitializingBean {
  protected final Log log = LogFactory.getLog(getClass());

  protected ComponentRequestManager componentRequestManager = null;
  protected DispatchStrategy dispatchStrategy; 
  protected Configuration configuration;

  private MissingResourceStrategy missingResourceStrategy;
  private ResourceSecurityStrategy resourceSecurityStrategy;

  public void afterPropertiesSet() throws Exception {
    if (dispatchStrategy == null) {
      dispatchStrategy = new DefaultDispatchStrategy();
      log.info("Using default DispatchStrategy " + dispatchStrategy.getClass().getName());
    }
    this.setCacheSeconds(0);
  }

  @Override
  public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
    if (logger.isDebugEnabled()) {
      logger.debug("handleRequestInternal(" + request.getRequestURI() + ")");
    }
    UrlDispatchInfo dispatchInfo = dispatchStrategy.resolveDispatchInfo(request);
    if (dispatchInfo == null) {
      dispatchInfo = new UrlDispatchInfo(request.getRequestURI(), null, null);
      dispatchInfo.setError(true);
      request.setAttribute(PresConstants.TOOBS_EXCEPTION_ATTR_NAME, new DispatchException(request.getRequestURI()));
    }
    if (response.containsHeader(PresConstants.TOOBS_EXCEPTION_HEADER_NAME)) {
      dispatchInfo.setError(true);
    }

    return handleRequestInternal(request, response, dispatchInfo);
  }

  protected abstract ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response, UrlDispatchInfo dispatchInfo) throws Exception;

  protected IComponentRequest setupComponentRequest(UrlDispatchInfo dispatchInfo, HttpServletRequest request, HttpServletResponse response, boolean expectResponse) {
    Map<String,Object> params = ParameterUtil.buildParameterMap(request);
    return componentRequestManager.set(dispatchInfo, request, response, params, expectResponse);
  }

  protected OutputStream getOutputStream(IRequest componentRequest) throws IOException {
    return componentRequest.getHttpResponse().getOutputStream();
  }


  public DispatchStrategy getDispatchStrategy() {
    return dispatchStrategy;
  }

  public void setDispatchStrategy(DispatchStrategy dispatchStrategy) {
    this.dispatchStrategy = dispatchStrategy;
  }

  public void setComponentRequestManager(ComponentRequestManager componentRequestManager) {
    this.componentRequestManager = componentRequestManager;
  }

  public MissingResourceStrategy getMissingResourceStrategy() {
    return missingResourceStrategy;
  }

  public void setMissingResourceStrategy(MissingResourceStrategy missingResourceStrategy) {
    this.missingResourceStrategy = missingResourceStrategy;
  }

  public ResourceSecurityStrategy getResourceSecurityStrategy() {
    return resourceSecurityStrategy;
  }

  public void setResourceSecurityStrategy(ResourceSecurityStrategy resourceSecurityStrategy) {
    this.resourceSecurityStrategy = resourceSecurityStrategy;
  }

  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }

}
