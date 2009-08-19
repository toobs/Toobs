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
package org.toobsframework.email.beans;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.toobsframework.util.BaseRequestManager;
import org.toobsframework.util.Configuration;
import org.toobsframework.util.string.StringResource;


@SuppressWarnings("unchecked")
public class ErrorEmailBean extends EmailBean {

  private BaseRequestManager requestManager;
  private String personId;
  private String context;
  private Throwable throwable;

  public ErrorEmailBean () {
  }
  
  public String getEmailSubject() {
    String[] arguments = new String[] {/*Configuration.getInstance().getMainHost(),*/ context, throwable.getMessage()};
    return StringResource.formatString(super.getEmailSubject(), arguments);
  }

  public String getMessageHtml() throws Exception {
    return this.getMessageText();
  }
  
  public String getMessageText() throws Exception {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    
    sw.append("An Error occurred in context [");
    sw.append(context);
    sw.append("] for person [");
    sw.append(personId);
    sw.append("]\n\nMessage: ");
    sw.append(throwable.getMessage());
    sw.append("\n\nStack traces\n");
    while (throwable != null) {
      sw.append("Caused by: ");
      throwable.printStackTrace(pw);
      throwable = throwable.getCause();
    }
    return sw.toString();
  }

  public String getContext() {
    return context;
  }

  public void setContext(String context) {
    this.context = context;
  }

  public String getPersonId() {
    return personId;
  }

  public void setPersonId(String personId) {
    this.personId = personId;
  }

  public Throwable getThrowable() {
    return throwable;
  }

  public void setThrowable(Throwable throwable) {
    this.throwable = throwable;
  }

  public BaseRequestManager getRequestManager() {
    return requestManager;
  }

  public void setRequestManager(BaseRequestManager requestManager) {
    this.requestManager = requestManager;
  }
}
