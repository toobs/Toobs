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
package org.toobsframework.social.session;

import java.util.Map;

public class SessionBase {
  protected String getParameter(Map<String, Object>map, String key) {
    Object o = map.get(key);
    
    if (o == null) {
      return null;
    }
    
    if (o.getClass().isArray()) {
      Object[] oa = (Object[]) o;
      if (oa.length == 0) {
        return null;
      }
      return oa[0].toString();
    } else {
      return o.toString();
    }
  }

}