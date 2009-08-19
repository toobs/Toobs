package org.toobsframework.social.session.posts;

import java.util.List;

import org.apache.camel.Handler;
import org.toobsframework.social.persistence.dao.SocialDao;
import org.toobsframework.social.persistence.model.Post;
import org.toobsframework.social.persistence.model.User;

public class GetFriendsPosts {

  SocialDao dao;

  @Handler
  public List<Post> getPosts(User user) {
    List<Post> posts = dao.getPostsForAUserFriends(user.getUserId());

    return posts;
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
