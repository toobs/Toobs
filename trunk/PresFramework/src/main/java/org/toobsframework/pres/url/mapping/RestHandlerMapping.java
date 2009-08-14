package org.toobsframework.pres.url.mapping;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.toobsframework.pres.component.controller.ComponentViewController;
import org.toobsframework.pres.doit.controller.DoItController;
import org.toobsframework.pres.layout.controller.ComponentLayoutController;
import org.toobsframework.pres.url.UrlMapping;
import org.toobsframework.pres.url.manager.IUrlManager;
import org.toobsframework.pres.url.manager.UrlManager;
import org.toobsframework.pres.util.PresConstants;
import org.toobsframework.util.Configuration;

public class RestHandlerMapping extends AbstractHandlerMapping {
  
  ComponentViewController componentConroller;
  ComponentLayoutController layoutController;
  DoItController doItcontroller;
  IUrlManager urlManager;
  
  @Override
  protected Object getHandlerInternal(HttpServletRequest httpRequest) throws Exception {
    long deployTime;
    if (httpRequest.getAttribute(PresConstants.DEPLOY_TIME) == null) {
      deployTime = Configuration.getInstance().getDeployTime();
    } else {
      deployTime = Long.parseLong((String)httpRequest.getAttribute(PresConstants.DEPLOY_TIME));
    }
    UrlMapping mapping = urlManager.getUrlMapping(httpRequest.getPathInfo(), deployTime);
    
    //Request req = (Request) httpRequest;
    // TODO Auto-generated method stub
    return mapping;
  }

}
