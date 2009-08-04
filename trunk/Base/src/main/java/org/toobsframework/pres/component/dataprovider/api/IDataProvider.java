package org.toobsframework.pres.component.dataprovider.api;

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author stewari
 * 
 * Interface to a data source.
 */
public interface IDataProvider {

  public Object dispatchAction(String action, String dao, String objectType, 
      String returnObjectType, String guidParam, String permissionContext, String indexParam, String namespace, Map<String, Object> params, Map<String, Object> outParams) throws Exception;

  public Object dispatchActionEx(HttpServletRequest request, HttpServletResponse response, String action, String dao, String objectType, 
	      String returnObjectType, String guidParam, String permissionContext, String indexParam, String namespace, Map<String, Object> params, Map<String, Object> outParams) throws Exception;

}
