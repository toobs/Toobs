package org.toobsframework.social.session.user;

import org.apache.camel.Handler;
import org.toobsframework.pres.component.dataprovider.api.DispatchContext;
import org.toobsframework.social.persistence.dao.SocialDao;
import org.toobsframework.social.persistence.model.User;

public class GetLoggedInUser {

  SocialDao dao;

  @Handler
  public User getUser(DispatchContext context) {
    User user = (User) context.getInputParameters().get("loggedInUser");
    
    return user;
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
