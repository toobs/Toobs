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
package org.toobsframework.pres.doit.controller.strategy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UrlPathHelper;
import org.toobsframework.exception.BaseException;
import org.toobsframework.exception.ParameterException;
import org.toobsframework.exception.PermissionException;
import org.toobsframework.exception.ValidationException;
import org.toobsframework.pres.doit.config.DoIt;
import org.toobsframework.pres.doit.config.Forward;
import org.toobsframework.pres.doit.config.Forwards;
import org.toobsframework.pres.util.ParameterUtil;
import org.toobsframework.pres.util.PresConstants;
import org.toobsframework.util.IRequest;

public class DefaultForwardStrategy implements ForwardStrategy {
  protected final Log log = LogFactory.getLog(getClass());

  public AbstractUrlBasedView resolveErrorForward(IRequest componentRequest, DoIt doIt, Map<String, Object> forwardParams, Throwable t) {
    AbstractUrlBasedView forwardView = null;

    boolean validationError = false;
    String forwardName = null;
    Map<String,Object> inputParams = componentRequest.getParams();
    Map<String,Object> responseParams = componentRequest.getResponseParams();

    if (t.getCause() instanceof ValidationException) {
      validationError = true;
      forwardParams.put(VALIDATION_ERROR_MESSAGES, responseParams.get(VALIDATION_ERROR_MESSAGES));
      forwardParams.put(VALIDATION_ERROR_OBJECTS, responseParams.get(VALIDATION_ERROR_OBJECTS));
      //addErrorForwardParams(componentRequest, thisAction, forwardParams, forwardParams);
      componentRequest.getHttpResponse().setHeader(VALIDATION_HEADER_NAME, "true");

    } else if (t.getCause() instanceof PermissionException) {
      PermissionException pe = (PermissionException)t.getCause();
      forwardName = pe.getReason() + PERMISSION_FORWARD_SUFFIX;
    } else if(t instanceof BaseException){
      componentRequest.getHttpRequest().setAttribute(PresConstants.USER_ERROR_MESSAGES_ATTR_NAME,((BaseException) t).getUserMessages());
    }

    componentRequest.getHttpRequest().setAttribute(PresConstants.TOOBS_EXCEPTION_ATTR_NAME, t);

    log.error("Error running Action: " + t);

    if (forwardName == null) {
      forwardName = getForwardName(ERROR_FORWARD_NAME_PARAM, inputParams);
      if (forwardName == null) {
        forwardName = getForwardName(ERROR_FORWARD_NAME_PARAM, responseParams);
        if (forwardName == null) {
          forwardName = DEFAULT_ERROR_FORWARD_NAME;
        }
      }
    }

    String forwardTo = null;
    Forward toobsForwardDef = getForward(doIt, forwardName);
    boolean forward = false;
    if (toobsForwardDef != null) {
      forwardTo = ParameterUtil.resoveForwardPath(componentRequest, toobsForwardDef, componentRequest.getHttpRequest().getParameterMap());
      forward = toobsForwardDef.getForward();
    } else if (validationError) {
      forwardTo = getReferer(componentRequest.getHttpRequest());
    }
    if (forwardTo == null || forwardTo.length() == 0) {
      componentRequest.getHttpResponse().setHeader(PresConstants.TOOBS_EXCEPTION_HEADER_NAME, "true");
      forwardTo = this.getReferer(componentRequest.getHttpRequest());
    }
    if (forward || validationError) {
      forwardView = new InternalResourceView(forwardTo);
    } else {
      forwardView = new InternalResourceView(forwardTo);
      //forwardView = new RedirectView(forwardTo, true);
    }
    return forwardView;
  }

  protected String getErrorUri(HttpServletRequest httpRequest) {
    String requestUri = httpRequest.getRequestURI();
    if (requestUri != null) {
      requestUri = this.stripHost(requestUri);
    }
    return requestUri;
  }

  protected String getReferer(HttpServletRequest httpRequest) {
    String referer = httpRequest.getHeader("Referer");
    if (referer != null) {
      referer = this.stripHost(referer);
      if (log.isDebugEnabled()) {
        String contextPath = new UrlPathHelper().getContextPath(httpRequest);
        log.debug("Referer: " + referer + " contextPath: " + contextPath);
      }
    }
    return referer;
  }

  private String stripHost(String uri) {
    if (uri.startsWith("http")) {
      int hostSlash = 0;
      for (int i = 0; i < 3; i++) {
        hostSlash = uri.indexOf('/', hostSlash+1);
      }
      uri = uri.substring(hostSlash);
    }
    return uri;
  }
  public AbstractUrlBasedView resolveSuccessForward(IRequest componentRequest, DoIt doIt, Map<String, Object> forwardParams) {

    AbstractUrlBasedView forwardView = null;
    String forwardName = getForwardName(FORWARD_NAME_PARAM, componentRequest.getParams());
    if (forwardName == null) {
      forwardName = DEFAULT_SUCCESS_FORWARD_NAME;
    }

    Forward toobsForwardDef = getForward(doIt, forwardName);
    if (toobsForwardDef != null) {
      String forwardTo = ParameterUtil.resoveForwardPath(componentRequest, toobsForwardDef, componentRequest.getHttpRequest().getParameterMap());
      forwardView = new RedirectView(forwardTo, true);

      if (toobsForwardDef != null && toobsForwardDef.getParameters() != null) {
        // Create a clone of the input params
        Map<String, Object> allParams = new HashMap<String,Object>(componentRequest.getParams());
        // Add the response params to it
        allParams.putAll(componentRequest.getResponseParams());
        try {
          ParameterUtil.mapParameters(componentRequest, "Forward:" + toobsForwardDef.getUri(), toobsForwardDef.getParameters().getParameter(), allParams, forwardParams, doIt.getName());
        } catch (ParameterException e) {
          log.error("Forward Parameter Mapping error " + e.getMessage(), e);
          componentRequest.getHttpResponse().setHeader(PresConstants.TOOBS_EXCEPTION_HEADER_NAME, "true");
          forwardView = new InternalResourceView(this.getReferer(componentRequest.getHttpRequest()));
        }
      }
    }

    if (log.isDebugEnabled()) {
      log.debug("Success forward to: " + (toobsForwardDef != null ? toobsForwardDef.getName() : "null") + " URI: " + (forwardView != null ? forwardView.getUrl() : "null"));
      Iterator<Map.Entry<String, Object>> iter = forwardParams.entrySet().iterator();
      while (iter.hasNext()) {
        Map.Entry<String, Object> entry = iter.next();
        log.debug("-->Forward Param - name: " + entry.getKey() + " value: " + entry.getValue());
      }
    }
    return forwardView;
  }

  protected Forward getForward(DoIt doIt, String name) {
    if (doIt == null) return null;
    Forward forward = null;
    Forwards forwards = doIt.getForwards();
    if (forwards != null) {
      Forward[] allFwds = forwards.getForward();
      for (int f = 0; f<allFwds.length; f++) {
        if (allFwds[f].getName().equals(name)) {
          forward = allFwds[f];
          break;
        }
      }
    }
    return forward;
  }

  protected String getForwardName(String name, Map<String,Object> params) {
    if (name == null || params == null) {
      return null;
    }
    Object forward = params.get(name);
    if (forward != null && forward.getClass().isArray()) {
      return ((String[])forward)[0];
    } else {
      return (String)forward;
    }
  }
  

  /* TODO Check to see if making responseParams work as error forward params
   * cause this sucks balls
   * See DoItRunner
  protected void addErrorForwardParams(IRequest request, Action actionDef, Map params, Map forwardParams) {
    Map errorParams = (Map)params.get("ErrorForwardParams");
    if (errorParams != null) {
      String guid = (String)errorParams.get("guid");
      if (guid != null && actionDef != null) {
        forwardParams.put(
            ((String[])ParameterUtil.resolveParam(request, actionDef.getGuidParam(), params))[0], guid);
      }
      Iterator iter = errorParams.keySet().iterator();
      while (iter.hasNext()) {
        Object key = iter.next();
        Object value = errorParams.get(key);
        forwardParams.put(key, value);
      }
    }
  }
  */

}
