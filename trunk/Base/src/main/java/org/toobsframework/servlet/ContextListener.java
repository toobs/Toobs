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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.toobsframework.scheduler.AppScheduler;


public class ContextListener extends org.springframework.web.context.ContextLoaderListener {

  private static Log log = LogFactory.getLog(ContextListener.class);
  
  private ContextLoader contextLoader;
  private ServletContext context = null;
  private WebApplicationContext webApplicationContext;
  
  private AppScheduler scheduler = null;
  
  public void contextDestroyed(ServletContextEvent event) {
    if (scheduler != null) {
      scheduler.destroy();
    }
    //super.contextDestroyed(event);
    if (this.contextLoader != null) {
      this.contextLoader.closeWebApplicationContext(event.getServletContext());
    }
    context = null;
  }

  public void contextInitialized(ServletContextEvent event) {
    //super.contextInitialized(event);
    this.contextLoader = createContextLoader();
    webApplicationContext = this.contextLoader.initWebApplicationContext(event.getServletContext());
    
    context = event.getServletContext();
    if (webApplicationContext.containsBean("appScheduler")) {
      scheduler = (AppScheduler)webApplicationContext.getBean("appScheduler");
      //scheduler = new AppScheduler();
      scheduler.init();
    }
    log.info("Context " + context.getServletContextName() + " initialized");
  }

}
