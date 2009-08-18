package org.toobsframework.social.session.user;

import org.apache.camel.Handler;
import org.toobsframework.pres.component.dataprovider.api.DispatchContext;
import org.toobsframework.social.persistence.dao.SocialDao;
import org.toobsframework.social.persistence.model.User;

public class GetUserByParameter {
  
  SocialDao dao;

  @Handler
  public User getUser(DispatchContext context) {
    String userId = (String) context.getInputParameters().get("userId");
    
    User user = dao.getUser(userId);

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
