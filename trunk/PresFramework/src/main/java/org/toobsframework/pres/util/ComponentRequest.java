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
package org.toobsframework.pres.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.toobsframework.pres.component.ParallelComponent;
import org.toobsframework.pres.url.UrlDispatchInfo;
import org.toobsframework.util.BaseRequest;

public class ComponentRequest extends BaseRequest implements IComponentRequest {

  private Map<String,ParallelComponent> parallelComponents;

  public ComponentRequest(UrlDispatchInfo dispatchInfo, HttpServletRequest httpRequest, HttpServletResponse httpResponse, Map<String,Object> params, boolean expectResponse) {
    super(dispatchInfo, httpRequest, httpResponse, params, expectResponse);
  }

  public void addParallelComponent(ParallelComponent parallelComponent) {
    if (parallelComponents == null) {
      parallelComponents = new HashMap<String,ParallelComponent>(); 
    }
    this.parallelComponents.put(parallelComponent.getId(),parallelComponent);
  }

  public ParallelComponent getParallelComponent(String id) {
    if (parallelComponents == null) {
      return null;
    }
    return parallelComponents.get(id);
  }

}
