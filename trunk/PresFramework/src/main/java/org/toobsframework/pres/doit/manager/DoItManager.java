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
package org.toobsframework.pres.doit.manager;

import java.util.HashMap;
import java.util.Map;

import org.toobsframework.pres.base.ManagerBase;
import org.toobsframework.pres.doit.DoItInitializationException;
import org.toobsframework.pres.doit.config.DoIt;
import org.toobsframework.pres.doit.config.DoItConfig;

/**
 * @author sean
 */
public final class DoItManager extends ManagerBase implements IDoItManager {
  private Map<String, DoIt> registry;

  private DoItManager() throws DoItInitializationException {
    log.info("Constructing new DoItManager");
  }

  // Read from config file
  public void afterPropertiesSet() throws DoItInitializationException {
    registry = new HashMap<String, DoIt>();
    loadConfig(DoItConfig.class);
  }

  
  public DoIt getDoIt(String Id) throws DoItInitializationException {
    if (isDoReload()) {
      loadConfig(DoItConfig.class);
    }
    if (!registry.containsKey(Id)) {
      throw new DoItInitializationException("DoIt " + Id + " not found");
    }
    return registry.get(Id);
  }

  @Override
  protected void registerConfiguration(Object object, String fileName) {
    DoItConfig doItConfig = (DoItConfig) object;
    DoIt[] doIts = doItConfig.getDoIt();
    for (int i = 0; i < doIts.length; i++) {
      DoIt thisDoIt = doIts[i];
      if (registry.containsKey(thisDoIt.getName()) && !isInitDone()) {
        log.warn("Overriding doit with Id: " + thisDoIt.getName());
      }
      this.registry.put(thisDoIt.getName(), thisDoIt);
    }
  }

}