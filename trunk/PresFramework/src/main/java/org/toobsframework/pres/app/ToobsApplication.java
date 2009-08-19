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
package org.toobsframework.pres.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.toobsframework.pres.component.Component;
import org.toobsframework.pres.doit.config.DoIt;
import org.toobsframework.pres.layout.RuntimeLayout;

public class ToobsApplication {

  private String root;
  private String name;
  private List<String> xslLocations;
  private Map<String,Component> components;
  private Map<String,RuntimeLayout> layouts;
  private Map<String,DoIt> doits;

  public String getRoot() {
    return root;
  }
  public void setRoot(String root) {
    this.root = root;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public List<String> getXslLocations() {
    return xslLocations;
  }
  public void setXslLocations(List<String> xslLocations) {
    this.xslLocations = xslLocations;
  }
  public void addXslLocation(String xslLocation) {
    if (this.xslLocations == null) {
      this.xslLocations = new ArrayList<String>();
    }
    this.xslLocations.add(xslLocation);
  }
  public Map<String, Component> getComponents() {
    return components;
  }
  public void setComponents(Map<String, Component> components) {
    this.components = components;
  }
  public Map<String, RuntimeLayout> getLayouts() {
    return layouts;
  }
  public void setLayouts(Map<String, RuntimeLayout> layouts) {
    this.layouts = layouts;
  }
  public Map<String, DoIt> getDoits() {
    return doits;
  }
  public void setDoits(Map<String, DoIt> doits) {
    this.doits = doits;
  }

  
}
