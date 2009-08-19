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

public class AppScheduleInfo {
  private String jobClass;
  private String jobName;
  private String groupName;
  private String triggerType;
  private String jobSchedule;
  private String jobEnvCronProperty;
  
  public String getGroupName() {
    return groupName;
  }
  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }
  public String getJobClass() {
    return jobClass;
  }
  public void setJobClass(String jobClass) {
    this.jobClass = jobClass;
  }
  public String getJobEnvCronProperty() {
    return jobEnvCronProperty;
  }
  public void setJobEnvCronProperty(String jobEnvCronProperty) {
    this.jobEnvCronProperty = jobEnvCronProperty;
  }
  public String getJobName() {
    return jobName;
  }
  public void setJobName(String jobName) {
    this.jobName = jobName;
  }
  public String getJobSchedule() {
    return jobSchedule;
  }
  public void setJobSchedule(String jobSchedule) {
    this.jobSchedule = jobSchedule;
  }
  public String getTriggerType() {
    return triggerType;
  }
  public void setTriggerType(String triggerType) {
    this.triggerType = triggerType;
  }
  
}
