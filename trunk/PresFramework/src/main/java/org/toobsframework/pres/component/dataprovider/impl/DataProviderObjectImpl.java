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
package org.toobsframework.pres.component.dataprovider.impl;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobsframework.pres.component.dataprovider.api.IDataProviderObject;
import org.toobsframework.pres.component.dataprovider.api.IDataProviderObjectProperty;
import org.toobsframework.pres.component.dataprovider.api.InvalidContextException;
import org.toobsframework.pres.component.dataprovider.api.LockException;
import org.toobsframework.pres.component.dataprovider.api.NotAMappedPropertyException;
import org.toobsframework.pres.component.dataprovider.api.NotAnIndexedPropertyException;
import org.toobsframework.pres.component.dataprovider.api.ObjectSaveException;
import org.toobsframework.pres.component.dataprovider.api.PropertyNotFoundException;
import org.toobsframework.pres.component.dataprovider.api.TypeMismatchException;
import org.toobsframework.pres.component.dataprovider.api.UnlockException;
import org.toobsframework.util.BetwixtUtil;


public class DataProviderObjectImpl implements IDataProviderObject {

  private static Log log = LogFactory.getLog(DataProviderObjectImpl.class);

  private Object valueObject = null;
  private boolean isXml = false;

  private String dao = null;

  public Object getValueObject() {
    return this.valueObject;
  }

  public void setValueObject(Object vo) {
    this.valueObject = vo;
  }

  public String getId() {
    // Override in subclass
    return null;
  }

  public void lock() throws LockException {
    // Not needed since we're not implementing versioning
    log.info("lock() not implemented");
  }

  public void unlock() throws UnlockException {
    // Not needed since we're not implementing versioning
    log.info("unlock() not implemented");
  }

  public boolean isLocked() {
    // Not needed since we're not implementing versioning
    return false;
  }

  public IDataProviderObjectProperty getProperty(String propertyName)
      throws PropertyNotFoundException {

    DataProviderPropertyImpl dsProperty = null;

    try {
      PropertyDescriptor property = PropertyUtils.getPropertyDescriptor(this
          .getValueObject(), propertyName);
      dsProperty = new DataProviderPropertyImpl(property);
      dsProperty.setPropertyValue(property.getReadMethod().invoke(this,
          (Object[]) null));
    } catch (IllegalAccessException e) {
      throw new PropertyNotFoundException("Property Not found.", e);
    } catch (InvocationTargetException e) {
      throw new PropertyNotFoundException("Property Not found.", e);
    } catch (NoSuchMethodException e) {
      throw new PropertyNotFoundException("Property Not found.", e);
    }

    return dsProperty;
  }

  public IDataProviderObjectProperty[] getProperties() {
    // TODO Auto-generated method stub
    return null;
  }

  public IDataProviderObjectProperty[] getProperties(String[] propertyNames)
      throws PropertyNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

  public Object get(String propertyName) throws PropertyNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

  public Object get(String propertyName, int index)
      throws PropertyNotFoundException, NotAnIndexedPropertyException {
    // TODO Auto-generated method stub
    return null;
  }

  public Object get(String propertyName, String key)
      throws PropertyNotFoundException, NotAMappedPropertyException {
    // TODO Auto-generated method stub
    return null;
  }

  public void set(String propertyName, Object value)
      throws PropertyNotFoundException, TypeMismatchException {
    // TODO Auto-generated method stub

  }

  public void set(String propertyName, int index, Object value)
      throws PropertyNotFoundException, NotAnIndexedPropertyException,
      TypeMismatchException {
    // TODO Auto-generated method stub

  }

  public void set(String propertyName, String key, Object value)
      throws PropertyNotFoundException, NotAMappedPropertyException,
      TypeMismatchException {
    // TODO Auto-generated method stub

  }

  public IDataProviderObject[] getChildren() {
    // TODO Auto-generated method stub
    return null;
  }

  public IDataProviderObject getChild(int index) {
    // TODO Auto-generated method stub
    return null;
  }

  public Object callMethod(String methodName, Class[] parameterTypes,
      Object[] parameterValues) throws IllegalAccessException,
      IllegalArgumentException, InvocationTargetException {
    // TODO Auto-generated method stub
    return null;
  }

  public void update(Map valueMap) throws PropertyNotFoundException,
      TypeMismatchException {
    // TODO Auto-generated method stub

  }

  public void addChild(String context, IDataProviderObjectProperty[] properties)
      throws InvalidContextException {
    // TODO Auto-generated method stub

  }

  public String save() throws ObjectSaveException {
    // TODO Auto-generated method stub
    return null;
  }

  public String toXml() throws IOException {
    if (this.isXml) {
      String xmlString = (String)this.valueObject;
      int cb = xmlString.indexOf('>');
      if (cb != -1 && xmlString.charAt(cb-1) == '?') {
        return xmlString.substring(cb + 1);
      } else {
        return xmlString;
      }
    } else {
      return BetwixtUtil.toXml(this.valueObject);
    }
  }

  public String getDao() {
    return dao;
  }

  public void setDao(String dao) {
    this.dao = dao;
  }

  /**
   * returns the classname of the valueobject.
   * 
   * @return classname o the valueobject.
   */
  public String getValueObjectClassName() {
    return this.getValueObject().getClass().getSimpleName();
  }

  /**
   * returns the dao used to get the object..
   * 
   * @return value object Dao interface.
   */
  public String getValueObjectDao() {
    return this.getDao();
  }

  public void setXml(boolean isXml) {
    this.isXml = isXml;
  }

  public boolean isXml() {
    return isXml;
  }

}
