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
package org.toobsframework.pres.component.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * Controller that transforms the virtual filename at the end of a URL
 * into a component request.  It then renders that component and dumps 
 * the result into the response.
 *
 * <p>Example: "/index" -> "index"
 * Example: "/index.comp" -> "index"
 * 
 * @author Sean
 */
public class ComponentViewController extends AbstractController {

  private static Log log = LogFactory.getLog(ComponentViewController.class);
  
  private IComponentViewHandler componentViewHandler;
  
  /**
   * 
   * Retrieves the URL path to use for lookup and delegates to
   * <code>getViewNameForUrlPath</code>.
   * 
   * @throws Exception Exception fetching or rendering component.
   * @see #getViewNameForUrlPath
   * 
   */
  public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

    return componentViewHandler.handleRequestInternal(request, response);

  }

  public IComponentViewHandler getComponentViewHandler() {
    return componentViewHandler;
  }

  public void setComponentViewHandler(IComponentViewHandler componentViewHandler) {
    this.componentViewHandler = componentViewHandler;
  }

}
