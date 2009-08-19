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
package org.toobsframework.pres.doit.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.toobsframework.pres.base.HandlerBase;
import org.toobsframework.pres.doit.config.DoIt;
import org.toobsframework.pres.doit.controller.strategy.ForwardStrategy;
import org.toobsframework.pres.doit.manager.IDoItManager;
import org.toobsframework.pres.doit.DoItNotFoundException;
import org.toobsframework.pres.doit.IDoItRunner;
import org.toobsframework.pres.url.UrlDispatchInfo;
import org.toobsframework.util.IRequest;

/**
 * Controller that transforms the virtual filename at the end of a URL into a
 * component request. It then renders that component and dumps the result into
 * the response.
 * 
 * <p>
 * Example: "/index" -> "index" Example: "/index.comp" -> "index"
 * 
 * @author pudney
 */
public class DoItHandler extends HandlerBase implements IDoItHandler {

  private IDoItRunner doItRunner;
  private IDoItManager doItManager;
  private ForwardStrategy forwardStrategy;

  /**
   * 
   * Retrieves the URL path to use for lookup and delegates to
   * <code>getViewNameForUrlPath</code>.
   * 
   * @throws Exception
   *           Exception fetching or rendering component.
   * @see #getViewNameForUrlPath
   * 
   */
  protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response, UrlDispatchInfo dispatchInfo) throws Exception {

    AbstractUrlBasedView forwardView = null;

    Map<String, Object> forwardParams = new HashMap<String, Object>();

    // Set component request with response params
    setupComponentRequest(dispatchInfo, request, response, true);
    IRequest componentRequest = componentRequestManager.get();
    DoIt doIt = null;

    try {
      // Get DoIt
      doIt = doItManager.getDoIt(dispatchInfo.getResourceId());
      if (doIt == null) {
        throw new DoItNotFoundException(dispatchInfo.getResourceId());
      }

      doItRunner.runDoIt(componentRequest, doIt);

      /*
       * HEY I REMOVED THIS CAUSE ITS REALLY WRONG for (Map.Entry<String,
       * Object> entry : componentRequest.getResponseParams().entrySet()) {
       * request.setAttribute(entry.getKey(), entry.getValue()); if (
       * entry.getKey().startsWith("->session:")) { String key2 =
       * entry.getKey().substring(10); request.getSession().setAttribute(key2,
       * entry.getValue()); } }
       */

      // Everything ran successfully. Forward to forward, if there is one
      // defined.
      forwardView = forwardStrategy.resolveSuccessForward(componentRequest, doIt, forwardParams);
      if (forwardView != null) {
        return new ModelAndView(forwardView, forwardParams);
      }

    } catch (Exception e) {
      forwardView = forwardStrategy.resolveErrorForward(componentRequest, doIt, forwardParams, e);
      if (forwardView != null) {
        return new ModelAndView(forwardView, forwardParams);
      } else {
        throw e;
      }
    } finally {
      this.componentRequestManager.unset();
    }

      return null;
  }

  public void setDoItRunner(IDoItRunner doItRunner) {
    this.doItRunner = doItRunner;
  }

  public void setDoItManager(IDoItManager doItManager) {
    this.doItManager = doItManager;
  }

  public void setForwardStrategy(ForwardStrategy forwardStrategy) {
    this.forwardStrategy = forwardStrategy;
  }

}
