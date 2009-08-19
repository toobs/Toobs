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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobsframework.email.beans.EmailBean;


public class EmailMessageReceiver implements IEmailMessageReceiver {

  private static Log log = LogFactory.getLog(EmailMessageReceiver.class);
  
  private JmsEmailReceiver jmsReceiver = null;
  
  public JmsEmailReceiver getJmsReceiver() {
    return this.jmsReceiver;
  }

  public void setJmsReceiver(JmsEmailReceiver jmsReceiver) {
    this.jmsReceiver = jmsReceiver;
  }
  
  public EmailBean recieve() throws JmsEmailException {
    if (log.isDebugEnabled()) {
      log.debug("recieve()");
    }
    return jmsReceiver.recieveMessage();
  }

}
