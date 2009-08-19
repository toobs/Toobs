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
package org.toobsframework.pres.url;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UrlDispatchInfo {

  private final String originalPath;
  private final String resourceId;
  private final String contentType;
  private final Map<String,Object> pathParameterMap;

  private boolean error;

  public UrlDispatchInfo(String originalPath, String resourceId, String contentType) {
    this.originalPath = originalPath;
    this.resourceId = resourceId;
    this.contentType = contentType;
    this.pathParameterMap = new HashMap<String,Object>();;
  }

  public UrlDispatchInfo(String originalPath, String resourceId, String contentType, Map<String,Object> pathParameterMap) {
    this.originalPath = originalPath;
    this.resourceId = resourceId;
    this.contentType = contentType;
    this.pathParameterMap = pathParameterMap;
  }

  public String getResourceId() {
    return resourceId;
  }

  public Map<String,Object> getPathParameterMap() {
    return Collections.unmodifiableMap( pathParameterMap );
  }

  public String getOriginalPath() {
    return originalPath;
  }

  public String getContentType() {
    return contentType;
  }

  public void setError(boolean error) {
    this.error = error;
  }

  public boolean isError() {
    return error;
  }

}
