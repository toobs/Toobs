package org.toobsframework.social.session.login;

import javax.servlet.http.HttpServletRequest;

import org.apache.camel.Handler;
import org.toobsframework.pres.component.dataprovider.api.DispatchContextEx;
import org.toobsframework.social.persistence.dao.SocialDao;
import org.toobsframework.social.persistence.model.User;
import org.toobsframework.social.session.SessionBase;

public class LogIn extends SessionBase {

  SocialDao dao;

  @Handler
  public DispatchContextEx logIn(DispatchContextEx context) {
    User user = dao.getUser(getParameter(context.getInputParameters(), "email"));
    HttpServletRequest request = context.getHttpServletRequest();
    request.getSession().setAttribute("loggedInUser", user);
    return context;
  }

  /**
   * @return the dao
   */
  public SocialDao getDao() {
    return dao;
  }

  /**
   * @param dao the dao to set
   */
  public void setDao(SocialDao dao) {
    this.dao = dao;
  }
}
