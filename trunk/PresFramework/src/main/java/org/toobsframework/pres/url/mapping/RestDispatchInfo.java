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
package org.toobsframework.pres.url.mapping;

import java.util.Map;

import org.toobsframework.pres.url.UrlMapping;

public class RestDispatchInfo {

  private UrlMapping mapping;
  private Map<String, String> params;
  private String resourceId;

  public RestDispatchInfo(UrlMapping mapping, Map<String, String> params) {
    this.mapping = mapping;
    this.params = params;
    init();
  }

  private void init() {
    if (mapping.getLayoutId() != null) {
      resourceId = mapping.getLayoutId();
    } else if (mapping.getComponentId() != null) {
      resourceId = mapping.getComponentId();
    } else {
      resourceId = mapping.getDoItId();
    }
  }

  public UrlMapping getMapping() {
    return mapping;
  }

  public void setMapping(UrlMapping mapping) {
    this.mapping = mapping;
  }

  public Map<String, String> getParams() {
    return params;
  }

  public void setParams(Map<String, String> params) {
    this.params = params;
  }

  public String getResourceId() {
    return resourceId;
  }

  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
  }

}
