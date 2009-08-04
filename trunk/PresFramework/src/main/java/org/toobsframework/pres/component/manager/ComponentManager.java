package org.toobsframework.pres.component.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xalan.trace.TraceListener;
import org.exolab.castor.xml.Unmarshaller;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.toobsframework.pres.base.ManagerBase;
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
import org.toobsframework.pres.resources.IResourceCacheLoader;
import org.toobsframework.pres.resources.ResourceCacheDescriptor;
import org.toobsframework.pres.resources.ResourceUnmarshaller;
import org.toobsframework.transformpipeline.domain.IXMLTransformer;
import org.toobsframework.transformpipeline.domain.IXMLTransformerHelper;
import org.toobsframework.transformpipeline.domain.XMLTransformerException;
import org.toobsframework.transformpipeline.domain.XMLTransformerFactory;
import org.toobsframework.transformpipeline.domain.XSLUriResolverImpl;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.URIResolver;


/**
 * @author pudney
 */
public final class ComponentManager extends ManagerBase implements IComponentManager {

  private static Log log = LogFactory.getLog(ComponentManager.class);

  private long localDeployTime = 0L;

  private IDataProvider dataProvider;

  private URIResolver uriResolver;

  private boolean useTranslets = false;
  private boolean useChain = false;

  private IXMLTransformer defaultTransformer;
  private IXMLTransformer htmlTransformer;
  private IXMLTransformer xmlTransformer;
  private TraceListener paramListener;
  private ConcurrentHashMap<String, org.toobsframework.pres.component.Component> registry;

  private ComponentManager() throws ComponentInitializationException {
    log.info("Constructing new ComponentManager");
    registry = new ConcurrentHashMap<String, org.toobsframework.pres.component.Component>();
    setUriResolver(new XSLUriResolverImpl());
    ConvertUtils.register(new DateToStringConverter(), String.class);
  }

  public void init() throws ComponentInitializationException, XMLTransformerException {
    XMLTransformerFactory.getInstance().setUseChain(useChain);
    XMLTransformerFactory.getInstance().setUseTranslets(useTranslets);

    xmlTransformer = XMLTransformerFactory.getInstance().getChainTransformer(XMLTransformerFactory.OUTPUT_FORMAT_XML, uriResolver, paramListener);
    htmlTransformer = XMLTransformerFactory.getInstance().getChainTransformer(XMLTransformerFactory.OUTPUT_FORMAT_HTML, uriResolver, paramListener);
    defaultTransformer = XMLTransformerFactory.getInstance().getDefaultTransformer(uriResolver);

    loadConfig(Components.class);
  }

  public org.toobsframework.pres.component.Component getComponent(String Id, long deployTime)
      throws ComponentNotFoundException, ComponentInitializationException {

    if (isDoReload() || deployTime > localDeployTime) {
      this.loadConfig(Components.class);
    }
    if (!registry.containsKey(Id)) {
      throw new ComponentNotFoundException(Id);
    }
    localDeployTime = deployTime;
    return registry.get(Id);
  }

  public String renderComponent(
      org.toobsframework.pres.component.Component component,
      String contentType, Map<String, Object> params, Map<String, Object> paramsOut, 
      IXMLTransformerHelper transformerHelper, HttpServletRequest request, HttpServletResponse response, boolean appendUrlScanner)
      throws ComponentNotInitializedException, ComponentException, ParameterException {
    return component.render(contentType, params, transformerHelper, request, response, paramsOut, null);
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
          uic = new org.toobsframework.pres.component.Component();
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

  public void setUseTranslets(boolean useTranslets) {
    this.useTranslets = useTranslets;
  }

  public boolean isUseTranslets() {
    return useTranslets;
  }

  public void setUseChain(boolean useChain) {
    this.useChain = useChain;
  }

  public boolean isUseChain() {
    return useChain;
  }

  public void setUriResolver(URIResolver uriResolver) {
    this.uriResolver = uriResolver;
  }

  public URIResolver getUriResolver() {
    return uriResolver;
  }

  public void setParamListener(TraceListener paramListener) {
    this.paramListener = paramListener;
  }

  public TraceListener getParamListener() {
    return paramListener;
  }

  /**
   * @return the dataProvider
   */
  public IDataProvider getDataProvider() {
    return dataProvider;
  }

  /**
   * @param dataProvider the dataProvider to set
   */
  public void setDataProvider(IDataProvider dataProvider) {
    this.dataProvider = dataProvider;
  }

}
