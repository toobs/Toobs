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
package org.toobsframework.pres.app.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobsframework.pres.app.AppReader;

public class URLResolverImpl implements URLResolver {

  private static Log log = LogFactory.getLog(URLResolverImpl.class);

  public AppRequest resolve(AppReader appReader, String url, String method) {
    log.info(url);
    return null;
  }

  protected AppRequest getBaseAppView(AppReader appReader, String urlPath) {
    String[] splitUrl = urlPath.split("/");
    if (log.isDebugEnabled()) {
      for (int i = 0; i < splitUrl.length; i++) {
        log.debug("Url part " + i + ": " + splitUrl[i]);
      }
    }
    BaseAppRequest view = null;
    
    if (splitUrl.length <= 1) {
      return new BaseAppRequest("/", DEFAULT_VIEW);
    }
    if (appReader.containsApp("/" + splitUrl[1])) {
      String appName = splitUrl[1];
      if (splitUrl.length == 2) {
        return new BaseAppRequest(appName, DEFAULT_VIEW);
      } else {
        
      }
    }
    
    /*
    if (splitUrl[1].equals(compPrefix)) {
      if (splitUrl.length >= 5) {
        return new BaseAppView("/", true, splitUrl[2], splitUrl[3], splitUrl[4]);
      } else if (splitUrl.length == 4) {
        return new BaseAppView("/", true, splitUrl[2], splitUrl[3], splitUrl[2]);
      } else {
        return new BaseAppView("/", true, null, null, splitUrl[2]);
      }
    }
    if (appManager.containsApp("/" + splitUrl[1])) {
      if (splitUrl[1].equals(compPrefix)) {
        if (splitUrl.length >= 6) {
          return new BaseAppView("/" + splitUrl[1], true, splitUrl[3], splitUrl[4], splitUrl[5]);
        } else if (splitUrl.length == 4) {
          return new BaseAppView("/" + splitUrl[1], true, splitUrl[3], splitUrl[4], splitUrl[3]);
        } else {
          return new BaseAppView("/" + splitUrl[1], true, null, null, splitUrl[3]);
        }
      } else {
        if (splitUrl.length >= 5) {
          return new BaseAppView("/" + splitUrl[1], false, splitUrl[2], splitUrl[3], splitUrl[4]);
        } else if (splitUrl.length == 4) {
          return new BaseAppView("/" + splitUrl[1], false, splitUrl[2], splitUrl[3], splitUrl[2]);
        } else if (splitUrl.length == 3) {
          return new BaseAppView("/" + splitUrl[1], false, null, null, splitUrl[2]);
        } else {
          return new BaseAppView("/" + splitUrl[1], false, null, null, DEFAULT_VIEW);
        }
      }
    } else {
      if (splitUrl.length >= 4) {
        return new BaseAppView("/", true, splitUrl[1], splitUrl[2], splitUrl[3]);
      } else if (splitUrl.length == 3) {
        return new BaseAppView("/", true, splitUrl[1], splitUrl[2], splitUrl[1]);
      } else {
        return new BaseAppView("/", true, null, null, splitUrl[1]);
      }
    }
    */ 
    return view;
  }

}
