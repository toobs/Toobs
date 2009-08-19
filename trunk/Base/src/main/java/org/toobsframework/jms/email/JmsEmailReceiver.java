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

import java.util.ArrayList;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobsframework.email.beans.EmailBean;
import org.toobsframework.jms.AbstractJmsReceiver;


@SuppressWarnings("unchecked")
public class JmsEmailReceiver extends AbstractJmsReceiver {

  private static Log log = LogFactory.getLog(JmsEmailReceiver.class);

  public EmailBean recieveMessage() throws JmsEmailException {
    Message msg = jmsTemplate.receive();
    if (msg == null) {
      return null;
    }
    EmailBean bean = new EmailBean();
    MapMessage mapMessage = (MapMessage)msg;
    try {
      bean.setEmailSender(mapMessage.getString("sender"));
      bean.setEmailSubject(mapMessage.getString("subject"));
      bean.setRecipients(getRecipientList(mapMessage.getString("recipients")));
      bean.setMessageHtml(mapMessage.getString("messageHtml"));
      bean.setMessageText(mapMessage.getString("messageText"));
      bean.setMailSenderKey(mapMessage.getString("mailSenderKey"));
      bean.setAttempts(mapMessage.getInt("attempts"));
      bean.setType(mapMessage.getInt("type"));
      bean.setFailureCause(mapMessage.getString("failureCause"));
    } catch (JMSException e) {
      log.error("Exception getting email from queue: " + e.getMessage(), e);
      throw new JmsEmailException(e);
    }
    return bean;
  }
  
  private ArrayList getRecipientList(String recipientList) {
    String[] recipients = recipientList.split(",");
    ArrayList list = new ArrayList();
    for (int i=0; i<recipients.length; i++) {
      list.add(recipients[i]);
    }
    return list;
  }
}
