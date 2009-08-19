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
package org.toobsframework.email;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.SingletonBeanFactoryLocator;
import org.toobsframework.email.beans.ErrorEmailBean;


public class EmailHelper {
  
  private static Log log = LogFactory.getLog(EmailHelper.class);

  private static BeanFactory beanFactory;
  private static SmtpMailSender smtpSender;

  static {
    beanFactory = SingletonBeanFactoryLocator.getInstance().useBeanFactory("beanRefFactory").getFactory();
    smtpSender = (SmtpMailSender)beanFactory.getBean("SmtpMailSender");
  }
  
  public static void sendErrorEmail(String personId, String context, Throwable throwable) {
    try {
      ErrorEmailBean email = (ErrorEmailBean)beanFactory.getBean("ErrorEmailBean");
      email.setPersonId(personId);
      email.setContext(context);
      email.setThrowable(throwable);
      
      smtpSender.sendEmail(email);
    } catch (Exception e) {
      log.warn("Could not sent error notification email " + e.getMessage(), e);
    }
  }
}
