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

import java.util.StringTokenizer;

public class UrlMappingUtil {

  private static final String[] NO_STRINGS = new String[0];

  /**
   * Break a pattern into its components, separated by '/'
   * @param pathPattern is the pattern
   * @return a string of parts
   */
  public static String[] tokenizePath(String pathPattern) {
    String[] tokens;
    if (pathPattern != null) {
      StringTokenizer tok = new StringTokenizer(pathPattern, "/");
      tokens = new String[tok.countTokens()];
      int i = 0;
      while (tok.hasMoreTokens()) {
        tokens[i++] = tok.nextToken();
      }
    } else {
      tokens = NO_STRINGS;
    }
    return tokens;
  }


}
