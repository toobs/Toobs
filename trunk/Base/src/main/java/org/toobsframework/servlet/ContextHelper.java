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
package org.toobsframework.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * This class, when loaded as a spring bean, gets the application
 * context associated to it.  It then keeps it statically for
 * other classes to use.
 * 
 * jgarza: This is not the best approac but I cannot refactor all 
 *         other dependencies, especially BeanMonkey and the Tags module
 *         
 * @author spudney
 *
 */
public class ContextHelper implements ApplicationContextAware {
  private static Log log = LogFactory.getLog(ContextHelper.class);

  private static ApplicationContext webApplicationContext = null;

  public static ApplicationContext getWebApplicationContext() {
    if (webApplicationContext == null) {
      log.error("Configuration error: No bean of type " + ContextHelper.class.getName() + " has been defined in a spring configuration.  This will result in further errors.");
    }
    return webApplicationContext;
  }

  public void setWebApplicationContext(ApplicationContext inWebApplicationContext) {
    webApplicationContext = inWebApplicationContext;
  }

  public void setApplicationContext(ApplicationContext appContext) throws BeansException {
    webApplicationContext = appContext;
  }

  public static Object getBean(String beanName) {
    if (webApplicationContext == null) {
      log.error("Configuration error: No bean of type " + ContextHelper.class.getName() + " has been defined in a spring configuration.  This will result in further errors.");
    }
    return webApplicationContext.getBean(beanName);
  }
}
