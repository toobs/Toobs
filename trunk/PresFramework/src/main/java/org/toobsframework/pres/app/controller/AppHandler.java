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
package org.toobsframework.pres.app.controller;

import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UrlPathHelper;
import org.toobsframework.pres.app.AppManager;
import org.toobsframework.pres.base.HandlerBase;
import org.toobsframework.pres.url.UrlDispatchInfo;
import org.toobsframework.pres.util.ComponentRequestManager;
import org.toobsframework.pres.util.IComponentRequest;
import org.toobsframework.pres.util.ParameterUtil;
import org.toobsframework.transformpipeline.domain.IXMLTransformerHelper;

public class AppHandler extends HandlerBase implements IAppHandler {

  private UrlPathHelper urlPathHelper = new UrlPathHelper();

  private AppManager appManager = null;
  private ComponentRequestManager componentRequestManager = null;
  private IXMLTransformerHelper transformerHelper = null;
  private URLResolver urlResolver; 

  /* (non-Javadoc)
   * @see org.toobsframework.pres.app.controller.IAppHandler#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response, UrlDispatchInfo dispatchInfo) throws Exception {

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

    Map<String,Object> params = ParameterUtil.buildParameterMap(request);
    IComponentRequest componentRequest = componentRequestManager.set(dispatchInfo, request, response, params, false);

    output = appManager.renderView(appRequest, componentRequest, transformerHelper);

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

  public void setTransformerHelper(IXMLTransformerHelper transformerHelper) {
    this.transformerHelper = transformerHelper;
  }

}
