package org.toobsframework.pres.app.controller;

import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UrlPathHelper;
import org.toobsframework.pres.app.AppManager;
import org.toobsframework.doitref.IDoItRefQueue;
import org.toobsframework.pres.security.IComponentSecurity;
import org.toobsframework.pres.util.ComponentRequestManager;
import org.toobsframework.pres.util.ParameterUtil;
import org.toobsframework.transformpipeline.domain.IXMLTransformerHelper;

@SuppressWarnings("unchecked")
public class AppHandler implements IAppHandler {
  private static Log log = LogFactory.getLog(AppHandler.class);

  private UrlPathHelper urlPathHelper = new UrlPathHelper();

  private AppManager appManager = null;
  private ComponentRequestManager componentRequestManager = null;
  private IDoItRefQueue doItRefQueue = null;
  private IXMLTransformerHelper transformerHelper = null;
  private IComponentSecurity layoutSecurity;
  private URLResolver urlResolver; 

  /* (non-Javadoc)
   * @see org.toobsframework.pres.app.controller.IAppHandler#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

    String output = "";
    String urlPath = this.urlPathHelper.getLookupPathForRequest(request);

    AppRequest appRequest = urlResolver.resolve( appManager, urlPath, request.getMethod() );
    if (log.isDebugEnabled()) {

      appManager.showApps();

      log.debug("AppView App   : " + appRequest.getAppName());
      log.debug("AppView isComp: " + appRequest.getRequestType());
      log.debug("AppView View  : " + appRequest.getViewName());
      appRequest.debugUrlParams();
    }
    
    Date startTime = null;
    if (log.isDebugEnabled()) {
      startTime = new Date();
    }

    Map params = ParameterUtil.buildParameterMap(request);
    componentRequestManager.set(request, response, params);

    output = appManager.renderView(appRequest, componentRequestManager.get(), transformerHelper);

    //Write out to the response.
    response.setContentType("text/html; charset=UTF-8");
    response.setHeader("Pragma",        "no-cache");                           // HTTP 1.0
    response.setHeader("Cache-Control", "no-cache, must-revalidate, private"); // HTTP 1.1
    PrintWriter writer = response.getWriter();
    writer.print(output);
    writer.flush();

    if (log.isDebugEnabled()) {
      Date endTime = new Date();
      log.debug("Time [" + appRequest.getAppName() + ":" + appRequest.getViewName() + "] - " + (endTime.getTime() - startTime.getTime()));
    }
    return null;

  }

  public void setAppManager(AppManager appManager) {
    this.appManager = appManager;
  }

  public void setComponentRequestManager(ComponentRequestManager componentRequestManager) {
    this.componentRequestManager = componentRequestManager;
  }

  public void setDoItRefQueue(IDoItRefQueue doItRefQueue) {
    this.doItRefQueue = doItRefQueue;
  }

  public void setLayoutSecurity(IComponentSecurity layoutSecurity) {
    this.layoutSecurity = layoutSecurity;
  }

  public void setTransformerHelper(IXMLTransformerHelper transformerHelper) {
    this.transformerHelper = transformerHelper;
  }

}
