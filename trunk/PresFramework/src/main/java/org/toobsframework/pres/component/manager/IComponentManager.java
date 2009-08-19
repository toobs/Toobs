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
package org.toobsframework.pres.component.manager;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.toobsframework.exception.ParameterException;
import org.toobsframework.pres.component.ComponentException;
import org.toobsframework.pres.component.ComponentInitializationException;
import org.toobsframework.pres.component.ComponentNotFoundException;
import org.toobsframework.pres.component.ComponentNotInitializedException;
import org.toobsframework.transformpipeline.domain.IXMLTransformerHelper;
import org.toobsframework.util.IRequest;

public interface IComponentManager {

  public abstract org.toobsframework.pres.component.Component getComponent(String Id) throws ComponentNotFoundException,
      ComponentInitializationException;

  public String renderComponent(
      IRequest request,
      org.toobsframework.pres.component.Component component,
      String contentType, 
      Map<String, Object> params, 
      Map<String, Object> paramsOut, 
      IXMLTransformerHelper transformerHelper, 
      boolean appendUrlScanner)
      throws ComponentNotInitializedException, ComponentException, ParameterException, IOException;

  public void addConfigFiles(List<String> configFiles);
}