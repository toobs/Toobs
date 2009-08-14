package org.toobsframework.pres.component.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.toobsframework.pres.base.XslManagerBase;
import org.toobsframework.pres.component.ComponentException;
import org.toobsframework.pres.component.ComponentInitializationException;
import org.toobsframework.pres.component.ComponentNotFoundException;
import org.toobsframework.pres.component.ComponentNotInitializedException;
import org.toobsframework.pres.component.config.Component;
import org.toobsframework.pres.component.config.Components;
import org.toobsframework.pres.component.config.ContentType;
import org.toobsframework.data.beanutil.converter.DateToStringConverter;
import org.toobsframework.exception.ParameterException;
import org.toobsframework.pres.component.dataprovider.api.DataProviderInitializationException;
import org.toobsframework.pres.component.dataprovider.api.IDataProvider;
import org.toobsframework.pres.component.dataprovider.manager.DataProviderNotFoundException;
import org.toobsframework.transformpipeline.domain.IXMLTransformerHelper;
import org.toobsframework.util.IRequest;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author pudney
 */
public final class ComponentManager extends XslManagerBase implements IComponentManager {

  private IDataProvider dataProvider;
  private ConcurrentHashMap<String, org.toobsframework.pres.component.Component> registry;

  private ComponentManager() throws ComponentInitializationException {
    log.info("Constructing new ComponentManager");
  }

  public void afterPropertiesSet() throws Exception {
    super.afterPropertiesSet();
    registry = new ConcurrentHashMap<String, org.toobsframework.pres.component.Component>();
    ConvertUtils.register(new DateToStringConverter(), String.class);

    loadConfig(Components.class);
  }

  public org.toobsframework.pres.component.Component getComponent(String Id)
      throws ComponentNotFoundException, ComponentInitializationException {

    if (isDoReload()) {
      this.loadConfig(Components.class);
    }
    if (!registry.containsKey(Id)) {
      throw new ComponentNotFoundException(Id);
    }
    return registry.get(Id);
  }

  public String renderComponent(
      IRequest request,
      org.toobsframework.pres.component.Component component,
      String contentType, 
      Map<String, Object> params, 
      Map<String, Object> paramsOut, 
      IXMLTransformerHelper transformerHelper, 
      boolean appendUrlScanner)
      throws ComponentNotInitializedException, ComponentException, ParameterException {
    return component.render(request, contentType, params, transformerHelper, paramsOut, null);
  }

  @Override
  protected void registerConfiguration(Object object, String fileName) {
    Components componentConfig = (Components) object;
    Component[] components = componentConfig.getComponent();
    if ((components != null) && (components.length > 0)) {
      Component comp = null;
      org.toobsframework.pres.component.Component uic = null;
      for (int j = 0; j < components.length; j++) {
        try {
          comp = components[j];
          uic = new org.toobsframework.pres.component.Component(comp.getId());
          configureComponent(comp, uic, dataProvider, fileName, registry);
          
          uic.setXmlTransformer(xmlTransformer);
          uic.setDefaultTransformer(defaultTransformer);
          uic.setHtmlTransformer(htmlTransformer);
          if (registry.containsKey(uic.getId()) && !isInitDone()) {
            log.warn("Overriding component with Id: " + uic.getId());
          }
          registry.put(uic.getId(), uic);
        } catch (Exception e) {
          log.warn("Error configuring and registering component " + comp.getId() + ": " + e.getMessage(), e);
        }
      }
    }
  }

  // Read from config file
  public static void configureComponent(Component comp,
      org.toobsframework.pres.component.Component uic, IDataProvider dataProvider, String fileName,
      Map<String, org.toobsframework.pres.component.Component> registry) throws DataProviderInitializationException, DataProviderNotFoundException, ComponentInitializationException {


    uic.setId(comp.getId());
    uic.setFileName(fileName);
    uic.setRenderErrorObject(comp.getRenderErrorObject());
    uic.setDataProvider(dataProvider);
    //Set object config property.
    uic.setObjectsConfig(comp.getGetObject());
    //Set component pipeline properties.
    Map<String, List<org.toobsframework.pres.component.Transform>> transforms = new HashMap<String, List<org.toobsframework.pres.component.Transform>>();
    Enumeration<ContentType> contentTypeEnum = comp.getPipeline().enumerateContentType();
    while (contentTypeEnum.hasMoreElements()) {
      List<org.toobsframework.pres.component.Transform> theseTransforms = new ArrayList<org.toobsframework.pres.component.Transform>();
      ContentType thisContentType = contentTypeEnum.nextElement();
      Enumeration<org.toobsframework.pres.component.config.Transform> transEnum = thisContentType.enumerateTransform();
      while (transEnum.hasMoreElements()) {
        org.toobsframework.pres.component.config.Transform thisTransformConfig = transEnum.nextElement();                  
        org.toobsframework.pres.component.Transform thisTransform = new org.toobsframework.pres.component.Transform();

        thisTransform.setTransformName(thisTransformConfig.getName());
        thisTransform.setTransformParams(thisTransformConfig.getParameters());

        theseTransforms.add(thisTransform);
      }
      String[] ctSplit = thisContentType.getContentType().split(";");
      for (int ct = 0; ct < ctSplit.length; ct++) {
        transforms.put(ctSplit[ct], theseTransforms);
      }
    }
    uic.setTransforms(transforms);
    uic.setControllerNames(new String[comp.getControllerCount()]);
    for (int c=0; c < comp.getControllerCount(); c++) {
      uic.getControllerNames()[c] = comp.getController(c).getName();
    }
    uic.setStyles(new String[comp.getStyleCount()]);
    for (int c=0; c < comp.getStyleCount(); c++) {
      uic.getStyles()[c] = comp.getStyle(c).getName();
    }
    uic.init();
    
  }

  /**
   * @param dataProvider the dataProvider to set
   */
  public void setDataProvider(IDataProvider dataProvider) {
    this.dataProvider = dataProvider;
  }

}
