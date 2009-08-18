package org.toobsframework.social.session.login;

import javax.servlet.http.HttpServletRequest;

import org.apache.camel.Handler;
import org.toobsframework.pres.component.dataprovider.api.DispatchContextEx;

public class LogOut {

  @Handler
  public DispatchContextEx logIn(DispatchContextEx context) {
    HttpServletRequest request = context.getHttpServletRequest();
    request.getSession().removeAttribute("loggedInUser");
    return context;
  }

}
