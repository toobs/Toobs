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
package org.toobsframework.pres.doit;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.validation.Errors;
import org.toobsframework.pres.component.config.Parameter;
import org.toobsframework.pres.doit.config.Action;
import org.toobsframework.pres.doit.config.Actions;
import org.toobsframework.pres.doit.config.DoIt;
import org.toobsframework.pres.doit.controller.strategy.ForwardStrategy;
import org.toobsframework.biz.validation.IValidator;
import org.toobsframework.data.IObjectLoader;
import org.toobsframework.data.beanutil.BeanMonkey;
import org.toobsframework.exception.ValidationException;
import org.toobsframework.pres.component.dataprovider.api.IDataProvider;
import org.toobsframework.pres.util.ComponentRequestManager;
import org.toobsframework.pres.util.ParameterUtil;
import org.toobsframework.search.index.ISingleIndexBuilder;
import org.toobsframework.util.IRequest;
import org.toobsframework.util.constants.PlatformConstants;

public class DoItRunner implements IDoItRunner {

  private static Log log = LogFactory.getLog(DoItRunner.class);

  private ComponentRequestManager componentRequestManager;
  private ISingleIndexBuilder indexBuilder;

  private IDataProvider dataProvider;

  public void runDoIt(IRequest request, DoIt doIt) throws Exception 
  {
    // Run Actions
    Action thisAction = null;
    if(doIt.getActions() != null) {
      Actions actionsObj = doIt.getActions();

      String multipleActionsKey = "defaultAction";
      String[] multipleActionsAry = new String[] {""};
      Map<String,Object> paramMap = request.getParams();
      Map<String,Object> responseMap = request.getResponseParams();
      try {
        if(actionsObj.getMultipleActionsKey() != null && !actionsObj.getMultipleActionsKey().equals("")) {
          multipleActionsKey = actionsObj.getMultipleActionsKey();
          Object multActObj = paramMap.get(actionsObj.getMultipleActionsKey());
          if (multActObj != null && multActObj.getClass().isArray()) {
            multipleActionsAry = (String[])multActObj;
          } else if (multActObj != null) {
            multipleActionsAry = new String[] {(String)multActObj};
          } else {
            multipleActionsAry = new String[] {""};
          }
        }
        
        for(int i = 0; i < multipleActionsAry.length; i++) {
          Map<String,Object> actionParams = new HashMap<String,Object>(paramMap);
          responseMap.clear();
          actionParams.put(multipleActionsKey, multipleActionsAry[i]);
          actionParams.put(PlatformConstants.MULTI_ACTION_INSTANCE, new Integer(i));
          Enumeration<Action> actions = doIt.getActions().enumerateAction();
          while (actions.hasMoreElements()) {
            thisAction = actions.nextElement();
            runAction(request, doIt.getName(), thisAction, actionParams, responseMap, (i == (multipleActionsAry.length - 1)));
          }            
        }
        Iterator<String> iter = responseMap.keySet().iterator();
        while (iter.hasNext()) {
          String key = iter.next();
          paramMap.put(key, responseMap.get(key));
          /*
          if (componentRequestManager.get().getHttpRequest() != null) {
            componentRequestManager.get().getHttpRequest().setAttribute((String)key, responseMap.get(key));
          }
          */
        }
      }
      
      // if validation errors are thrown, make sure to correctly pull
      // error objects..
      catch (Exception e) {
        if (e.getCause() instanceof ValidationException) {
          log.warn("Caught validation exception.");
          this.pullErrorObjectsIntoRequest(request, doIt, paramMap, responseMap, multipleActionsKey, multipleActionsAry, (ValidationException)e.getCause());
        }
        throw e;
      }
    }
  }
      
  private void runAction(IRequest request, String doItName, Action thisAction, Map<String,Object> params, Map<String,Object> responseParams, boolean lastAction)
    throws Exception {
          
    String actionType = thisAction.getActionType();
    Object retObj = null;
    if (actionType.equalsIgnoreCase("objectAction")) {
      //Fix the input params using the param mapping for 
      //this configuration.
      if (thisAction.getParameters() != null) {
        // Cant do this for now cause of the array problem
        //ParameterUtil.mapParameters(thisAction.getParameters().getParameter(), params, params, doItName);
        ParameterUtil.mapDoItParameters(request, thisAction.getParameters().getParameter(), params, params, true );
      }
      try {
        if (thisAction.isExtended()) {
          retObj = this.getDataProvider().dispatchActionEx(
              request,
              thisAction.getAction(), 
              ((String[])ParameterUtil.resolveParam(request, thisAction.getServiceProvider(), params))[0], 
              ((String[])ParameterUtil.resolveParam(request, thisAction.getInputObjectType(), params))[0], 
              thisAction.getReturnObjectType(),
              ((String[])ParameterUtil.resolveParam(request, thisAction.getGuidParam(), params))[0], 
              thisAction.getPermissionContext(),
              thisAction.getIndexParam(),
              thisAction.getNamespace(),
              params,
              responseParams);
        } else {
          retObj = this.getDataProvider().dispatchAction(
              thisAction.getAction(), 
              ((String[])ParameterUtil.resolveParam(request, thisAction.getServiceProvider(), params))[0], 
              ((String[])ParameterUtil.resolveParam(request, thisAction.getInputObjectType(), params))[0], 
              thisAction.getReturnObjectType(),
              ((String[])ParameterUtil.resolveParam(request, thisAction.getGuidParam(), params))[0], 
              thisAction.getPermissionContext(),
              thisAction.getIndexParam(),
              thisAction.getNamespace(),
              params,
              responseParams);
        }
        /* TODO: Remove this later 
        Iterator iter = responseParams.keySet().iterator();
        while (iter.hasNext()) {
          Object key = iter.next();
          params.put((String)key, responseParams.get(key));
        }
        */
      } catch (Exception e) {
        /* TODO Check to see if making responseParams work as error forward params
         * cause this sucks balls
        if (e.getCause() instanceof ValidationException) {
          responseParams.put("ErrorForwardParams", params.get("ErrorForwardParams"));
        }
        */
        throw e;
      }
    } else if (actionType.equalsIgnoreCase("cookieAction")) {
      String cookieName = ((String[])ParameterUtil.resolveParam(request, params.get("cookieName"), params))[0];
      String cookieValue = ((String[])ParameterUtil.resolveParam(request, params.get("cookieValue"), params))[0];
      int maxAge = -1;
      try {
        maxAge = Integer.parseInt(((String[])ParameterUtil.resolveParam(request, params.get("maxAge"), params))[0]);
      } catch (Exception e) {}
      
      Cookie doitCookie = new Cookie(cookieName, cookieValue);
      doitCookie.setMaxAge(maxAge);
      componentRequestManager.get().getHttpResponse().addCookie(doitCookie);
    } else if (actionType.equalsIgnoreCase("sessionAction")) {
      Map<String,Object> sessionMap = new HashMap<String,Object>();

      if (thisAction.getParameters() != null) {
        ParameterUtil.mapDoItParameters(request, thisAction.getParameters().getParameter(), params, sessionMap, true);
      }
      HttpSession session = componentRequestManager.get().getHttpRequest().getSession();
      Iterator<Map.Entry<String,Object>> iter = sessionMap.entrySet().iterator();
      while (iter.hasNext()) {
        Map.Entry<String,Object> entry = iter.next();
        session.setAttribute(entry.getKey(), entry.getValue());
      }
    } else if (actionType.equalsIgnoreCase("indexAction") && lastAction) {
      if (this.getIndexBuilder() != null) {
        indexBuilder.buildIndexes(thisAction.getServiceProvider());
      }
    } else {
      //TODO -- Add the ability to run scripts defined in config here.
    }
    
    //HashMap responseParams = new HashMap();
    //Add the output params into the request for 
    //this configuration.
    if(thisAction.getOutputParameters() != null && retObj != null){
      JXPathContext context = null;
      if ("delete".equalsIgnoreCase(thisAction.getAction())) {
        context = JXPathContext.newContext(responseParams);
        responseParams.put("deleted", String.valueOf(((Boolean)retObj).booleanValue()));
      } else {
        context = JXPathContext.newContext(retObj);
      }
      Parameter[] paramMap =  thisAction.getOutputParameters().getParameter();
      for(int j = 0; j < paramMap.length; j++){
         Parameter thisParam = paramMap[j];
         String[] paramPath = ParameterUtil.resolveParam(request, thisParam.getPath(), params);
         String[] paramName = ParameterUtil.resolveParam(request, thisParam.getName(), params);
         Object value = null;
         for (int i = 0; i <paramName.length; i++) {
           if(thisParam.getIsStatic()){
             value = thisParam.getPath();
           } else {
             try {
             value = context.getValue(paramPath[i]);
             } catch (org.apache.commons.jxpath.JXPathException e) {
               if (!thisParam.getIgnoreNull()) {
                 log.warn("Problem evaluating jxpath: " + paramName[i] + " value: " + paramPath[i] + " action: " + thisAction.getServiceProvider(), e);
               }
               continue;
             }
             if (value != null && value.getClass().isArray()) {
               value = ((String[])value)[0];
             }
           }
           responseParams.put(paramName[i], value);
         }
       }
    }
    
    //Add 
    if(thisAction.getReturnAttributeName() != null && retObj != null){
      JXPathContext context = JXPathContext.newContext(retObj);
      responseParams.put(thisAction.getReturnAttributeName(), context.getValue("./valueObject/guid"));
    }

    Iterator<String> iter = responseParams.keySet().iterator();
    while (iter.hasNext()) {
      String key = iter.next();
      params.put(key, responseParams.get(key));
    }
  
  }
  
  /**
   *  Called upon receipt of a validation error.  Iterates over all the
   *  actions for this DoIt, retrieving an object of appropriate type out
   *  of the posted request.  All such objects are put in the response parameter
   *  mapping under the "ValidationErrorObjects" key.
   */
  @SuppressWarnings("unchecked")
  private void pullErrorObjectsIntoRequest(IRequest request, DoIt doIt, Map<String,Object> paramMap, Map<String,Object> responseMap, String multipleActionsKey, String[] multipleActionsAry, ValidationException ve) throws Exception
  {
    if (log.isDebugEnabled()) {
      log.debug("ENTER pullErrorObjectsIntoRequest");
    }
    Action thisAction = null;
    Collection globalErrorObjects = new ArrayList();
    Collection<String> globalErrorMessages = new ArrayList<String>();
    BeanFactory beanFactory = BeanMonkey.getBeanFactoryInstance();
    
    if(doIt.getActions() != null) {
      paramMap.remove("guid");
      //Actions actionsObj = doIt.getActions();
      
      for(int i = 0; i < multipleActionsAry.length; i++) {
        HashMap actionParams = new HashMap(paramMap);
        responseMap.clear();
        actionParams.put(multipleActionsKey, multipleActionsAry[i]);
        actionParams.put(PlatformConstants.MULTI_ACTION_INSTANCE, new Integer(i));
        Collection instanceErrorObjects = new ArrayList();
        Collection instanceErrorMessages = new ArrayList();
        
        if (ve != null) {
          Iterator errIter = ve.getErrors().iterator();
          while (errIter.hasNext()) {
            Errors err = (Errors)errIter.next();
            instanceErrorMessages.addAll(err.getAllErrors());
          }        
        }
        // iterate over the create and update actions
        Enumeration actions = doIt.getActions().enumerateAction();
        while (actions.hasMoreElements()) {
          // for each, retrieve the object of appropriate type
          thisAction = (Action) actions.nextElement();
          
          // get parameters from action object
          String actionStr = thisAction.getAction();
          // for now, only do error object handling for creates and updates..
          if(!actionStr.startsWith("create") && !actionStr.startsWith("update"))
          {
            continue;
          }        
          
          //retrieve the input object for this action
          Object inputObject = constructInputObjectFromAction(request, thisAction, actionParams);
          
          //if there's an input object 
          if(inputObject != null || actionStr.endsWith("Collection"))
          {
            if (thisAction.getNamespace() != null && !"".equals(thisAction.getNamespace())) {
              actionParams.put("namespace", thisAction.getNamespace());
            }
            // before calling the getSafeBean method, have to 
            // stick this action's returnObjectType in the paramMap
            String objectReturnType = ((String[])ParameterUtil.resolveParam(request, thisAction.getReturnObjectType(), actionParams))[0];
            actionParams.put("returnObjectType", objectReturnType);
            
            // use the bean monkey to populate that object
            // (pass false in as last arg to block validation)
            boolean collection = false;
            String className = null;
            if (actionStr.endsWith("Collection")) {
              String beanClazz = ((String[])ParameterUtil.resolveParam(request, thisAction.getInputObjectType(), actionParams))[0];
              inputObject = BeanMonkey.populateCollection(beanClazz, thisAction.getIndexParam(), actionParams, false, thisAction.getValidationErrorMode(), instanceErrorMessages);
              collection = true;
              className = beanClazz.substring(beanClazz.lastIndexOf(".") + 1);
            } else {
              try {
                BeanMonkey.populate(inputObject, actionParams, instanceErrorMessages);
              } catch (ValidationException e) {
                Iterator errIter = e.getErrors().iterator();
                while (errIter.hasNext()) {
                  Errors err = (Errors)errIter.next();
                  instanceErrorMessages.addAll(err.getAllErrors());
                }        
              }
              className = inputObject.getClass().getName();
            }
            // If there are no error messages for the object don't produce an error object
            //if (instanceErrorMessages.size() == 0) continue;
            
            // and get the validator for the input object
            IValidator v = null;
            className = className.substring(className.lastIndexOf(".") + 1);
            String validatorName = className + "Validator";
            if (beanFactory.containsBean(validatorName)) {
              v = (IValidator) beanFactory.getBean(validatorName);
            } else {
              log.warn("No validator " + validatorName + " for " + className);
            }
            
            // if there's no validator, then just continue...
            if(v == null) continue;
            
            // call the validator's prepare method,
            // pipe the populated bean through the validator's getSafeBean method
            // and, finally dump the populated object into the error objects map
            actionParams.put("doit.validation.error.mode", new Boolean(true));
            v.prepare(inputObject, actionParams);
            if (collection && inputObject != null && inputObject instanceof Collection) {
              Iterator iter = ((Collection)inputObject).iterator();
              while (iter.hasNext()) {
                instanceErrorObjects.add(v.getSafeBean(iter.next(), actionParams));
              }
            } else {
              instanceErrorObjects.add(v.getSafeBean(inputObject, actionParams));
            }
          }
          
          continue;
        }
        globalErrorMessages.addAll(instanceErrorMessages);
        globalErrorObjects.addAll(instanceErrorObjects);
      }      
      // put the populated error objs in the request scope.
      responseMap.put(ForwardStrategy.VALIDATION_ERROR_OBJECTS, globalErrorObjects);
      responseMap.put(ForwardStrategy.VALIDATION_ERROR_MESSAGES, globalErrorMessages);
      
    }
    
    if (log.isDebugEnabled()) {
      log.debug("EXIT pullErrorObjectsIntoRequest");
    }
  }
  
  /**
   *   Given an action, looks at parameters w.in that action to either 
   *   load an object of appropriate type using a dao, or just construct
   *   an empty object of appropriate type.
   */
  @SuppressWarnings("unchecked")
  private Object constructInputObjectFromAction(IRequest request, Action action, Map paramMap) throws Exception
  {
    String objectDao = ((String[])ParameterUtil.resolveParam(request, action.getServiceProvider(), paramMap))[0];
    String objectInputType = ((String[])ParameterUtil.resolveParam(request, action.getInputObjectType(), paramMap))[0]; 

    // Fix the input params using the param mapping for 
    // this configuration.
    if (action.getParameters() != null) {
      ParameterUtil.mapDoItParameters(request, action.getParameters().getParameter(), paramMap, paramMap, true);
    }
     
    Object inputObject = null;
    
    // if the object input type was specified, construct an empty object 
    if(objectInputType != null && !"".equals(objectInputType)) {
      // otherwise just instantiate a new object of the appropriate type
      try {
        Class clazz = Class.forName(objectInputType);
        if(clazz != null)
          inputObject = clazz.getConstructor().newInstance();
      } catch (ClassNotFoundException cnfe) {
        // if class isn't found here... don't do shit..
        // method will return null, and this action will be skipped in the
        // populated list of error objects
      } catch (InstantiationException ie) {
        // Abstract too
      }
    }
    
    //NOTE: passing leading '$' forces a lookup in the parameter map
    String[] objectGuidArray = ((String[])ParameterUtil.resolveParam(request, "$" + action.getGuidParam(), paramMap));
    String objectGuid = objectGuidArray == null ? null : objectGuidArray[0];
    
    // but if guid is present, replace the empty object with db load
    if(objectGuid != null && !"".equals(objectGuid))
    {
      // then use the dao to load the object from the db
      Object daoBean = null;      
      //if a dao bean is specified ... load it up
      if(objectDao != null && !"".equals(objectDao))
      {
        // fetch the object using a dao bean..
        try {
          daoBean = BeanMonkey.getBeanFactoryInstance().getBean(objectDao);
        } catch(org.springframework.beans.factory.NoSuchBeanDefinitionException nsbde) {
          //if there isn't a daoBean matching that specified in the DOIT, this is so
          //because there is most likely a custom js script underlying the definition
          //so, bypass this exception by just returning null here.
          log.warn("No bean named " + objectDao + " is defined.  Returning null.");
          return null;
        }
        
        // get the personId parameter
        //String personGuid = (String) paramMap.get("personId");

        try {
          inputObject = ((IObjectLoader)daoBean).load(objectGuid);
        } catch(ClassCastException cce) {
          // if dao is not of type IBaseObjectDao, then just return null
          return null;
        }
      }
    }
    
    return inputObject;
  }
  
  public void setDataProvider(IDataProvider dataProvider) {
    this.dataProvider = dataProvider;
  }

  public IDataProvider getDataProvider() {
    return dataProvider;
  }
  
  public ComponentRequestManager getComponentRequestManager() {
    return componentRequestManager;
  }

  public void setComponentRequestManager(
      ComponentRequestManager componentRequestManager) {
    this.componentRequestManager = componentRequestManager;
  }

  public ISingleIndexBuilder getIndexBuilder() {
    return indexBuilder;
  }

  public void setIndexBuilder(ISingleIndexBuilder indexBuilder) {
    this.indexBuilder = indexBuilder;
  }

}
