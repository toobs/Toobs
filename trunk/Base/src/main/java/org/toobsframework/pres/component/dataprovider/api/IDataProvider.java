package org.toobsframework.pres.component.dataprovider.api;

import java.util.Collection;
import java.util.Map;

/**
 * @author stewari
 * 
 * Interface to a data source.
 */
public interface IDataProvider {

  /**
   * get the object identified by the specified Id whether the tree rooted at
   * this object is returned is implementation specific
   * 
   * @param objectId
   * @param params
   * @return specified object
   */
  public IDataProviderObject getObject(String returnObjectType, String serviceProvider, String propertyName,
      String objectId, Map<String, Object> params, Map<String, Object> outParams) throws ObjectNotFoundException,
      DataProviderNotInitializedException;

  /**
   * @param objectType
   *          If null all objects are searched
   * @param filterExpr -
   *          filterExpr expression to use for search. filterExpr is based on
   *          RFC 2254
   * @param scope -
   *          defines the scope of the search
   * @return objects that match the search criteria
   */
  public Collection<IDataProviderObject> search(String returnValueObject, String dao,
      String searchCriteria, String searchMethod, String permissionAction, Map<String, Object> params, Map<String, Object> outParams)
      throws ObjectCreationException, InvalidSearchContextException,
      InvalidSearchFilterException, DataProviderNotInitializedException;

  public Collection<IDataProviderObject> searchIndex(String returnValueObject, String dao,
      String searchCriteria, String searchMethod, String permissionAction, Map<String, Object> params, Map<String, Object> outParams)
      throws ObjectCreationException, InvalidSearchContextException,
      InvalidSearchFilterException, DataProviderNotInitializedException;

  public Object dispatchAction(String action, String dao, String objectType, 
      String returnObjectType, String guidParam, String permissionContext, String indexParam, String namespace, Map<String, Object> params, Map<String, Object> outParams) throws Exception;

}
