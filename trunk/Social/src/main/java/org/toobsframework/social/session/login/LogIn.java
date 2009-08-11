package org.toobsframework.social.session.login;

import java.util.Map;

import org.apache.camel.Handler;
import org.toobsframework.pres.component.dataprovider.api.DispatchContext;
import org.toobsframework.social.persistence.dao.SocialDao;
import org.toobsframework.social.persistence.model.User;

public class LogIn {

  SocialDao dao;

  @Handler
  public DispatchContext logIn(DispatchContext context) {
    User user = dao.getUser(getParameter(context.getInputParameters(), "email"));
    context.getOutputParameters().put("->session:loggedInUser", user);
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
