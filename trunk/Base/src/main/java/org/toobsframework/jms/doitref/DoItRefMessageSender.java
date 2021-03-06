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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobsframework.doitref.beans.DoItRefBean;


public class DoItRefMessageSender implements IDoItRefMessageSender {

  private static Log log = LogFactory.getLog(DoItRefMessageSender.class);
  
  private JmsDoItRefSender jmsSender = null;
  
  public JmsDoItRefSender getJmsSender() {
    return this.jmsSender;
  }

  public void setJmsSender(JmsDoItRefSender jmsSender) {
    this.jmsSender = jmsSender;
  }
  
  public void send(DoItRefBean doItRefBean) throws JmsDoItRefException {
    try {
      if (log.isDebugEnabled()) {
        log.debug("Sending DoItRefBean message to JMS Sender ");
      }
      
      jmsSender.sendMessage(doItRefBean);
      
      if (log.isDebugEnabled()) {
        log.debug("DoItRefBean message sent to JMS Sender ");
      }
    } catch(Exception e) {
      log.warn("JmsDoIt failed: " + e.getMessage(), e);
      throw new JmsDoItRefException(e);
    }
  }

}
