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
package org.toobsframework.pres.base;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.toobsframework.pres.resources.IResourceCacheLoader;
import org.toobsframework.pres.resources.ResourceCacheDescriptor;
import org.toobsframework.pres.resources.ResourceUnmarshaller;
import org.toobsframework.util.Configuration;

/**
 * Common ancestor for the managers
 * @author jaimeg@yahoo-inc.com
 *
 */
public abstract class ManagerBase implements ApplicationContextAware, InitializingBean {
  protected final Log log = LogFactory.getLog(getClass());

  private static boolean initDone = false;
  private List<String> configFiles = null;
  private ResourceCacheDescriptor[] resourceCache = null;
  private boolean doReload = false;

  protected ApplicationContext applicationContext;
  protected Configuration configuration;

  public void afterPropertiesSet() throws Exception {
    doReload = configuration.doReload();
  }

  protected void initCache() {
    int l = configFiles.size();
    resourceCache = new ResourceCacheDescriptor[l];
    for(int fileCounter = 0; fileCounter < l; fileCounter++) {
      String fileSpec = configFiles.get(fileCounter);
      try {
        if (log.isDebugEnabled()) {
          log.debug("Checking Configuration file spec: " + fileSpec);
        }
        resourceCache[fileCounter] = new ResourceCacheDescriptor(applicationContext, fileSpec);
      } catch (Exception ex) {
        log.error("ComponentLayout initialization failed " + ex.getMessage(), ex);
      }
    }
  }

  public void loadConfig(final Class<?> clazz) {
    final ResourceUnmarshaller unmarshaller = new ResourceUnmarshaller();
    if(configFiles == null) {
      return;
    }
    if (resourceCache == null) {
      initCache();
    }

    for(int fileCounter = 0; fileCounter < resourceCache.length; fileCounter++) {
      String fileSpec = configFiles.get(fileCounter);
      try {
        if (log.isDebugEnabled()) {
          log.debug("Checking Configuration file spec: " + fileSpec);
        }
        Resource[] resources = null;
        if (doReload) {
          resources = resourceCache[fileCounter].checkIfModified();
          // This call is only done if doReload is desired.  This call can be
          // low performance so it is only good for development
          if (resources == null) {
            continue;
          }
        }
        resourceCache[fileCounter].load(resources, doReload, new IResourceCacheLoader() {
          public void load(Resource resource) throws IOException {
            Object object = unmarshaller.unmarshall(resource, clazz);
            registerConfiguration(object, resource.getFilename());
          }
        });
      } catch (Exception ex) {
        log.error("ComponentLayout initialization failed " + ex.getMessage(), ex);
      }
    }
    initDone = true;
  }
  
  /**
   * Implement this method to register a resource found by the loadResources method
   * @param object - is the object of the class specified in the loadConfig call
   * @param fileName - is the name where the resource was found
   */
  protected abstract void registerConfiguration(Object object, String fileName);

  /**
   * @return the initDone
   */
  public static boolean isInitDone() {
    return initDone;
  }

  /**
   * @param initDone the initDone to set
   */
  public static void setInitDone(boolean initDone) {
    ManagerBase.initDone = initDone;
  }

  protected boolean isDoReload() {
    return configuration.doReload();
  }

  protected boolean useTranslets() {
    return configuration.useTranslets();
  }

  protected boolean useChain() {
    return configuration.useChain();
  }


  public List<String> getConfigFiles() {
    return configFiles;
  }

  public void setConfigFiles(List<String> configFiles) {
    this.configFiles = configFiles;
  }

  public void addConfigFiles(List<String> configFiles) {
    this.configFiles.addAll(configFiles);
  }

  public void addConfigFile(String configFile) {
    this.configFiles.add(configFile);
  }

  public void insertConfigFile(String configFile) {
    this.configFiles.add(0, configFile);
  }

  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

   public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }

}
