package org.toobsframework.social.session.common;

import org.apache.camel.Handler;
import org.toobsframework.pres.component.dataprovider.api.DispatchContext;
import org.toobsframework.social.persistence.model.User;
import org.toobsframework.social.session.SessionBase;

public class CreateUserContext extends SessionBase {
  
  
  @Handler
  public DispatchContext createUserObject(DispatchContext context) {
    User user = new User();
    user.setFirstName(getParameter(context.getInputParameters(), "firstName"));
    user.setLastName(getParameter(context.getInputParameters(), "lastName"));
    user.setUserId(getParameter(context.getInputParameters(), "email"));
    user.setPassword(getParameter(context.getInputParameters(), "password"));
    
    context.setContextObject(user);
    
    return context;
  }

}
