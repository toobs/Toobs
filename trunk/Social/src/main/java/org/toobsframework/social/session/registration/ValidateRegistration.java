package org.toobsframework.social.session.registration;

import org.apache.camel.Handler;
import org.springframework.validation.BeanPropertyBindingResult;
import org.toobsframework.exception.ValidationException;
import org.toobsframework.pres.component.dataprovider.api.DispatchContext;
import org.toobsframework.social.persistence.dao.SocialDao;
import org.toobsframework.social.persistence.model.User;

public class ValidateRegistration {
  SocialDao dao;
  
  @Handler
  public DispatchContext validateUser(DispatchContext context) throws ValidationException {
    User user = (User) context.getContextObject();
    
    BeanPropertyBindingResult errors = new BeanPropertyBindingResult(user, "user");
    if (user.getFirstName() == null || user.getFirstName().trim().length() == 0) {
      errors.rejectValue("firstName", "first.name.missing", "first name needs to be provided");
    }
    if (user.getLastName() == null || user.getLastName().trim().length() == 0) {
      errors.rejectValue("lastName", "last.name.missing", "last name needs to be provided");
    }
    if (user.getUserId() == null || user.getUserId().trim().length() == 0) {
      errors.rejectValue("userId", "email.missing", "email needs to be provided");
    }
    if (user.getPassword() == null || user.getPassword().trim().length() < 6) {
      errors.rejectValue("password", "password.too.short", "password needs to be at least 6 characters");
    }
    
    User existingUser = dao.getUser(user.getUserId());
    if (existingUser != null) {
      errors.reject("user.exists", "user with email " + user.getUserId() + " already exists");
    }
    if (errors.hasErrors()) {
      throw new ValidationException(errors);
    }
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
