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
package org.toobsframework.pres.component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.springframework.validation.ObjectError;
import org.toobsframework.pres.base.Transformable;
import org.toobsframework.pres.component.config.GetObject;
import org.toobsframework.exception.ParameterException;
import org.toobsframework.pres.component.dataprovider.api.IDataProvider;
import org.toobsframework.pres.component.dataprovider.api.IDataProviderObject;
import org.toobsframework.pres.component.dataprovider.impl.DataProviderObjectImpl;
import org.toobsframework.pres.doit.controller.strategy.ForwardStrategy;
import org.toobsframework.pres.util.CookieVO;
import org.toobsframework.pres.util.ParameterUtil;
import org.toobsframework.transformpipeline.domain.IXMLTransformer;
import org.toobsframework.transformpipeline.domain.IXMLTransformerHelper;
import org.toobsframework.transformpipeline.domain.XMLTransformerException;
import org.toobsframework.util.BetwixtUtil;
import org.toobsframework.util.IRequest;

/**
 * @author pudney
 */
public class Component extends Transformable {
  private static final String XML_HEADER = "<?xml version=\"1.0\"?>";
  private static final String XML_START_COMPONENTS = "<component";
  private static final String XML_END_COMPONENTS = "</component>";
  private static final String XML_START_OBJECTS = "<objects>";
  private static final String XML_END_OBJECTS = "</objects>";
  private static final String XML_START_ERRORS = "<errors>";
  private static final String XML_END_ERRORS = "</errors>";
  private static final String XML_START_ERROR_OBJS = "<errorobjects>";
  private static final String XML_END_ERROR_OBJS = "</errorobjects>";

  private String id;
  private boolean renderErrorObject;
  private boolean scanUrls;
  private boolean initDone;

  private Map<String, List<Transform>> transforms;
  private GetObject[] objectsConfig;
  private String[] controllerNames;
  private String[] styles;
  private String fileName;

  private IDataProvider dataProvider;

  public Component(String id) {
    this.id = id;
    this.initDone = false;
  }

  public void init() throws ComponentInitializationException {
    this.initDone = true;
  }

  /**
   * Get the objects associated to this component
   * @param paramsIn - the parameters sent to the datasource to obtain th object
   * @param paramsOut
   * @return an array of all the objects implementing IDataSourceObject
   */
  public IDataProviderObject[] getObjects(IRequest request, Map<String,Object> paramsIn, Map<String,Object> paramsOut, IXMLTransformerHelper transformerHelper) throws ComponentException,
      ComponentNotInitializedException, ParameterException {
    List<IDataProviderObject> allObjects = new ArrayList<IDataProviderObject>();
    if (!this.initDone) {
      ComponentNotInitializedException ex = new ComponentNotInitializedException();
      ex.setComponentId(this.id);
      throw ex;
    }

    int len = objectsConfig.length;
    for (int i = 0; i < len; i++) {
      Map<String,Object> params = new HashMap<String,Object>(paramsIn);
      GetObject thisObjDef = objectsConfig[i];
      //Fix the params using the param mapping for 
      //this configuration.
      if(thisObjDef.getParameters() != null){
        ParameterUtil.mapParameters(request, "Component:" + this.id + ":GetObject:" + thisObjDef.getServiceProvider(), thisObjDef.getParameters().getParameter(), params, params, this.id, allObjects);
      }

      List<IDataProviderObject> theseObjects = new ArrayList<IDataProviderObject>();

      //Call the appropriate action.
      Map<String,Object> outParams = new HashMap<String,Object>();
      // TODO: JG I need to put the cookies: notations into into parameters
      if (thisObjDef.getAction().equals("getCookie")) {
        String searchCriteria = ParameterUtil.resolveParam(request, thisObjDef.getSearchCriteria(), params)[0];
        String thisGuidParam = ParameterUtil.resolveParam(request, thisObjDef.getGuidParam(), params)[0];
        String cookieName = (searchCriteria != null ? searchCriteria : "");
        Object guidValue = params.get(thisGuidParam);
        if (guidValue != null && guidValue.getClass().isArray()) {
          cookieName += ((String[])guidValue)[0];
        } else {
          cookieName += guidValue;
        }
        String cookieValue = null;
        
        Cookie[] cookies = request.getHttpRequest().getCookies();
        if (cookies != null) {
          for (int c = 0; c < cookies.length; c++) {
            Cookie cookie = cookies[c];
            if (cookie.getName().equals(cookieName)) {
              cookieValue = cookie.getValue();
              break;
            }
          }
        }
        if (cookieName != null && cookieValue != null) {
          theseObjects.add(this.createObject(new CookieVO(cookieName, cookieValue)));
        }
      } else {
        theseObjects.add(getDispatchedObject(request, thisObjDef, params, outParams));
      }
      // TODO SNIP!!!
      ParameterUtil.mapScriptParams(outParams, paramsIn);
      if(thisObjDef.getOutputParameters() != null){
        ParameterUtil.mapOutputParameters(request, thisObjDef.getOutputParameters().getParameter(), paramsIn, this.id, theseObjects);
        if (paramsOut != null) {
          ParameterUtil.mapOutputParameters(request, thisObjDef.getOutputParameters().getParameter(), paramsOut, this.id, theseObjects);
        }
      }
      allObjects.addAll(theseObjects);
    }

    IDataProviderObject[] objArray = new IDataProviderObject[allObjects.size()];
    objArray = allObjects.toArray(objArray);
    return objArray;
  }

  public IDataProviderObject createObject(Object valueObject) throws ComponentException,
      ComponentNotInitializedException {
    if (valueObject == null) {
      return null;
    }
    DataProviderObjectImpl dsObj = new DataProviderObjectImpl();
    dsObj.setValueObject(valueObject);
    return dsObj;
  }

/*  public IDataProviderObject getObject(String actionName, String returnedValueObject,
      String daoObject, String property, boolean noCache, String guid, Map<String, Object> params, Map<String, Object> outParams) throws ComponentException,
      ComponentNotInitializedException {
    IDataProviderObject object = null;
    if (!this.initDone) {
      ComponentNotInitializedException ex = new ComponentNotInitializedException();
      ex.setComponentId(this.Id);
      throw ex;
    }
    try {
      if (!noCache) {
        object = componentRequestManager.checkRequestCache(actionName, returnedValueObject, guid);
      }
      if (object == null) {
        object = dataProvider.getObject(returnedValueObject, daoObject, property, guid, params, outParams);
        if (!noCache) {
          componentRequestManager.cacheObject(actionName, returnedValueObject, guid, object);
        }
      }
    } catch (DataProviderNotInitializedException ex) {
      ComponentException ce = new ComponentException("Datasource not initialized.", ex);
      throw ce;
    } catch (ObjectNotFoundException ex) {
      ComponentException ce = new ComponentException("Error getting object " +
          returnedValueObject + " for component " + this.Id, ex);
      throw ce;
    }
    return object;
  }
*/
  
  public IDataProviderObject getDispatchedObject(IRequest request, GetObject thisObjDef,  Map<String, Object> params, Map<String, Object> outParams) throws ComponentException,
      ComponentNotInitializedException {
    IDataProviderObject object = null;
    if (!this.initDone) {
      ComponentNotInitializedException ex = new ComponentNotInitializedException();
      ex.setComponentId(this.id);
      throw ex;
    }
    try {
      
      /* TODO Move caching logic
      if (!thisObjDef.getNoCache()) {
        object = componentRequestManager.checkRequestCache(thisObjDef.getServiceProvider(), thisObjDef.getAction(), "");
      }
      
      if (object == null) {
      */
        Object obj;
        if (thisObjDef.isExtended()) {
          obj = dataProvider.dispatchActionEx(request, thisObjDef.getAction(), thisObjDef.getServiceProvider(), 
              "", "", thisObjDef.getGuidParam(), thisObjDef.getPermissionAction(), "", 
              "", params, outParams);
        } else {
          obj = dataProvider.dispatchAction(thisObjDef.getAction(), thisObjDef.getServiceProvider(), 
            "", "", thisObjDef.getGuidParam(), thisObjDef.getPermissionAction(), "", 
            "", params, outParams);
        }
        if (obj != null && !(obj instanceof IDataProviderObject)) {
          object = new DataProviderObjectImpl();
          ((DataProviderObjectImpl)object).setValueObject(obj);
        } else if (obj != null){
          object = (IDataProviderObject) obj;
        }
        /*
        if (!thisObjDef.getNoCache() && object != null) {
          componentRequestManager.cacheObject(thisObjDef.getServiceProvider(), thisObjDef.getAction(), "", object);
        }
      }
      */
    } catch (Exception ex) {
      throw new ComponentException("Component " + getId() + " cannot get object with action " + thisObjDef.getAction(), ex);
    }
    return object;
  }

  /*public String render(String contentType, Map<String, Object> params, Map<String, Object> outParams)
    throws ComponentNotInitializedException, ComponentException, ParameterException {
    return this.render(contentType, params, outParams, null);
  }*/

  /**
   * Creates the Objects XML and then runs the proper transformations to 
   * complete the rendering of the component as defined in the .cc.xml config file.
   * 
   * @return rendered component
   * @throws ComponentNotInitializedException
   * @throws ComponentException
   * @throws IOException 
   */
  public String render(IRequest request, String contentType, Map<String, Object> params, IXMLTransformerHelper transformerHelper, Map<String, Object> outParams)
      throws ComponentNotInitializedException, ComponentException, ParameterException, IOException {
    ByteArrayOutputStream renderedOutput = new ByteArrayOutputStream();

    this.renderStream(renderedOutput, request, contentType, transformerHelper);

    return new String(renderedOutput.toByteArray(), "UTF-8");
  }

  public void renderStream(OutputStream outputStream, IRequest componentRequest, String outputFormat, IXMLTransformerHelper transformerHelper) throws ComponentException, ParameterException, ComponentNotInitializedException, IOException {
    Date start = new Date();
    String componentXML = this.getObjectsAsXML(componentRequest, componentRequest.getParams(), componentRequest.getResponseParams(), transformerHelper);
    Date endGet = new Date();

    if (!outputFormat.equals("bizXML")) {
      this.callXMLPipeline(outputStream, componentRequest, outputFormat, componentXML, transformerHelper);
    } else {
      outputStream.write(componentXML.getBytes());
    }

    Date end = new Date();
    if (log.isDebugEnabled()) {
      log.debug("Comp [" + id + "] gTime: " + (endGet.getTime()-start.getTime()) + " rTime: " + (end.getTime()-endGet.getTime()) + " fTime: " + (end.getTime()-start.getTime()));
    }
  }


  /**
   * Runs the objects through the xml pipeline to get the proper rendering of
   * the component as defined in the config file.
   * 
   * @return rendered component
   * @throws ComponentNotInitializedException
   * @throws ComponentException
   */
  private void callXMLPipeline(OutputStream outputStream, IRequest request, String contentType, String inputXMLString, IXMLTransformerHelper transformerHelper)
      throws ComponentException, ParameterException {

    try {

      List<String> inputXSLs = new ArrayList<String>();
      Map<String, Object> transformParams = new HashMap<String, Object>();

      // Prepare XSLs and Params.
      List<Transform> contentTransforms = this.getTransforms().get(contentType);
      if (contentTransforms != null && contentTransforms.size() > 0) {
        Iterator<Transform> it = contentTransforms.iterator();
        while (it.hasNext()) {
          Transform transform = it.next();
          inputXSLs.add(transform.getTransformName());
          //Fix the params using the param mapping for 
          //this configuration.
          if(transform.getTransformParams() != null){
            ParameterUtil.mapParameters(request, "Transform:" + transform.getTransformName(), transform.getTransformParams().getParameter(), request.getParams(), transformParams, this.id);
          }
        }
      } else {
        throw new ComponentException("Component with id: " + this.id + " does not have a transform for content type: " + contentType);
      }

      // Do Transformation
      if (inputXSLs.size() > 0) {
        transformParams.put(IXMLTransformer.COMPONENT_REQUEST, request);
        getTransformer(inputXSLs, contentType).transformStream(outputStream, inputXSLs, inputXMLString, transformParams, transformerHelper);
      }

    } catch (XMLTransformerException xte) {
      throw new ComponentException("Error running transform", xte);
    }

  }

  /**
   * Gets all of the objects in this component as a single xml stream with a
   * proper wrapper.  The XML looks like this:
   * 
   * <pre><code>
   * &lt;component id="<i>componentId</i>"&gt;
   *   &lt;objects&gt;
   *     ...
   *   &lt;/objects&gt;
   * &lt;/component&gt;
   * </code></pre>
   * 
   * @return component as xml
   * @throws ComponentNotInitializedException
   * @throws ComponentException
   */
  @SuppressWarnings("unchecked")
  private String getObjectsAsXML(IRequest request, Map<String, Object> params, Map<String, Object> outParams, IXMLTransformerHelper transformerHelper)
      throws ComponentNotInitializedException, ComponentException, ParameterException {
    StringBuffer xml = new StringBuffer();
    IDataProviderObject[] objects = this.getObjects(request, params, outParams, transformerHelper);
    try {
      xml.append(XML_HEADER);
      xml.append(XML_START_COMPONENTS).append(" id=\"").append(this.id).append("\">");
      xml.append(XML_START_OBJECTS);
      if ((objects != null) && (objects.length > 0)) {
        for (int i = 0; i < objects.length; i++) {
          if (objects[i] != null) {
            xml.append(objects[i].toXml());
          }
        }
      }
      xml.append(XML_END_OBJECTS);
      if (renderErrorObject) {
        if (params.containsKey(ForwardStrategy.VALIDATION_ERROR_MESSAGES)) {
          request.getHttpResponse().setHeader("toobs.error.validation", "true");
          List<ObjectError> globalErrorList = (List<ObjectError>)params.get(ForwardStrategy.VALIDATION_ERROR_MESSAGES);
          for (int g = 0; g < globalErrorList.size(); g++) {
            xml.append(XML_START_ERRORS);
            List<ObjectError> errorList = (List<ObjectError>)globalErrorList.get(g);
            for (int i = 0; i < errorList.size(); i++) {
              xml.append(BetwixtUtil.toXml(errorList.get(i)));
            }
            xml.append(XML_END_ERRORS);
          }
        }
        if (params.containsKey(ForwardStrategy.VALIDATION_ERROR_OBJECTS)) {
          List<IDataProviderObject> globalMessageList = (List<IDataProviderObject>)params.get(ForwardStrategy.VALIDATION_ERROR_OBJECTS);
          for (int g = 0; g < globalMessageList.size(); g++) {
            xml.append(XML_START_ERROR_OBJS);
            List<IDataProviderObject> errorList = (List<IDataProviderObject>)globalMessageList.get(g);
            for (int i = 0; i < errorList.size(); i++) {
              xml.append(BetwixtUtil.toXml(errorList.get(i)));
            }
            xml.append(XML_END_ERROR_OBJS);
          }
        }
      }
      xml.append(XML_END_COMPONENTS);
    } catch (IOException ex) {
      throw new ComponentException("Error getting xml for object", ex);
    }

    return xml.toString();
  }

  @SuppressWarnings("unchecked")
  private IDataProviderObject checkForValidation(Map<String, Object> paramsIn, GetObject getObjDef, String guid) throws ComponentException, ComponentNotInitializedException {
    List<IDataProviderObject> errorObjects = (List<IDataProviderObject>)paramsIn.get(ForwardStrategy.VALIDATION_ERROR_OBJECTS);
    if(errorObjects != null)
    {
      for(int i = 0; i < errorObjects.size(); i++)
      {
        Object errorObject = errorObjects.get(i);
        Class<? extends Object> errorObjClass = errorObject.getClass();

        //if the error obj class and the returned value object match...
        String errorObjClassName = errorObjClass.getName();
        errorObjClassName = errorObjClassName.substring(errorObjClassName.lastIndexOf(".") + 1);
        if(errorObjClassName.equals(getObjDef.getReturnedValueObject()))
        {
          //then check the guid value
          try {
            Method getGuidMethod = errorObjClass.getMethod("getGuid");
            String errorObjGuid = (String)getGuidMethod.invoke(errorObject);
            // if guids match, return the error obj instance
            if(errorObjGuid != null && errorObjGuid.equals(guid)) 
              return this.createObject(errorObject);

          // if exception, then just continue
          } catch(NoSuchMethodException nsme) {
          } catch(IllegalAccessException iae) {
          } catch(InvocationTargetException iae) {
          }
        }
      }
    }
    
    return null;
  }
  
  /**
   * Loads the transformer based on the type of transform requested.
   * 
   * @param type
   * 
   * @return XMLTransformer
   * @throws StrutsCXException
  private IXMLTransformer getXMLTransformer(String type)
      throws XMLTransformerException {
    return XMLTransformerFactory.getInstance().getXMLTransformer(type);
  }
  */

  public GetObject[] getObjectsConfig() {
    return objectsConfig;
  }

  public void setObjectsConfig(GetObject[] objectsConfig) {
    this.objectsConfig = objectsConfig;
  }

  public void setId(String Id) {
    this.id = Id;
  }

  public String getId() {
    return this.id;
  }

  public void setTransforms(Map<String, List<Transform>> transforms) {
    this.transforms = transforms;
  }

  public Map<String, List<Transform>> getTransforms() {
    return this.transforms;
  }

  public boolean isRenderErrorObject() {
    return renderErrorObject;
  }

  public void setRenderErrorObject(boolean renderErrorObject) {
    this.renderErrorObject = renderErrorObject;
  }

  public boolean isScanUrls() {
    return scanUrls;
  }

  public void setScanUrls(boolean scanUrls) {
    this.scanUrls = scanUrls;
  }

  public String[] getControllerNames() {
    return controllerNames;
  }

  public void setControllerNames(String[] controllerNames) {
    this.controllerNames = controllerNames;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String[] getStyles() {
    return styles;
  }

  public void setStyles(String[] styles) {
    this.styles = styles;
  }

  public IDataProvider getDataProvider() {
    return dataProvider;
  }

  public void setDataProvider(IDataProvider dataProvider) {
    this.dataProvider = dataProvider;
  }

}
