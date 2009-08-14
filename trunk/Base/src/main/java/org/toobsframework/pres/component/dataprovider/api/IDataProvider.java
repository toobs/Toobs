package org.toobsframework.pres.component.dataprovider.api;

import java.util.Map;

import org.toobsframework.util.IRequest;

/**
 * @author stewari
 * 
 * Interface to a data source.
 */
public interface IDataProvider {

  public Object dispatchAction(String action, String dao, String objectType, 
      String returnObjectType, String guidParam, String permissionContext, String indexParam, String namespace, Map<String, Object> params, Map<String, Object> outParams) throws Exception;

  public Object dispatchActionEx(IRequest request, String action, String dao, String objectType, 
	      String returnObjectType, String guidParam, String permissionContext, String indexParam, String namespace, Map<String, Object> params, Map<String, Object> outParams) throws Exception;

}
