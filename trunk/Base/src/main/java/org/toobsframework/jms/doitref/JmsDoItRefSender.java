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
package org.toobsframework.jms.doitref;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.MessageCreator;
import org.toobsframework.doitref.beans.DoItRefBean;
import org.toobsframework.jms.AbstractJmsSender;


public class JmsDoItRefSender extends AbstractJmsSender {

  private static Log log = LogFactory.getLog(JmsDoItRefSender.class);

  public void sendMessage(final DoItRefBean doItRefBean) {
    jmsTemplate.send(new MessageCreator() {
      public Message createMessage(Session session) throws JMSException {

        Message objMessage = null;
        try {
          objMessage = session.createObjectMessage(doItRefBean);
        } catch (Exception e) {
          log.error("JMS Mail exception " + e.getMessage(), e);
          throw new JMSException(e.getMessage());
        }
        return objMessage;
      }
    });

  }
}
