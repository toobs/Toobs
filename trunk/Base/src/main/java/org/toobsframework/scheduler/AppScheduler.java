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
package org.toobsframework.scheduler;

import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.toobsframework.management.MemoryMonitor;
import org.toobsframework.util.Configuration;

public class AppScheduler implements BeanFactoryAware {
  private static final Log log = LogFactory.getLog(AppScheduler.class);

  private Configuration configuration;
  private BeanFactory beanFactory;

  private Scheduler scheduler = null;
  private MemoryMonitor memMon = null;
  private List<AppScheduleInfo> appSchedules;
  
  public AppScheduler(){
  }

  public void init() {
    try {
      SchedulerFactory sf = new StdSchedulerFactory();
      scheduler = sf.getScheduler();
      Iterator<AppScheduleInfo> iter = appSchedules.iterator();
      while (iter.hasNext()) {
        AppScheduleInfo appInfo = (AppScheduleInfo)iter.next();
        try {
          JobDetail job = new JobDetail(appInfo.getJobName(), appInfo.getGroupName(), java.lang.Class.forName(appInfo.getJobClass()));
          String jobSchedule = appInfo.getJobSchedule();
          String jobSchedOvr = configuration.getProperty(appInfo.getJobEnvCronProperty());
          if (jobSchedOvr != null && jobSchedOvr.length() > 0) {
            jobSchedule = jobSchedOvr; 
          }
          CronTrigger trigger = new CronTrigger("Trigger-" + appInfo.getJobName(), "Trigger-" + appInfo.getGroupName(), 
              appInfo.getJobName(), appInfo.getGroupName(), jobSchedule);
          
          scheduler.addJob(job, true);
          Date ft = scheduler.scheduleJob(trigger);
          
          log.info("Job " + job.getName() + " will next run at " + ft.toString());

        } catch (ClassNotFoundException e) {
          log.error("Job " + appInfo.getJobName() + " class " + appInfo.getJobClass() + " not found");
        }
      }

      scheduler.start();
      
      memMon = MemoryMonitor.getInstance();
    } catch (ParseException e) {
      log.error("ParseException in AppScheduler " + e.getMessage(), e);
    } catch (SchedulerException e) {
      log.error("SchedulerException in AppScheduler " + e.getMessage(), e);
    }
    
  }
  
  public void destroy() {
    try {
      scheduler.shutdown();
      Iterator<AppScheduleInfo> iter = appSchedules.iterator();
      while (iter.hasNext()) {
        AppScheduleInfo appInfo = (AppScheduleInfo)iter.next();
        ScheduledJob job = (ScheduledJob)beanFactory.getBean(appInfo.getJobName());
        job.shutdown();
      }
    } catch (SchedulerException e) {
      log.error("SchedulerException in AppScheduler during shutdown " + e.getMessage(), e);
    }
  }

  public List<AppScheduleInfo> getAppSchedules() {
    return appSchedules;
  }

  public void setAppSchedules(List<AppScheduleInfo> appSchedules) {
    this.appSchedules = appSchedules;
  }

  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }

  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }

}
