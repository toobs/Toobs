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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobsframework.biz.scriptmanager.IScriptManager;
import org.toobsframework.pres.component.dataprovider.api.DataProviderNotInitializedException;
import org.toobsframework.pres.component.dataprovider.api.IDataProvider;
import org.toobsframework.pres.component.dataprovider.api.IDataProviderObject;
import org.toobsframework.pres.component.dataprovider.api.InvalidContextException;
import org.toobsframework.pres.component.dataprovider.api.ObjectCreationException;
import org.toobsframework.pres.component.dataprovider.api.ObjectNotFoundException;
import org.toobsframework.pres.component.dataprovider.api.PropertyNotFoundException;
import org.toobsframework.pres.component.dataprovider.api.TypeMismatchException;
import org.toobsframework.pres.util.ParameterUtil;
import org.toobsframework.util.IRequest;
import org.toobsframework.util.constants.PlatformConstants;


@SuppressWarnings("unchecked")
public class RhinoDataProviderImpl implements IDataProvider {

  private Log log = LogFactory.getLog(RhinoDataProviderImpl.class);
  
  private IScriptManager scriptManager;
  
  public IDataProviderObject getObject(String objectType, String objectDao, String propertyName,
      String objectId, Map params, Map outParams) throws ObjectNotFoundException,
      DataProviderNotInitializedException {
    DataProviderObjectImpl dsObj = new DataProviderObjectImpl();
    try {
      // Get Object
      params.put("guid", objectId);
      if(params.get("personId") == null) {
        params.put("personId", PlatformConstants.PERSON_ID_NO_PERSON);
      }
      Object retObj = this.getScriptManager().runScript("get", objectDao, objectType, params, outParams);
  
      // Prepare Result
      dsObj.setValueObject(retObj);
  
    } catch (Exception e) {
      throw new ObjectNotFoundException("Error getting object:" + objectType + 
          ":" + objectId, e);
    }
    return dsObj;
  }
  
  public Collection search(String returnType, String objectDao,
      String searchCriteria, String searchMethod, String permissionAction, Map params, Map outParams) throws ObjectCreationException {
    ArrayList retObjects = new ArrayList();
  
    // Get Object
    params.put("searchMethod", searchMethod);
    params.put("searchCriteria", searchCriteria);
    params.put("permissionAction", permissionAction);
    params.put("returnType", returnType);
    if(params.get("personId") == null) {
      params.put("personId", PlatformConstants.PERSON_ID_NO_PERSON);
    }
    try {
      // Get Object
      ArrayList objects =  (ArrayList) this.getScriptManager().runScript("search", objectDao, searchMethod, params, outParams);
      //Massage Objects.
      Iterator it = objects.iterator();
      while(it.hasNext()){
        DataProviderObjectImpl dsObj = new DataProviderObjectImpl();
        dsObj.setValueObject(it.next());
        retObjects.add(dsObj);
      }
  
    } catch (Exception e) {
      throw new ObjectCreationException("Error searching for objects.", e);
    }
  
    return retObjects;
  }
  
  public Collection searchIndex(String returnType, String objectDao,
      String searchCriteria, String searchMethod, String permissionAction, Map params, Map outParams) throws ObjectCreationException {
    ArrayList retObjects = new ArrayList();
  
    // Get Object
    params.put("dao", ParameterUtil.resolveParam(null, objectDao, params));
    params.put("searchMethod", searchMethod);
    params.put("searchCriteria", searchCriteria);
    params.put("permissionAction", permissionAction);
    params.put("returnType", returnType);
    if(params.get("personId") == null) {
      params.put("personId", PlatformConstants.PERSON_ID_NO_PERSON);
    }
    try {
      // Get Object
      ArrayList objects =  (ArrayList) this.getScriptManager().runScript("searchIndex", objectDao, searchMethod, params, outParams);
      if (objects != null) {
        //Massage Objects.
        Iterator it = objects.iterator();
        while(it.hasNext()){
          DataProviderObjectImpl dsObj = new DataProviderObjectImpl();
          dsObj.setValueObject(it.next());
          retObjects.add(dsObj);
        }
      }
    } catch (Exception e) {
      throw new ObjectCreationException("Error searching for objects.", e);
    }
  
    return retObjects;
  }
  
  public Boolean deleteObject(String objectDao, String objectId, String permissionContext, String namespace, Map params, Map outParams)
      throws ObjectNotFoundException, DataProviderNotInitializedException {
    Boolean retObj;
  
    params.put("guid", objectId);
    params.put("namespace", namespace);
    params.put("permissionContext", permissionContext);
    if(params.get("personId") == null) {
      params.put("personId", PlatformConstants.PERSON_ID_NO_PERSON);
    }
    params.put("modifier", params.get("personId"));
    params.put("lastModified", new Date());
  
    try {
      retObj = (Boolean) this.getScriptManager().runScript("delete", objectDao, objectDao, params, outParams);
    } catch (Exception e) {
      throw new ObjectNotFoundException("Error deleteing object(" + objectDao + ":" + objectId + ")",
          e);
    }
    return retObj;
  }
  
  public Boolean purgeObject(String objectDao, String objectId, String permissionContext, String namespace, Map params, Map outParams)
      throws ObjectNotFoundException, DataProviderNotInitializedException {
    Boolean retObj;
    
    params.put("guid", objectId);
    params.put("namespace", namespace);
    params.put("permissionContext", permissionContext);
    if(params.get("personId") == null) {
      params.put("personId", PlatformConstants.PERSON_ID_NO_PERSON);
    }
    params.put((namespace != null && namespace.length()>0 ? namespace + "." : "") + "modifier", params.get("personId"));
    params.put((namespace != null && namespace.length()>0 ? namespace + "." : "") + "lastModTs", new Date());
    
    try {
      retObj = (Boolean) this.getScriptManager().runScript("purge", objectDao, objectDao, params, outParams);
    } catch (Exception e) {
      throw new ObjectNotFoundException("Error deleteing object(" + objectDao + ":" + objectId + ")",
          e);
    }
    return retObj;
  }
  
  public IDataProviderObject updateObject(String objectType, String objectDao,
      String returnObjectType, String objectId, String permissionContext, String namespace, Map valueMap, Map outParams) throws ObjectNotFoundException,
      PropertyNotFoundException, TypeMismatchException,
      DataProviderNotInitializedException {
    DataProviderObjectImpl dsObj = new DataProviderObjectImpl();
    try {
  
      valueMap.put("guid", objectId);
      valueMap.put("namespace", namespace);
      valueMap.put("returnObjectType", returnObjectType);
      valueMap.put("permissionContext", permissionContext);
      if(valueMap.get("personId") == null) {
        valueMap.put("personId", PlatformConstants.PERSON_ID_NO_PERSON);
      }
      if(valueMap.get("modifier") == null) {
        valueMap.put("modifier", valueMap.get("personId"));
      }
      valueMap.put((namespace != null && namespace.length()>0 ? namespace + "." : "") + "lastModTs", new Date());
      if (objectId == null || objectId.equals("")) {
        valueMap.put((namespace != null && namespace.length()>0 ? namespace + "." : "") + "createdOn", new Date());
        if(valueMap.get("creator") == null) {
          valueMap.put((namespace != null && namespace.length()>0 ? namespace + "." : "") + "creator", valueMap.get("personId"));
        }
      }
  
  
      Object retObj = this.getScriptManager().runScript("update", objectDao, objectType, valueMap, outParams);
      dsObj.setValueObject(retObj);
      
    } catch (Exception e) {
      throw new ObjectNotFoundException("Error updating object.", e);
    }
    return dsObj;
  }
  
  public IDataProviderObject updateObjectCollection(String objectType, String objectDao,
      String returnObjectType, String objectId, String permissionContext, String indexParam, String namespace, Map valueMap, Map outParams) throws ObjectNotFoundException,
      PropertyNotFoundException, TypeMismatchException,
      DataProviderNotInitializedException {
    DataProviderObjectImpl dsObj = new DataProviderObjectImpl();
    try {
  
      valueMap.remove("guid");
      valueMap.put("namespace", namespace);
      valueMap.put("returnObjectType", returnObjectType);
      valueMap.put("permissionContext", permissionContext);
      valueMap.put("indexParam", indexParam);
      if(valueMap.get("personId") == null) {
        valueMap.put("personId", PlatformConstants.PERSON_ID_NO_PERSON);
      }
      if(valueMap.get("modifier") == null) {
        valueMap.put("modifier", valueMap.get("personId"));
      }
      valueMap.put((namespace != null && namespace.length()>0 ? namespace + "." : "") + "lastModTs", new Date());
      if (objectId == null || objectId.equals("")) {
        valueMap.put((namespace != null && namespace.length()>0 ? namespace + "." : "") + "createdOn", new Date());
        if(valueMap.get("creator") == null) {
          valueMap.put((namespace != null && namespace.length()>0 ? namespace + "." : "") + "creator", valueMap.get("personId"));
        }
      }
  
      Object retObj = this.getScriptManager().runScript("updateCollection", objectDao, objectType, valueMap, outParams);
      dsObj.setValueObject(retObj);
      
    } catch (Exception e) {
      throw new ObjectNotFoundException("Error updating object.", e);
    }
    return dsObj;
  }
  
  public IDataProviderObject createObject(String objectType, String objectDao,
      String returnObjectType, String permissionContext, String namespace, Map params, Map outParams) throws ObjectCreationException,
      DataProviderNotInitializedException, InvalidContextException {
    DataProviderObjectImpl dsObj = new DataProviderObjectImpl();
  
    params.put("objectDao", objectDao);
    params.put("namespace", namespace);
    params.put("objectClass", objectType);
    params.put("returnObjectType", returnObjectType);
    params.put("permissionContext", permissionContext);
    params.put((namespace != null && namespace.length()>0 ? namespace + "." : "") + "createTs", new Date());
    params.put((namespace != null && namespace.length()>0 ? namespace + "." : "") + "lastModTs", new Date());
    if (!objectType.endsWith("PersonImpl")) {
      if(params.get("personId") == null) {
        params.put("personId", PlatformConstants.PERSON_ID_NO_PERSON);
      }
      if(params.get("creator") == null) {
        params.put((namespace != null && namespace.length()>0 ? namespace + "." : "") + "creator", params.get("personId"));
      }
      if(params.get("modifier") == null) {
        params.put((namespace != null && namespace.length()>0 ? namespace + "." : "") + "modifier", params.get("personId"));
      }
    }
    try {
      // Get Object
      Object retObj = this.getScriptManager().runScript("create", objectDao, objectType, params, outParams);
  
      // Prepare Result
      dsObj.setValueObject(retObj);
  
    } catch (Exception e) {
      throw new ObjectCreationException("Error Creating object", e);
    }
    return dsObj;
  }
  
  public IDataProviderObject createObjectCollection(String objectType, String objectDao,
      String returnObjectType, String permissionContext, String indexParam, String namespace, Map params, Map outParams) throws ObjectCreationException,
      DataProviderNotInitializedException, InvalidContextException {
    DataProviderObjectImpl dsObj = new DataProviderObjectImpl();
  
    params.put("namespace", namespace);
    params.put("returnObjectType", returnObjectType);
    params.put("permissionContext", permissionContext);
    params.put("indexParam", indexParam);
    params.put((namespace != null && namespace.length()>0 ? namespace + "." : "") + "createTs", new Date());
    params.put((namespace != null && namespace.length()>0 ? namespace + "." : "") + "lastModTs", new Date());
    if (!objectType.endsWith("PersonImpl")) {
      if(params.get("personId") == null) {
        params.put("personId", PlatformConstants.PERSON_ID_NO_PERSON);
      }
      params.put((namespace != null && namespace.length()>0 ? namespace + "." : "") + "creator", params.get("personId"));
      params.put((namespace != null && namespace.length()>0 ? namespace + "." : "") + "modifier", params.get("personId"));
    }
    try {
      // Get Object
      Object retObj = this.getScriptManager().runScript("createCollection", objectDao, objectType, params, outParams);
  
      // Prepare Result
      dsObj.setValueObject(retObj);
  
    } catch (Exception e) {
      throw new ObjectCreationException("Error Creating object", e);
    }
    return dsObj;
  }
  
  public Object callAction(String objectType, String action, String objectDao, String objectId, String permissionContext, String namespace, Map params, Map outParams) throws ObjectCreationException {
    DataProviderObjectImpl dsObj = new DataProviderObjectImpl();
    
    params.put("guid", objectId);
    params.put("namespace", namespace);
    params.put("permissionContext", permissionContext);
    params.put((namespace != null && namespace.length()>0 ? namespace + "." : "") + "createTs", new Date());
    params.put((namespace != null && namespace.length()>0 ? namespace + "." : "") + "lastModTs", new Date());
    if (!objectType.endsWith("PersonImpl")) {
      if(params.get("personId") == null) {
        params.put((namespace != null && namespace.length()>0 ? namespace + "." : "") + "personId", PlatformConstants.PERSON_ID_NO_PERSON);
      }
      params.put((namespace != null && namespace.length()>0 ? namespace + "." : "") + "creator", params.get("personId"));
      params.put((namespace != null && namespace.length()>0 ? namespace + "." : "") + "modifier", params.get("personId"));
    }
  
    // Get Object
    try {
      Object retObj = this.getScriptManager().runScript(action, objectDao, objectType, params, outParams);
      dsObj.setValueObject(retObj);
    } catch (Exception e) {
      throw new ObjectCreationException("Error running action " + action + " on " + objectDao, e);
    }
  
    return dsObj;
  }
  
  public Object dispatchAction(String action, String dao, String objectType, 
	      String returnObjectType, String guidParam, String permissionContext, String indexParam, String namespace, Map params, Map outParams) throws Exception {
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
	      // TODO Give a better message jackass
	    }
	    if (action.equalsIgnoreCase("update")) {
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
	    } else if (action.equalsIgnoreCase("purge")) {
	      returnObj = this.purgeObject(dao, guid, permissionContext, namespace, params, outParams);
	    } else {
	      returnObj = this.callAction(objectType, action, dao, guid, permissionContext, namespace, params, outParams);
	    }
	  
	    return returnObj;
	  }
	  
  public Object dispatchActionEx(IRequest request, String action, String dao, String objectType, 
	      String returnObjectType, String guidParam, String permissionContext, String indexParam, String namespace, Map params, Map outParams) throws Exception {
	  return dispatchAction(action, dao, objectType, returnObjectType, guidParam, permissionContext, indexParam, namespace, params, outParams);
	}
	  
  public IScriptManager getScriptManager() {
    return scriptManager;
  }
  
  public void setScriptManager(IScriptManager scriptManager) {
    this.scriptManager = scriptManager;
  }

}
