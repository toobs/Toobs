package org.toobsframework.social.session.common;

import java.util.Map;

import org.apache.camel.Handler;
import org.toobsframework.pres.component.dataprovider.api.DispatchContext;
import org.toobsframework.social.persistence.model.User;

public class CreateUserContext {
  
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
