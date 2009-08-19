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
package org.toobsframework.pres.url.mapping.strategy;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;
import org.toobsframework.pres.url.UrlDispatchInfo;

public class DefaultDispatchStrategy implements DispatchStrategy {

  private UrlPathHelper urlPathHelper = new UrlPathHelper();

  public UrlDispatchInfo resolveDispatchInfo(HttpServletRequest request) {
    String resourceId = resolveResouceId(request);
    String contentType = resolveContentType(request);
    return new UrlDispatchInfo(this.urlPathHelper.getLookupPathForRequest(request), resourceId, contentType);
  }

  protected String resolveResouceId(HttpServletRequest request) {
    String resourceId = (String)request.getAttribute(DISPATCH_RESOURCE_ID_ATTRIBUTE);
    if (resourceId == null) {
      String urlPath = this.urlPathHelper.getLookupPathForRequest(request);
      resourceId = WebUtils.extractFilenameFromUrlPath(urlPath);
    }
    return resourceId;
  }

  protected String resolveContentType(HttpServletRequest request) {
    String contentType = (String)request.getAttribute(DISPATCH_CONTENT_TYPE_ATTRIBUTE);
    if (contentType == null) {
      contentType = DEFAULT_CONTENT_TYPE;
    }
    return contentType;
  }

  public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
    this.urlPathHelper = urlPathHelper;
  }

}
