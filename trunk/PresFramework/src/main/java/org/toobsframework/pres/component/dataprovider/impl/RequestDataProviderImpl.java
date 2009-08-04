/*
 * Created by IntelliJ IDEA.
 * User: spudney
 * Date: Oct 6, 2008
 * Time: 3:07:53 PM
 */
package org.toobsframework.pres.component.dataprovider.impl;

import org.springframework.beans.factory.BeanFactory;
import org.toobsframework.pres.component.dataprovider.api.*;
import org.toobsframework.pres.util.ComponentRequestManager;
import org.toobsframework.servlet.ContextHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestDataProviderImpl implements IDataProvider {
  private Log log = LogFactory.getLog(RequestDataProviderImpl.class);
  
  private static BeanFactory beanFactory;
  private static ComponentRequestManager componentRequestManager;

  static {
    beanFactory = ContextHelper.getWebApplicationContext();
    componentRequestManager = (ComponentRequestManager)beanFactory.getBean("componentRequestManager");
  }

  public IDataProviderObject getObject(String objectType, String objectDao, String propertyName,
      String objectId, Map params, Map outParams) throws ObjectNotFoundException,
      DataProviderNotInitializedException {

    if(componentRequestManager == null || componentRequestManager.get() == null) {
      throw new DataProviderNotInitializedException("Component Request Manager or Component Request is null.");
    }

    Object retObj = null;
    try {
      retObj = componentRequestManager.get().getParam(objectDao);
    } catch (Exception e) {
        throw new ObjectNotFoundException("Error getting object:" + objectType + ":" + objectId, e);
    }

    // Prepare Result
    if(retObj == null) {
      throw new ObjectNotFoundException("Error getting object:" + objectType + ":" + objectId);
    } else if(retObj instanceof DataProviderObjectImpl) {
      return (IDataProviderObject) retObj;
    } else {
      DataProviderObjectImpl dsObj = new DataProviderObjectImpl();
      dsObj.setValueObject(retObj);
      return dsObj;
    }
  }



  public Boolean deleteObject(String dao, String objectId, String permissionContext, String namespace, Map params, Map outParams) throws ObjectNotFoundException, DataProviderNotInitializedException {
    throw new ObjectNotFoundException("This Datasource does not support this action at this time.");
  }

  public IDataProviderObject updateObject(String objectType, String objectDao, String returnObjectType, String objectId, String permissionContext, String namespace, Map valueMap, Map outParams) throws ObjectNotFoundException, PropertyNotFoundException, TypeMismatchException, DataProviderNotInitializedException {
    throw new ObjectNotFoundException("This Datasource does not support this action at this time.");
  }

  public IDataProviderObject updateObjectCollection(String objectType, String objectDao, String returnObjectType, String objectId, String permissionContext, String namespace, String indexParam, Map valueMap, Map outParams) throws ObjectNotFoundException, PropertyNotFoundException, TypeMismatchException, DataProviderNotInitializedException {
    throw new ObjectNotFoundException("This Datasource does not support this action at this time.");
  }

  public IDataProviderObject createObject(String objectType, String objectDao, String returnObjectType, String permissionContext, String namespace, Map params, Map outParams) throws ObjectCreationException, DataProviderNotInitializedException, InvalidContextException {
    throw new ObjectCreationException("This Datasource does not support this action at this time.");
  }

  public IDataProviderObject createObjectCollection(String objectType, String objectDao, String returnObjectType, String permissionContext, String indexParam, String namespace, Map params, Map outParams) throws ObjectCreationException, DataProviderNotInitializedException, InvalidContextException {
    throw new ObjectCreationException("This Datasource does not support this action at this time.");
  }

  public Collection search(String returnValueObject, String dao, String searchCriteria, String searchMethod, String permissionAction, Map params, Map outParams) throws ObjectCreationException, InvalidSearchContextException, InvalidSearchFilterException, DataProviderNotInitializedException {
    throw new ObjectCreationException("This Datasource does not support this action at this time.");
  }

  public Collection searchIndex(String returnValueObject, String dao, String searchCriteria, String searchMethod, String permissionAction, Map params, Map outParams) throws ObjectCreationException, InvalidSearchContextException, InvalidSearchFilterException, DataProviderNotInitializedException {
    throw new ObjectCreationException("This Datasource does not support this action at this time.");
  }

  public Object dispatchAction(String action, String dao, String objectType, String returnObjectType, String guidParam, String permissionContext, String indexParam, String namespace, Map params, Map outParams) throws Exception {
    throw new ObjectNotFoundException("This Datasource does not support this action at this time.");
  }

  public Object dispatchActionEx(HttpServletRequest request, HttpServletResponse response, String action, String dao, String objectType, String returnObjectType, String guidParam, String permissionContext, String indexParam, String namespace, Map params, Map outParams) throws Exception {
	    throw new ObjectNotFoundException("This Datasource does not support this action at this time.");
	  }

  public ComponentRequestManager getComponentRequestManager() {
    return componentRequestManager;
  }

  public void setComponentRequestManager(
      ComponentRequestManager componentRequestManager) {
    this.componentRequestManager = componentRequestManager;
  }
  

}