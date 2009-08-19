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
package org.toobsframework.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.toobsframework.pres.url.UrlDispatchInfo;

public interface IRequest {

  public abstract Map<String, Object> getParams();

  public abstract void setParams(Map<String, Object> params);

  public abstract HttpServletRequest getHttpRequest();

  public abstract HttpServletResponse getHttpResponse();
  
  public abstract Boolean getSingleBooleanParam(String paramName);

  public abstract String getString(String paramName);

  public abstract String[] getStringArray(String paramName);
  
  public abstract Object getParam(String paramName);

  public abstract Object putParam(String paramName, Object paramValue);

  public abstract Map<String,Object> getResponseParams();

  public abstract void setResponseParams(Map<String,Object> responseParams);

  public abstract UrlDispatchInfo getDispatchInfo();
}