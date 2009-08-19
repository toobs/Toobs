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
package org.toobsframework.pres.component.dataprovider.api;

import java.util.Map;

import org.toobsframework.util.IRequest;

public class DispatchContextFactory {
  public static DispatchContext createDispatchContext(String action, String guid, String permissionContext, String namespace, Map<String, Object> inputParameters, Map<String, Object> outputParameters) {
    DispatchContext context = new DispatchContext();
    context.setAction(action);
    context.setGuid(guid);
    context.setPermissionContext(permissionContext);
    context.setNamespace(namespace);
    context.setInputParameters(inputParameters);
    context.setOutputParameters(outputParameters);
    return context;
  }

  public static DispatchContextEx createDispatchContextEx(IRequest request, String action, String guid, String permissionContext, String namespace, Map<String, Object> inputParameters, Map<String, Object> outputParameters) {
    DispatchContextEx context = new DispatchContextEx();
    context.setRequest(request);
    context.setAction(action);
    context.setGuid(guid);
    context.setPermissionContext(permissionContext);
    context.setNamespace(namespace);
    context.setInputParameters(inputParameters);
    context.setOutputParameters(outputParameters);
    return context;
  }
}
