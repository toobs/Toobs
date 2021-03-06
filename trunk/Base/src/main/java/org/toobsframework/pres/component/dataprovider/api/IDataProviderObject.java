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

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.io.IOException;

/**
 * @author stewari
 */
public interface IDataProviderObject {
    
    /**
     * Get the unique identifier for this object
     * @return
     */
    public String getId();
    
    /**
     * Lock this object
     *
     */
    public void lock() throws LockException;
    
    /**
     * Unlock this object
     *
     */
    public void unlock() throws UnlockException;
    
    /**
     * Query this objects lock status
     * @return lock status
     */
    public boolean isLocked();
    
    /**
     * get the specified property
     * @param property name
     * @return specified property
     */
    public IDataProviderObjectProperty getProperty(String propertyName)
        throws PropertyNotFoundException;
    
    /**
     * get all the properties of this object
     * @return properties
     */
    public IDataProviderObjectProperty[] getProperties();
    
    
    /**
     * get the specified properties of this object
     * @param propertyNames
     * @return
     */
    public IDataProviderObjectProperty[] getProperties(String[] propertyNames)
        throws PropertyNotFoundException;
    
    /**
     * Return the value of a simple property specified by the name
     * @param propertyName
     * @return value of the specified property
     * @throws PropertyNotFoundException
     */
    public Object get(String propertyName)
        throws PropertyNotFoundException;
     
    
    /**
     * Return the value of an indexed property specified by the name
     * @param propertyName
     * @param index
     * @return value of the specified property
     * @throws PropertyNotFoundException, NotAnIndexedPropertyException
     */
    public Object get(String propertyName, int index)
        throws PropertyNotFoundException, NotAnIndexedPropertyException;
    
    /**
     * Return the value of a mapped property specified by the name
     * @param propertyName
     * @param key
     * @return value of the specified property
     * @throws PropertyNotFoundException, NotAMappedPropertyException
     */
    public Object get(String propertyName, String key)
        throws PropertyNotFoundException, NotAMappedPropertyException;
    
    
    /**
     * Set the value of a simple property
     * @param propertyName
     * @param value
     * @throws PropertyNotFoundException, TypeMismatchException
     */
    public void set(String propertyName, Object value)
        throws PropertyNotFoundException, TypeMismatchException;
    
    /**
     * Set the value of an indexed property
     * @param propertyName
     * @param index
     * @param value
     * @throws PropertyNotFoundException, NotAnIndexedPropertyException, TypeMismatchException
     */
    public void set(String propertyName, int index, Object value)
        throws PropertyNotFoundException, NotAnIndexedPropertyException, TypeMismatchException;
    
    /**
     * Set the value of a mapped property
     * @param propertyName
     * @param key
     * @param value
     * @throws PropertyNotFoundException, NotAMappedPropertyException, TypeMismatchException
     */
    public void set(String propertyName, String key, Object value)
        throws PropertyNotFoundException, NotAMappedPropertyException, TypeMismatchException;
    
    /**
     * get the children of this object
     * @return children of this object
     */
    public IDataProviderObject[] getChildren();
    
    /**
     * get the child at the specified index
     * @param index
     * @return child at the specified index
     */
    public IDataProviderObject getChild(int index);
    
    /**
     * Call the named method on the object implementing this interface
     * parameterTypes and parameterValues are parallel arrays
     * @param methodName
     * @param parameterTypes
     * @param parameterValues
     * @return result of the method call
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public Object callMethod(String methodName, Class[] parameterTypes, Object[] parameterValues)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
    
    /**
     * Update the specified simple properties.
     * @param values - property name/value pairs
     */
    public void update(Map valueMap)
        throws PropertyNotFoundException, TypeMismatchException;
    
    /**
     * add a child
     * @param context
     * @param properties
     * @throws InvalidContextException
     */
    public void addChild(String context, IDataProviderObjectProperty[] properties)
		throws InvalidContextException;
    
    
    /**
     * saves this object in the datasource
     * should be called only for root objects
     * @return the Id of the saved object
     * @throws ObjectSaveException
     */
    public String save() throws ObjectSaveException;
    
    /**
     * returns this object as xml
     * @return xml corresponding to this object
     */
    public String toXml() throws IOException;

    /**
     * returns the classname of the valueobject.
     * @return classname o the valueobject.
     */
    public String getValueObjectClassName();

    /**
     * returns the dao used to get the object..
     * @return value object Dao interface.
     */
    public String getValueObjectDao();
    
    public Object getValueObject();
}
