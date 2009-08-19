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
package org.toobsframework.biz.validation;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.validation.Errors;
import org.toobsframework.util.BaseRequestManager;


public abstract class BaseValidator implements IValidator, BeanFactoryAware {

  protected BeanFactory beanFactory;
  protected BaseRequestManager requestManager;
  
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }

  public void setRequestManager(BaseRequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void prePopulate(Object obj, Map properties) {
  }

  public void audit(Object obj, Map properties) {
  }

  public void validateCollection(Collection collection, Errors e) {
  }

  public boolean doCreateCollectionMember(Object obj) {
    return true;
  }
}
