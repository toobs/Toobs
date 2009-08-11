package org.toobsframework.social.session.registration;

import org.apache.camel.Handler;
import org.toobsframework.pres.component.dataprovider.api.DispatchContext;
import org.toobsframework.social.persistence.dao.SocialDao;
import org.toobsframework.social.persistence.model.User;

public class RegisterUser {
  
  SocialDao dao;

  @Handler
  public DispatchContext registerUser(DispatchContext context) {
    User user = (User) context.getContextObject();
    
    dao.addUser(user.getUserId(), user.getFirstName(), user.getLastName(), user.getPassword());
    
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
