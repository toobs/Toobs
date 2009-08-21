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

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.ExchangePattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.toobsframework.pres.component.dataprovider.api.DispatchContext;
import org.toobsframework.pres.component.dataprovider.api.DispatchContextEx;
import org.toobsframework.pres.component.dataprovider.api.DispatchContextFactory;
import org.toobsframework.pres.component.dataprovider.api.IDataProvider;
import org.toobsframework.pres.component.dataprovider.api.ObjectNotFoundException;
import org.toobsframework.util.IRequest;

public class CamelDataProviderImpl implements IDataProvider, ApplicationContextAware {
  protected final static Log log = LogFactory.getLog(CamelDataProviderImpl.class);
  
  private ApplicationContext applicationContext;

  public void setApplicationContext(ApplicationContext context) throws BeansException {
    this.applicationContext = context;
  }
  
  private Object getBean(String serviceProviderName) {
    return applicationContext.getBean(serviceProviderName);
  }

  public Object dispatchAction(String action, String camelContextName, String objectType_unused,
      String returnObjectType_unused, String guidParam, String permissionContext,
      String indexParam_unused, String namespace, Map<String, Object> params,
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

    CamelContext camelContext = (CamelContext) getBean(camelContextName);
    DispatchContext dispatchContext = DispatchContextFactory.createDispatchContext(action, guid, permissionContext, namespace, params, outParams);
    if (log.isTraceEnabled()) {
      log.trace("+++ calling camel action " + action + " for " + camelContextName);
    }
    returnObj = camelContext.createProducerTemplate().sendBody("direct:" + action, ExchangePattern.InOut, dispatchContext);
    if (log.isTraceEnabled()) {
      log.trace("+++ camel returned object " + returnObj);
    }
    
    if (returnObj != null && returnObj instanceof DispatchContext) {
      returnObj = ((DispatchContext) returnObj).getContextObject();
    }
    
    return returnObj;
  }

  public Object dispatchActionEx(IRequest request, String action, String camelContextName, String objectType_unused,
      String returnObjectType_unused, String guidParam, String permissionContext,
      String indexParam_unused, String namespace, Map<String, Object> params,
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

    CamelContext camelContext = (CamelContext) getBean(camelContextName);
    DispatchContextEx dispatchContext = DispatchContextFactory.createDispatchContextEx(request, action, guid, permissionContext, namespace, params, outParams);
    if (log.isTraceEnabled()) {
      log.trace("+++ calling extended camel action " + action + " for " + camelContextName);
    }
    returnObj = camelContext.createProducerTemplate().sendBody("direct:" + action, ExchangePattern.InOut, dispatchContext);
    if (log.isTraceEnabled()) {
      log.trace("+++ camel returned object " + returnObj);
    }
    
    if (returnObj != null && returnObj instanceof DispatchContextEx) {
      returnObj = ((DispatchContextEx) returnObj).getContextObject();
    }
    
    return returnObj;
  }

}
