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
package org.toobsframework.pres.xsl;

import org.toobsframework.pres.component.dataprovider.api.IDataProvider;
import org.toobsframework.pres.component.manager.IComponentManager;
import org.toobsframework.pres.layout.manager.IComponentLayoutManager;
import org.toobsframework.pres.util.ComponentRequestManager;
import org.toobsframework.transformpipeline.domain.IXMLTransformerHelper;
import org.toobsframework.util.Configuration;

public class ComponentTransformerHelper implements IXMLTransformerHelper {
  protected Configuration configuration = null;
  protected ComponentRequestManager componentRequestManager = null;
  protected IComponentManager componentManager = null;
  protected IComponentLayoutManager componentLayoutManager = null;
  protected IDataProvider dataProvider;

  
  public IDataProvider getDataProvider() {
    return dataProvider;
  }

  public void setDataProvider(IDataProvider dataProvider) {
    this.dataProvider = dataProvider;
  }
  
  /**
   * @return the componentRequestManager
   */
  public ComponentRequestManager getComponentRequestManager() {
    return componentRequestManager;
  }
  
  /**
   * @param componentRequestManager the componentRequestManager to set
   */
  public void setComponentRequestManager(
      ComponentRequestManager componentRequestManager) {
    this.componentRequestManager = componentRequestManager;
  }
  
  /**
   * @return the componentManager
   */
  public IComponentManager getComponentManager() {
    return componentManager;
  }
  
  /**
   * @param componentManager the componentManager to set
   */
  public void setComponentManager(IComponentManager componentManager) {
    this.componentManager = componentManager;
  }
  
  /**
   * @return the componentLayoutManager
   */
  public IComponentLayoutManager getComponentLayoutManager() {
    return componentLayoutManager;
  }
  
  /**
   * @param componentLayoutManager the componentLayoutManager to set
   */
  public void setComponentLayoutManager(
      IComponentLayoutManager componentLayoutManager) {
    this.componentLayoutManager = componentLayoutManager;
  }

  /**
   * @return the toobs configuration
   */
  public Configuration getConfiguration() {
    return configuration;
  }

  /**
   * 
   * @param configuration the toobs configuration to set
   */
  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }

}
