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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

@SuppressWarnings("unchecked")
public class ValidationException extends BaseException {

  /**
   * 
   */
  private static final long serialVersionUID = 524024283814875832L;
  private Collection errors = new ArrayList();
  
  public ValidationException(Errors errors) {
    this.errors.add(errors);
  }

  public ValidationException(Collection errors) {
    this.errors = errors;
  }

  public ValidationException(Object bean, String name, String field, String errorCode) {
    BindException bindException = new BindException(bean, name);    
    bindException.rejectValue(field, errorCode);
    this.errors.add(bindException);
  }

  public ValidationException(Object bean, String name, String field, String errorCode, String defaultMessage) {
    BindException bindException = new BindException(bean, name);    
    bindException.rejectValue(field, errorCode, defaultMessage);
    this.errors.add(bindException);
  }

  public Collection getErrors() {
    return errors;
  }

  public void setErrors(Collection errors) {
    this.errors = errors;
  }

  public void addErrors(Errors errors) {
    this.errors.add(errors);
  }
  
  public String getMessage() {
    StringBuffer sb = new StringBuffer();
    if (this.errors != null) {
      Iterator errIter = errors.iterator();
      while (errIter.hasNext()) {
        Errors err = (Errors)errIter.next();
        sb.append("Validation error for object " + err.getObjectName() + "\n");
        for (int i=0; i<err.getAllErrors().size(); i++) {
          ObjectError objErr = (ObjectError)err.getAllErrors().get(i);
          sb.append(objErr.getDefaultMessage() + "\n");
        }
      }
    }
    return sb.toString();
  }
}
