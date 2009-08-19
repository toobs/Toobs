package org.toobsframework.social.session.common;

import org.apache.camel.Handler;
import org.toobsframework.pres.component.dataprovider.api.DispatchContext;
import org.toobsframework.social.persistence.BrowsingContext;
import org.toobsframework.social.session.SessionBase;

public class CreateBrowsingContext extends SessionBase {

  @Handler
  public DispatchContext createBrowsingContext(DispatchContext context) {
    BrowsingContext browser = new BrowsingContext();
    browser.setUserAgent(getParameter(context.getInputParameters(), "userAgent"));
    context.setContextObject(browser);
    return context;
  }

}
