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
package org.toobsframework.exception;

@SuppressWarnings("serial")
public class ParameterException extends BaseException {
  private String context;
  private String name;
  private String path;
  
  public ParameterException(String context, String name, String path) {
    this.context = context;
    this.name = name;
    this.path = path;
  }

  public String getMessage() {
    return "Parameter in context " + context + " with name " 
    + name + " and path " + path + " could not be resolved";
  }
}
