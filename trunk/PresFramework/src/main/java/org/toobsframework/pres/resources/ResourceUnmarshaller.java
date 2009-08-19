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
package org.toobsframework.pres.resources;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.Unmarshaller;
import org.springframework.core.io.Resource;
import org.toobsframework.pres.component.manager.ComponentManager;


/**
 * Boilerplate to load a spring reource into a castor unmarshaller
 * @author jaimegarza
 *
 */
public class ResourceUnmarshaller {

  private static Log log = LogFactory.getLog(ResourceUnmarshaller.class);

  /**
   * Unmarshall a resource of a given class into its corresponding object
   * @param resource - the resource that describes the file location
   * @param clazz - the class of the unmarshalled result
   * @return the unmarshalled object
   * @throws IOException
   */
  public Object unmarshall(Resource resource, Class clazz) {
    Object config = null;
    try {
      URL configFileURL = resource.getURL();
      InputStreamReader reader = new InputStreamReader(configFileURL.openStream());
      try {
        Unmarshaller unmarshaller = new Unmarshaller(clazz);
        unmarshaller.setValidation(false);
        config = unmarshaller.unmarshal(reader);
      } finally {
        reader.close();
      }
    } catch (Exception e) {
      log.warn("Configuration load failure on file " + resource.getFilename() +  ": " + e.getMessage(), e);
    }
    return config;
  }
}
