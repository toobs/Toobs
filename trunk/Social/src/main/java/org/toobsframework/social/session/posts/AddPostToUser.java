package org.toobsframework.social.session.posts;

import org.apache.camel.Handler;
import org.toobsframework.pres.component.dataprovider.api.DispatchContext;
import org.toobsframework.social.persistence.dao.SocialDao;
import org.toobsframework.social.persistence.model.User;
import org.toobsframework.social.session.SessionBase;

public class AddPostToUser extends SessionBase {
  SocialDao dao;

  @Handler
  public DispatchContext addPost(DispatchContext context) {
    User user = (User) context.getInputParameters().get("loggedInUser");
    String userId = getParameter(context.getInputParameters(), "userId");
    String comment = getParameter(context.getInputParameters(), "comment");
    dao.addPost(user.getUserId(), userId, comment);
    
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
