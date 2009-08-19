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
package org.toobsframework.pres.doit;

import org.toobsframework.exception.BaseException;


public class DoItNotFoundException extends BaseException {

  private static final long serialVersionUID = 1L;

  private String doItId;

  public DoItNotFoundException(String doItId) {
    super("Component with Id " + doItId + " not found in registry");
    this.setDoItId(doItId);
  }
  public void setDoItId(String doItId) {
    this.doItId = doItId;
  }
  public String getDoItId() {
    return doItId;
  }

}
