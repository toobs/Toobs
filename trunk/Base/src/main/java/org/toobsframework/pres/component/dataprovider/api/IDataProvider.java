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
   * delete the specified object
   * 
   * @param objectId
   * @return true if something was removed, false otherwise
   * @throws ObjectNotFoundException
   * @throws DataProviderNotInitializedException
   */
  public Boolean deleteObject(String dao, String objectId, String permissionContext, String namespace, Map<String, Object> params, Map<String, Object> outParams)
      throws ObjectNotFoundException, DataProviderNotInitializedException;

  /**
   * Update the specified simple properties for the specified object.
   * 
   * @param objectId
   * @param values -
   *          property name/value pairs
   */
  public IDataProviderObject updateObject(String objectType, String objectDao,
      String returnObjectType, String objectId, String permissionContext, String namespace, Map<String, Object> valueMap, Map<String, Object> outParams)
      throws ObjectNotFoundException, PropertyNotFoundException,
      TypeMismatchException, DataProviderNotInitializedException;

  /**
   * Update the specified simple properties for the specified object.
   * 
   * @param objectId
   * @param values -
   *          property name/value pairs
   */
  public IDataProviderObject updateObjectCollection(String objectType, String objectDao,
      String returnObjectType, String objectId, String permissionContext, String namespace, String indexParam, Map<String, Object> valueMap, Map<String, Object> outParams)
      throws ObjectNotFoundException, PropertyNotFoundException,
      TypeMismatchException, DataProviderNotInitializedException;

  /**
   * Create an object using the specified context and properties
   * 
   * @param parentId
   * @param context
   * @param properties
   * @return
   * @throws ObjectCreationException
   * @throws DataProviderNotInitializedException
   * @throws InvalidContextException
   */
  public IDataProviderObject createObject(String objectType, String objectDao,
      String returnObjectType, String permissionContext, String namespace, Map<String, Object> params, Map<String, Object> outParams) throws ObjectCreationException,
      DataProviderNotInitializedException, InvalidContextException;

  /**
   * Create an object using the specified context and properties
   * 
   * @param parentId
   * @param context
   * @param properties
   * @return
   * @throws ObjectCreationException
   * @throws DataProviderNotInitializedException
   * @throws InvalidContextException
   */
  public IDataProviderObject createObjectCollection(String objectType, String objectDao,
      String returnObjectType, String permissionContext, String indexParam, String namespace, Map<String, Object> params, Map<String, Object> outParams) throws ObjectCreationException,
      DataProviderNotInitializedException, InvalidContextException;

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
