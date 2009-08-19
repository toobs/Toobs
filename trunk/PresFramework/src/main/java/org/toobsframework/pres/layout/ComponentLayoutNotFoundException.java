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
package org.toobsframework.pres.layout;

import org.toobsframework.exception.BaseException;
/**
 * @author stewari
 */
public class ComponentLayoutNotFoundException extends BaseException {
   
  private static final long serialVersionUID = 1L;
  private String componentLayoutId;
  
  public ComponentLayoutNotFoundException(String componentLayoutId) {
    super("Component with Id " + componentLayoutId + " not found in registry");
    this.componentLayoutId = componentLayoutId;
  }

  public void setComponentLayoutId(String componentLayoutId) {
    this.componentLayoutId = componentLayoutId;
  }
  
  public String getComponentLayoutId() {
      return this.componentLayoutId;
  }

  public String getMessage() {
    return "Component with Id " + componentLayoutId + " not found in registry";
  }
}
