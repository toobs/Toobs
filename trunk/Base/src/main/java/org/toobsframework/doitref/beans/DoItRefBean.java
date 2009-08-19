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
package org.toobsframework.doitref.beans;

import java.io.Serializable;
import java.util.Map;

public class DoItRefBean implements Serializable {

  private static final long serialVersionUID = 4L;
  
  private String doItName;
  private Map paramMap;
  protected int attempts = 0;
  protected String failureCause;

  public int getAttempts() {
    return attempts;
  }
  public void setAttempts(int attempts) {
    this.attempts = attempts;
  }
  public String getFailureCause() {
    return failureCause;
  }
  public void setFailureCause(String failureCause) {
    this.failureCause = failureCause;
  }
  public String getDoItName() {
    return doItName;
  }
  public void setDoItName(String doItName) {
    this.doItName = doItName;
  }
  public Map getParamMap() {
    return paramMap;
  }
  public void setParamMap(Map paramMap) {
    this.paramMap = paramMap;
  }
  
  public DoItRefBean() {}
  
  public DoItRefBean(String doItName, Map paramMap) {
    super();
    this.doItName = doItName;
    this.paramMap = paramMap;
  }
  
}
