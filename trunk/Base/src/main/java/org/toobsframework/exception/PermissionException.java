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
public class PermissionException extends BaseException {
  private String objectTypeName;
  private String action;
  private Object guid;
  private String personId;
  private String reason;
  
  public PermissionException(String objectTypeName, String action, Object guid, String personId, String reason) {
    this.objectTypeName = objectTypeName;
    this.action = action;
    this.guid = guid;
    this.personId = personId;
    this.reason = reason;
  }

  public String getMessage() {
    String message = "PermissionException";
    message += "\n  Object: " + objectTypeName;
    message += "\n  GUID  : " + guid;
    message += "\n  Action: " + action;
    message += "\n  Person: " + personId;
    message += "\n  Reason: " + reason;
    return message;
  }

  public String getAction() {
    return action;
  }

  public Object getGuid() {
    return guid;
  }

  public String getObjectTypeName() {
    return objectTypeName;
  }

  public String getPersonId() {
    return personId;
  }

  public String getReason() {
    return reason;
  }
}
