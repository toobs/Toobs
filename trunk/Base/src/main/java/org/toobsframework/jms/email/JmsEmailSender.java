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

import java.util.List;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.MessageCreator;
import org.toobsframework.email.beans.EmailBean;
import org.toobsframework.jms.AbstractJmsSender;


public class JmsEmailSender extends AbstractJmsSender {

  private static Log log = LogFactory.getLog(JmsEmailSender.class);

  public void sendMessage(final EmailBean emailBean) {
    jmsTemplate.send(new MessageCreator() {
      public Message createMessage(Session session) throws JMSException {

        MapMessage mapMessage = null;
        try {
          mapMessage = session.createMapMessage();
          mapMessage.setString("sender", emailBean.getEmailSender());
          mapMessage.setString("subject", emailBean.getEmailSubject());
          mapMessage.setString("recipients", getRecipientList(emailBean.getRecipients()));
          mapMessage.setString("messageText", emailBean.getMessageText());
          mapMessage.setString("messageHtml", emailBean.getMessageHtml());
          mapMessage.setString("mailSenderKey", emailBean.getMailSenderKey());
          mapMessage.setInt("attempts", emailBean.getAttempts());
          mapMessage.setInt("type", emailBean.getType());
          mapMessage.setString("failureCause", emailBean.getFailureCause());
        } catch (Exception e) {
          log.error("JMS Mail exception " + e.getMessage(), e);
          throw new JMSException(e.getMessage());
        }
        return mapMessage;
      }
    });

  }
  private String getRecipientList(List recipientList) {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<recipientList.size(); i++) {
      if (i != 0) sb.append(",");
      sb.append((String)recipientList.get(i));
    }
    return sb.toString();
  }
}
