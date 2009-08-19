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

import org.toobsframework.exception.BaseException;


public class DispatchException extends BaseException {

  private static final long serialVersionUID = 1L;

  private String uri;

  public DispatchException(String uri) {
    super("Dispatch failed for uri " + uri);
    this.uri = uri;
  }

  public DispatchException(String uri, Throwable cause) {
    super("Dispatch failed for uri " + uri + " : " + cause.getMessage(), cause);
    this.uri = uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getUri() {
    return uri;
  }

}
