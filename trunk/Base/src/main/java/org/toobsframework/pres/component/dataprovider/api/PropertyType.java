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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.enums.Enum;

/**
 * @author stewari
 */
public class PropertyType extends Enum {
    
    public static final PropertyType SIMPLE = new PropertyType("simple");
    public static final PropertyType INDEXED = new PropertyType("indexed");
    public static final PropertyType MAPPED = new PropertyType("mapped");
    
    private PropertyType(String type) {
        super(type);
    }
    
    public static PropertyType getEnum(String type) {
        return (PropertyType) getEnum(PropertyType.class, type);
    }
    
    public static Map getEnumMap() {
        return getEnumMap(PropertyType.class);
    }
    
    public static List getEnumList() {
        return getEnumList(PropertyType.class);
    }
    
    public static Iterator iterator() {
        return iterator(PropertyType.class);
    }
}
