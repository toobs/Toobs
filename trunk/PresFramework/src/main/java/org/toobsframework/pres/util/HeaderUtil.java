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

import org.toobsframework.util.IRequest;

public class HeaderUtil implements INamespaceParameterHelper {

  private static final String NAMESPACE = "header:";
  public static final int LEN_NAMESPACE = NAMESPACE.length();

  public boolean supports(String tagName) {
    return tagName.startsWith(NAMESPACE);
  }

  public String[] getValue(IRequest request, String tagName) {
    String[] ret = new String[]{""};

    if (!tagName.startsWith(NAMESPACE)) {
      return ret;
    }

    String headerName = tagName.substring(LEN_NAMESPACE);
    String value = request.getHttpRequest().getHeader(headerName);
    if (value != null) {
      ret = new String[] {value};
    }
    return ret;
  }


}
