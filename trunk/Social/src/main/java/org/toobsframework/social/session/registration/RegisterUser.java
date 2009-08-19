/*
 * This file is licensed to the Toobs Framework Group under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Toobs Framework Group licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file 
 * except in compliance with the License.  You may obtain a copy of the 
 * License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
