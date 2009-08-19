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

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.toobsframework.doitref.beans.DoItRefBean;
import org.toobsframework.jms.AbstractJmsReceiver;


@SuppressWarnings("unchecked")
public class JmsDoItRefReceiver extends AbstractJmsReceiver {

  private static Log log = LogFactory.getLog(JmsDoItRefReceiver.class);

  public DoItRefBean recieveMessage() throws JmsDoItRefException {
    DoItRefBean bean = null;
    try {
      bean = (DoItRefBean)jmsTemplate.receiveAndConvert();
    } catch (Exception e) {
      log.error("Exception getting email from queue: " + e.getMessage(), e);
      throw new JmsDoItRefException(e);
    }
    return bean;
  }
  
}
