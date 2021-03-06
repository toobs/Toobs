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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobsframework.pres.url.UrlDispatchInfo;

public class BaseRequestManager {
  protected final Log log = LogFactory.getLog(getClass());

  protected static ThreadLocal<IRequest> requestHolder = new ThreadLocal<IRequest>();

  public IRequest set(UrlDispatchInfo dispatchInfo, HttpServletRequest httpRequest, HttpServletResponse httpResponse, Map<String,Object> params, boolean expectResponse) {
    IRequest request = new BaseRequest(dispatchInfo, httpRequest, httpResponse, params, expectResponse);
    if (get() != null) {
      log.warn("REQUEST ALREADY SET");
    }
    requestHolder.set(request);
    return request;
  }

  public IRequest get() {
    IRequest request = requestHolder.get();
    return request;
  }

  public void unset() {
    requestHolder.set(null);
  }
}
