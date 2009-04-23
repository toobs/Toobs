package org.toobsframework.pres.component.datasource.api;

import java.util.Collection;
import java.util.Map;

/**
 * @author stewari
 * 
 * Interface to a data source.
 */
public interface IDataSource {

  /**
   * get the object identified by the specified Id whether the tree rooted at
   * this object is returned is implementation specific
   * 
   * @param objectId
   * @param params
   * @return specified object
   */
  public IDataSourceObject getObject(String returnObjectType, String dao,
      String objectId, Map<String, Object> params, Map<String, Object> outParams) throws ObjectNotFoundException,
      DataSourceNotInitializedException;

  /**
   * delete the specified object
   * 
   * @param objectId
   * @return true if something was removed, false otherwise
   * @throws ObjectNotFoundException
   * @throws DataSourceNotInitializedException
   */
  public Boolean deleteObject(String dao, String objectId, String permissionContext, String namespace, Map<String, Object> params, Map<String, Object> outParams)
      throws ObjectNotFoundException, DataSourceNotInitializedException;

  /**
   * Update the specified simple properties for the specified object.
   * 
   * @param objectId
   * @param values -
   *          property name/value pairs
   */
  public IDataSourceObject updateObject(String objectType, String objectDao,
      String returnObjectType, String objectId, String permissionContext, String namespace, Map<String, Object> valueMap, Map<String, Object> outParams)
      throws ObjectNotFoundException, PropertyNotFoundException,
      TypeMismatchException, DataSourceNotInitializedException;

  /**
   * Update the specified simple properties for the specified object.
   * 
   * @param objectId
   * @param values -
   *          property name/value pairs
   */
  public IDataSourceObject updateObjectCollection(String objectType, String objectDao,
      String returnObjectType, String objectId, String permissionContext, String namespace, String indexParam, Map<String, Object> valueMap, Map<String, Object> outParams)
      throws ObjectNotFoundException, PropertyNotFoundException,
      TypeMismatchException, DataSourceNotInitializedException;

  /**
   * Create an object using the specified context and properties
   * 
   * @param parentId
   * @param context
   * @param properties
   * @return
   * @throws ObjectCreationException
   * @throws DataSourceNotInitializedException
   * @throws InvalidContextException
   */
  public IDataSourceObject createObject(String objectType, String objectDao,
      String returnObjectType, String permissionContext, String namespace, Map<String, Object> params, Map<String, Object> outParams) throws ObjectCreationException,
      DataSourceNotInitializedException, InvalidContextException;

  /**
   * Create an object using the specified context and properties
   * 
   * @param parentId
   * @param context
   * @param properties
   * @return
   * @throws ObjectCreationException
   * @throws DataSourceNotInitializedException
   * @throws InvalidContextException
   */
  public IDataSourceObject createObjectCollection(String objectType, String objectDao,
      String returnObjectType, String permissionContext, String indexParam, String namespace, Map<String, Object> params, Map<String, Object> outParams) throws ObjectCreationException,
      DataSourceNotInitializedException, InvalidContextException;

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
  public Collection<IDataSourceObject> search(String returnValueObject, String dao,
      String searchCriteria, String searchMethod, String permissionAction, Map<String, Object> params, Map<String, Object> outParams)
      throws ObjectCreationException, InvalidSearchContextException,
      InvalidSearchFilterException, DataSourceNotInitializedException;

  public Collection<IDataSourceObject> searchIndex(String returnValueObject, String dao,
      String searchCriteria, String searchMethod, String permissionAction, Map<String, Object> params, Map<String, Object> outParams)
      throws ObjectCreationException, InvalidSearchContextException,
      InvalidSearchFilterException, DataSourceNotInitializedException;

  public Object dispatchAction(String action, String dao, String objectType, 
      String returnObjectType, String guidParam, String permissionContext, String indexParam, String namespace, Map<String, Object> params, Map<String, Object> outParams) throws Exception;

}
