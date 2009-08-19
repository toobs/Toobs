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

import org.toobsframework.pres.url.UrlDispatchInfo;

public interface DispatchStrategy {
  public static final String DISPATCH_RESOURCE_ID_ATTRIBUTE = "org.toobsframework.pres.url.mapping.DISPATCH_RESOURCE_ID";
  public static final String DISPATCH_CONTENT_TYPE_ATTRIBUTE = "org.toobsframework.pres.url.mapping.DISPATCH_CONTENT_TYPE";
  public static final String DISPATCH_PATTERN_ATTRIBUTE = "org.toobsframework.pres.url.mapping.DISPATCH_PATTERN";
  public static final String DEFAULT_CONTENT_TYPE = "xhtml";

  public abstract UrlDispatchInfo resolveDispatchInfo(HttpServletRequest request);

}