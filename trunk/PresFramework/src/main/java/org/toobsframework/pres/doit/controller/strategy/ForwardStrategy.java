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
package org.toobsframework.pres.doit.controller.strategy;

import java.util.Map;

import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.toobsframework.pres.doit.config.DoIt;
import org.toobsframework.util.IRequest;

public interface ForwardStrategy {

  public static final String FORWARD_NAME_PARAM = "forwardName";
  public static final String ERROR_FORWARD_NAME_PARAM = "errorForwardName";
  public static final String PERMISSION_FORWARD_SUFFIX = "PermForward";

  public static final String DEFAULT_SUCCESS_FORWARD_NAME = "success";
  public static final String DEFAULT_ERROR_FORWARD_NAME = "error";

  public static final String VALIDATION_HEADER_NAME = "toobs.error.validation";
  public static final String VALIDATION_ERROR_OBJECTS = "org.toobsframework.validationErrorObjects";
  public static final String VALIDATION_ERROR_MESSAGES = "org.toobsframework.validationErrorMessages";

  public abstract AbstractUrlBasedView resolveErrorForward(IRequest componentRequest, DoIt doIt, Map<String, Object> forwardParams, Throwable t);

  public abstract AbstractUrlBasedView resolveSuccessForward(IRequest componentRequest, DoIt doIt, Map<String, Object> forwardParams);

}