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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.toobsframework.email.beans.EmailBean;


@SuppressWarnings("unchecked")
public class SmtpMailSender {

  private static Log log = LogFactory.getLog(SmtpMailSender.class);

  private Properties mailProperties;
  
  private Map javaMailSenders;

  public void sendEmail(EmailBean email) throws MailException, MessagingException {
    JavaMailSender javaMailSender = (JavaMailSender)javaMailSenders.get(email.getMailSenderKey());
    if (javaMailSender == null) {
      throw new MessagingException(email.getMailSenderKey() + " is an invalid mailSenderKey");
    }
    if (this.getMailProperties() != null) {
      ((JavaMailSenderImpl)javaMailSender).setJavaMailProperties(this.getMailProperties());
    }
    try {
      String[] recipients = this.processRecipients(email.getRecipients());
      String[] bccs = new String[email.getBccs().size()]; 
      for(int i = 0; i < recipients.length; i++) {
        MimeMessage message = null;
        MimeMessageHelper helper = null;
        String thisRecipient = recipients[i];
        switch (email.getType()) {
          case EmailBean.MESSAGE_TYPE_TEXT:
            message = javaMailSender.createMimeMessage();
            helper = new MimeMessageHelper(message, false, "us-ascii");
            helper.setSubject(email.getEmailSubject());
            helper.setFrom(email.getEmailSender());
            
            helper.setTo(thisRecipient);
            helper.setBcc((String[])email.getBccs().toArray(bccs));
            helper.setText(email.getMessageText(), false);
            log.info("Sending message " + message.toString());
            javaMailSender.send(message);
          break;
          case EmailBean.MESSAGE_TYPE_HTML:
            message = javaMailSender.createMimeMessage();
            helper = new MimeMessageHelper(message, true, "us-ascii");
            helper.setSubject(email.getEmailSubject());
            helper.setFrom(email.getEmailSender());
            
            helper.setTo(thisRecipient);
            helper.setBcc((String[])email.getBccs().toArray(bccs));
            helper.setText(email.getMessageText(), email.getMessageHtml());
            log.info("Sending message " + message.toString());
            javaMailSender.send(message);
          break;
        }
      }
    } catch (Exception e) {
      log.error("Mail exception " + e.getMessage(), e);
      throw new MessagingException(e.getMessage());
    }
  }

  public Properties getMailProperties() {
    return mailProperties;
  }

  public void setMailProperties(Properties mailProperties) {
    this.mailProperties = mailProperties;
  }

  public Map getJavaMailSenders() {
    return javaMailSenders;
  }

  public void setJavaMailSenders(Map javaMailSenders) {
    this.javaMailSenders = javaMailSenders;
  }
  
  private String[] processRecipients(ArrayList recipients) {
    
    List retRecipients = new ArrayList(); 
    Iterator it = recipients.iterator();
    while(it.hasNext()){
      StringTokenizer st = new StringTokenizer((String)it.next(), ",");
      while(st.hasMoreTokens()){
        retRecipients.add(st.nextToken());
      }
    }
    
    String[] ary = new String[retRecipients.size()];
    return (String[])retRecipients.toArray(ary);
  }
}
