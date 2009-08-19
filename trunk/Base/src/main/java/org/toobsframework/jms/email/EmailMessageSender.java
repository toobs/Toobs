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
package org.toobsframework.jms.email;

import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.mail.MailException;
import org.toobsframework.email.SmtpMailSender;
import org.toobsframework.email.beans.EmailBean;


public class EmailMessageSender implements IEmailMessageSender {

  private static Log log = LogFactory.getLog(EmailMessageSender.class);
  
  private JmsEmailSender jmsSender = null;
  
  public JmsEmailSender getJmsSender() {
    return this.jmsSender;
  }

  public void setJmsSender(JmsEmailSender jmsSender) {
    this.jmsSender = jmsSender;
  }
  
  public void send(EmailBean emailBean) throws JmsEmailException {
    try {
      if (log.isDebugEnabled()) {
        log.debug("Sending Email message to JMS Sender ");
      }
      jmsSender.sendMessage(emailBean);
      if (log.isDebugEnabled()) {
        log.debug("Email message sent to JMS Sender ");
      }
    } catch(Exception e) {
      log.warn("JmsEmail failed: " + e.getMessage() + " trying direct", e);
      BeanFactoryLocator bfl = org.springframework.beans.factory.access.SingletonBeanFactoryLocator.getInstance();
      BeanFactoryReference bf = bfl.useBeanFactory("beanRefFactory");
      SmtpMailSender sender = (SmtpMailSender)bf.getFactory().getBean("SmtpMailSender");
      try {
        sender.sendEmail(emailBean);
      } catch (MailException e1) {
        log.error("Email exception: " + e.getMessage(), e);
        throw new JmsEmailException(e);
      } catch (MessagingException e1) {
        log.error("Email exception: " + e.getMessage(), e);
        throw new JmsEmailException(e);
      }
    }
  }

}
