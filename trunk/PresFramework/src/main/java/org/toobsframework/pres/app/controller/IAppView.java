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
package org.toobsframework.pres.app.controller;

import java.util.Map;

public interface IAppView {

  public abstract String getAppName();
  public abstract void setAppName(String appName);
  
  public abstract String getViewName();
  public abstract void setViewName(String viewName);
  
  public abstract boolean isComponentView();
  public abstract void setComponentView(boolean isComponentView);
  
  public abstract String getUrlParam(String name);
  public abstract void setUrlParam(String name, String value);
  public abstract void removeUrlParam(String name);
  public abstract Map<String,String> getUrlParams();

  public abstract void debugUrlParams();
  public abstract String getContentType();
}
