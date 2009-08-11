package org.toobsframework.social.session.common;

import java.util.Map;

import org.apache.camel.Handler;
import org.toobsframework.pres.component.dataprovider.api.DispatchContext;
import org.toobsframework.social.persistence.BrowsingContext;

public class CreateBrowsingContext {

  @Handler
  public DispatchContext createBrowsingContext(DispatchContext context) {
    BrowsingContext browser = new BrowsingContext();
    browser.setUserAgent(getParameter(context.getInputParameters(), "userAgent"));
    context.setContextObject(browser);
    return context;
  }

  private String getParameter(Map<String, Object>map, String key) {
    Object o = map.get(key);
    
    if (o == null) {
      return null;
    }
    
    if (o.getClass().isArray()) {
      Object[] oa = (Object[]) o;
      if (oa.length == 0) {
        return null;
      }
      return oa[0].toString();
    } else {
      return o.toString();
    }
  }}
