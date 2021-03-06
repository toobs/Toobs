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

import javax.servlet.http.Cookie;

import org.toobsframework.util.IRequest;

public class CookieUtil implements INamespaceParameterHelper {

  static final String VALUE = "value:";
  static final String VERSION = "version:";
  static final String SECURE = "secure:";
  static final String PATH = "path:";
  static final String MAX_AGE = "maxAge:";
  static final String DOMAIN = "domain:";
  static final String COMMENT = "comment:";
  public static final String NAMESPACE = "cookie:";
  public static final int LEN_VALUE = VALUE.length();
  public static final int LEN_VERSION = VERSION.length(); 
  public static final int LEN_SECURE = SECURE.length();
  public static final int LEN_PATH = PATH.length();
  public static final int LEN_MAX_AGE = MAX_AGE.length();
  public static final int LEN_DOMAIN = DOMAIN.length();
  public static final int LEN_COMMENT = COMMENT.length();
  public static final int LEN_NAMESPACE = NAMESPACE.length();
  

  Cookie findCookie(IRequest request, String name) {
    Cookie[] cookies = request.getHttpRequest().getCookies();
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals(name)) {
        return cookie;
      }
    }
    return null;
  }
  
  public boolean supports(String tagName) {
    return tagName.startsWith(NAMESPACE);
  }
  
  public String[] getValue(IRequest request, String tagName) {
    String[] ret = new String[]{""};

    if (!tagName.startsWith(NAMESPACE)) {
      return ret;
    }

    String cookieName = tagName.substring(LEN_NAMESPACE);
    if (cookieName.startsWith(VALUE)) {
      Cookie cookie = findCookie(request, cookieName.substring(LEN_VALUE));
      if (cookie != null) {
        ret = new String[] {cookie.getValue()};
      }
    } else if (cookieName.startsWith(COMMENT)) {
      Cookie cookie = findCookie(request, cookieName.substring(LEN_COMMENT));
      if (cookie != null) {
        ret = new String[] {cookie.getComment()};
      }
    } else if (cookieName.startsWith(DOMAIN)) {
      Cookie cookie = findCookie(request, cookieName.substring(LEN_DOMAIN));
      if (cookie != null) {
        ret = new String[] {cookie.getDomain()};
      }
    } else if (cookieName.startsWith(MAX_AGE)) {
      Cookie cookie = findCookie(request, cookieName.substring(LEN_MAX_AGE));
      if (cookie != null) {
        ret = new String[] {String.valueOf(cookie.getMaxAge())};
      }
    } else if (cookieName.startsWith(PATH)) {
      Cookie cookie = findCookie(request, cookieName.substring(LEN_PATH));
      if (cookie != null) {
        ret = new String[] {cookie.getPath()};
      }
    } else if (cookieName.startsWith(SECURE)) {
      Cookie cookie = findCookie(request, cookieName.substring(LEN_SECURE));
      if (cookie != null) {
        ret = new String[] {String.valueOf(cookie.getSecure())};
      }
    } else if (cookieName.startsWith(VERSION)) {
      Cookie cookie = findCookie(request, cookieName.substring(LEN_VERSION));
      if (cookie != null) {
        ret = new String[] {String.valueOf(cookie.getVersion())};
      }
    } else {
      Cookie cookie = findCookie(request, cookieName);
      if (cookie != null) {
        ret = new String[] {cookie.getValue()};
      }
    }
    return ret;
  }
}
