package org.toobsframework.pres.component.dataprovider.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.BeanFactory;
import org.toobsframework.data.beanutil.BeanMonkey;
import org.toobsframework.pres.component.dataprovider.api.DataProviderNotInitializedException;
import org.toobsframework.pres.component.dataprovider.api.IDataProvider;
import org.toobsframework.pres.component.dataprovider.api.IDataProviderObject;
import org.toobsframework.pres.component.dataprovider.api.InvalidContextException;
import org.toobsframework.pres.component.dataprovider.api.InvalidSearchContextException;
import org.toobsframework.pres.component.dataprovider.api.InvalidSearchFilterException;
import org.toobsframework.pres.component.dataprovider.api.ObjectCreationException;
import org.toobsframework.pres.component.dataprovider.api.ObjectNotFoundException;
import org.toobsframework.pres.component.dataprovider.api.PropertyNotFoundException;
import org.toobsframework.pres.component.dataprovider.api.TypeMismatchException;
import org.toobsframework.servlet.ContextHelper;

public class DefaultDataProviderImpl implements IDataProvider {
  protected static BeanFactory beanFactory;

  static {
    beanFactory = ContextHelper.getWebApplicationContext();
  }
  
  private Object getServiceProvider(String serviceProviderName) {
    return beanFactory.getBean(serviceProviderName);
  }

  public Object dispatchAction(String action, String dao, String objectType,
      String returnObjectType, String guidParam, String permissionContext,
      String indexParam, String namespace, Map<String, Object> params,
      Map<String, Object> outParams) throws Exception {
    Object returnObj = null;
    //Get the guid. If there is one.
    String guid = null;
    if(params.get(guidParam) != null && params.get(guidParam).getClass().isArray()) {
      guid = ((String[]) params.get(guidParam))[0];
    } else {
      guid = (String) params.get(guidParam);
    }        
    //Run action.
    if (action == null) {
      throw new ObjectNotFoundException("action was not provided for the dispatch");
    }
    /*if (action.equalsIgnoreCase("update")) {
      returnObj = this.updateObject(objectType, dao, returnObjectType, guid, permissionContext, namespace, params, outParams);
    } else if (action.equalsIgnoreCase("updateCollection")) {
      returnObj = this.updateObjectCollection(objectType, dao, returnObjectType, guid, permissionContext, indexParam, namespace, params, outParams);
    } else if (action.equalsIgnoreCase("create")) {
      returnObj = this.createObject(objectType, dao, returnObjectType, permissionContext, namespace, params, outParams);
    } else if (action.equalsIgnoreCase("createCollection")) {
      returnObj = this.createObjectCollection(objectType, dao, returnObjectType, permissionContext, indexParam, namespace, params, outParams);
    } else if (action.equalsIgnoreCase("get")) {
      returnObj = this.getObject(returnObjectType, dao, "", guid, params, outParams);
    } else if (action.equalsIgnoreCase("delete")) {
      returnObj = this.deleteObject(dao, guid, permissionContext, namespace, params, outParams);
    } else {
      returnObj = null; //this.callAction(objectType, action, dao, guid, permissionContext, namespace, params, outParams);
    }*/
  
    return returnObj;
  }

  public IDataProviderObject getObject(String returnObjectType, String serviceProviderName, String propertyName,
      String objectId, Map<String, Object> params, Map<String, Object> outParams)
      throws ObjectNotFoundException, DataProviderNotInitializedException {
    
    try {
      Object bean = getServiceProvider(serviceProviderName);
      Object value = BeanMonkey.getPropertyValue(bean, propertyName);
      DataProviderObjectImpl valueObjectReturn = new DataProviderObjectImpl();
      valueObjectReturn.setValueObject(value);
      return valueObjectReturn;
    } catch (Exception e) {
      throw new ObjectNotFoundException("Cannot load object: " + e.getMessage(), e);
    }
  }

  public Collection<IDataProviderObject> search(String returnValueObject,
      String dao, String searchCriteria, String searchMethod,
      String permissionAction, Map<String, Object> params,
      Map<String, Object> outParams) throws ObjectCreationException,
      InvalidSearchContextException, InvalidSearchFilterException,
      DataProviderNotInitializedException {
    // TODO Auto-generated method stub
    return null;
  }

  public Collection<IDataProviderObject> searchIndex(String returnValueObject,
      String dao, String searchCriteria, String searchMethod,
      String permissionAction, Map<String, Object> params,
      Map<String, Object> outParams) throws ObjectCreationException,
      InvalidSearchContextException, InvalidSearchFilterException,
      DataProviderNotInitializedException {
    // TODO Auto-generated method stub
    return null;
  }

}
