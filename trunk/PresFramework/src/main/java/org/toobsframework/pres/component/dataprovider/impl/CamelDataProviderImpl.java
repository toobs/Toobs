package org.toobsframework.pres.component.dataprovider.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.camel.CamelContext;
import org.apache.camel.ExchangePattern;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.toobsframework.data.beanutil.BeanMonkey;
import org.toobsframework.pres.component.dataprovider.api.DataProviderNotInitializedException;
import org.toobsframework.pres.component.dataprovider.api.DispatchContext;
import org.toobsframework.pres.component.dataprovider.api.DispatchContextEx;
import org.toobsframework.pres.component.dataprovider.api.DispatchContextFactory;
import org.toobsframework.pres.component.dataprovider.api.IDataProvider;
import org.toobsframework.pres.component.dataprovider.api.IDataProviderObject;
import org.toobsframework.pres.component.dataprovider.api.InvalidSearchContextException;
import org.toobsframework.pres.component.dataprovider.api.InvalidSearchFilterException;
import org.toobsframework.pres.component.dataprovider.api.ObjectCreationException;
import org.toobsframework.pres.component.dataprovider.api.ObjectNotFoundException;

public class CamelDataProviderImpl implements IDataProvider, ApplicationContextAware {
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
    returnObj = camelContext.createProducerTemplate().sendBody("direct:" + action, ExchangePattern.InOut, dispatchContext);
    
    if (returnObj != null && returnObj instanceof DispatchContext) {
      returnObj = ((DispatchContext) returnObj).getContextObject();
    }
    
    return returnObj;
  }

  public Object dispatchActionEx(HttpServletRequest request, HttpServletResponse response, String action, String camelContextName, String objectType_unused,
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
    DispatchContextEx dispatchContext = DispatchContextFactory.createDispatchContextEx(request, response, action, guid, permissionContext, namespace, params, outParams);
    returnObj = camelContext.createProducerTemplate().sendBody("direct:" + action, ExchangePattern.InOut, dispatchContext);
    
    if (returnObj != null && returnObj instanceof DispatchContextEx) {
      returnObj = ((DispatchContext) returnObj).getContextObject();
    }
    
    return returnObj;
  }

}
