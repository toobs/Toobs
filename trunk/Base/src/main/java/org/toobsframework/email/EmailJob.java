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
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.toobsframework.email.beans.EmailBean;
import org.toobsframework.jms.email.IEmailMessageReceiver;
import org.toobsframework.jms.email.IEmailMessageSender;
import org.toobsframework.scheduler.ScheduledJob;


public class EmailJob implements Job, ScheduledJob {

  private static Log log = LogFactory.getLog(EmailJob.class);
  
  private static boolean running = false;
  
  private static Object synch = new Object();
  
  private int retries = 3;
  
  public EmailJob() {
  }

  public void execute(JobExecutionContext context)
    throws JobExecutionException {
    if (isRunning()) {
      log.info("Email job already running");
      return;
    }
    try {
      setRunning(true);
      log.info("Starting email processing");
      BeanFactoryLocator bfl = org.springframework.beans.factory.access.SingletonBeanFactoryLocator.getInstance();
      BeanFactoryReference bf = bfl.useBeanFactory("beanRefFactory");
      IEmailMessageReceiver emr = null;
      IEmailMessageSender ems = null;
      try {
        emr = (IEmailMessageReceiver)bf.getFactory().getBean("IEmailMessageReceiver");
        ems = (IEmailMessageSender)bf.getFactory().getBean("IEmailMessageSender");
      } catch (Exception e1) {
        log.info("JMS Email disabled");
        return;
      }
      SmtpMailSender sender = (SmtpMailSender)bf.getFactory().getBean("SmtpMailSender");
      EmailBean email = null;
      while ((email = emr.recieve()) != null) {
        try {
          if (log.isDebugEnabled()) {
            log.debug("Sending email: " + email.toString());
          }
          sender.sendEmail(email);
        } catch (Exception e) {
          log.info("Send Email failed " + e.getMessage(), e);
          email.setFailureCause(e.getMessage());
          email.setAttempts(email.getAttempts() + 1);
          if (email.getAttempts() < retries) {
            ems.send(email);
          } else {
            // Send to failed queue
            //emf.send(email);
          }
        }
      }
    } catch (Exception e) {
      log.error("Error running email job", e);
    } finally {
      setRunning(false);      
    }
  }

  public static void shutdownJob() {
  }

  public void shutdown() {
    EmailJob.shutdownJob();
  }
  
  private boolean isRunning() {
    synchronized(synch) {
      return EmailJob.running;
    }
  }

  private void setRunning(boolean running) {
    synchronized(synch) {
      EmailJob.running = running;
    }
  }
}
