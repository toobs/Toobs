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

/**
 * @author stewari
 */
public interface IDataProviderObjectProperty {

    /**
     * get parent object's unique identifier
     * @return parent object Id
     */
    public String getParentId();
    
     
    /**
     * get the property name
     * @return property name
     */
    public String getPropertyName();
     
    /**
     * get property type, e.g., simple, indexed, mapped
     * @return property type
     */
    public PropertyType getPropertyType();
    
    /**
     * get property value type
     * @return property type
     */
    public Class getValueType();
    
    /**
     * get property value
     * @return property value
     */
    public Object getPropertyValue();
    
    /**
     * Check if this is a mapped property. A mapped property is one that
     * represents a map, i.e., name-value pairs
     * @return true if this is a mapped property, false otherwise
     */
    public boolean isMapped();
    
    /**
     * Check if this is an indexed property. An indexed property is one that
     * represents a collection, i.e., list, array
     * @return true if this is an indexed property, false otherwise
     */
    public boolean isIndexed();
    
    /**
     * Check if this is a simple property.
     * @return true if this is a simple property, false otherwise
     */
    public boolean isSimple();
}
