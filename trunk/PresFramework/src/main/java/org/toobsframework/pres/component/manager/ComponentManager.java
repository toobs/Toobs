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
import org.toobsframework.pres.component.ComponentException;
import org.toobsframework.pres.component.ComponentInitializationException;
import org.toobsframework.pres.component.ComponentNotFoundException;
import org.toobsframework.pres.component.ComponentNotInitializedException;
import org.toobsframework.pres.component.config.Component;
import org.toobsframework.pres.component.config.Components;
import org.toobsframework.pres.component.config.ContentType;
import org.toobsframework.data.beanutil.converter.DateToStringConverter;
import org.toobsframework.exception.ParameterException;
import org.toobsframework.pres.component.datasource.api.DataSourceInitializationException;
import org.toobsframework.pres.component.datasource.manager.DataSourceNotFoundException;
import org.toobsframework.transformpipeline.domain.IXMLTransformer;
import org.toobsframework.transformpipeline.domain.XMLTransformerException;
import org.toobsframework.transformpipeline.domain.XMLTransformerFactory;
import org.toobsframework.transformpipeline.domain.XSLUriResolverImpl;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.transform.URIResolver;


/**
 * @author pudney
 */
public final class ComponentManager implements IComponentManager {

  private static Log log = LogFactory.getLog(ComponentManager.class);

  private static Map<String, org.toobsframework.pres.component.Component> registry;
  private static boolean initDone = false;
  private static long[] lastModified;
  private static long localDeployTime = 0L;

  private List<String> configFiles = null;
  private boolean doReload = false;

  private URIResolver uriResolver;

  private boolean useTranslets = false;
  private boolean useChain = false;

  private IXMLTransformer defaultTransformer;
  private IXMLTransformer htmlTransformer;
  private IXMLTransformer xmlTransformer;
  private TraceListener paramListener;

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

    loadConfig();
  }

  public org.toobsframework.pres.component.Component getComponent(String Id, long deployTime)
      throws ComponentNotFoundException, ComponentInitializationException {

    if (doReload || deployTime > localDeployTime) {
      this.loadConfig();
    }
    if (!registry.containsKey(Id)) {
      throw new ComponentNotFoundException(Id);
    }
    localDeployTime = deployTime;
    return registry.get(Id);
  }

  public String renderComponent(
      org.toobsframework.pres.component.Component component,
      String contentType, Map<String, Object> params, Map<String, Object> paramsOut, boolean appendUrlScanner)
      throws ComponentNotInitializedException, ComponentException, ParameterException {
    return component.render(contentType, params, paramsOut);
  }

  // Read from config file
  public void loadConfig() throws ComponentInitializationException {
    InputStreamReader reader = null;
    if(configFiles == null) {
      return;
    }
    int l = configFiles.size();
    if (lastModified == null) {
      log.info("LastModified is null " + this.toString() + " Registry " + registry);
      lastModified = new long[l];
    }
    for(int fileCounter = 0; fileCounter < l; fileCounter++) {
      String fileName = configFiles.get(fileCounter);
      try {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL configFileURL = classLoader.getResource(fileName);
        if (configFileURL == null) {
          log.warn("Skipping missing Components file [" + fileName + "]");
          continue;
        }
        File configFile = new File(configFileURL.getFile());
        if (configFile.lastModified() <= lastModified[fileCounter]) {
          continue;
        }
        log.info("Reloading Components file [" + fileName + "]");
        //registry.clear();
        reader = new InputStreamReader(configFileURL.openStream());
        Unmarshaller unmarshaller = new Unmarshaller(
            Class.forName(Components.class.getName()));
        unmarshaller.setValidation(false);
        Components componentConfig = (Components) unmarshaller.unmarshal(reader);
        Component[] components = componentConfig.getComponent();
        if ((components != null) && (components.length > 0)) {
          Component comp = null;
          org.toobsframework.pres.component.Component uic = null;
          for (int i = 0; i < components.length; i++) {
            comp = components[i];
            uic = new org.toobsframework.pres.component.Component();

            configureComponent(comp, uic, fileName, registry);
            uic.setXmlTransformer(xmlTransformer);
            uic.setDefaultTransformer(defaultTransformer);
            uic.setHtmlTransformer(htmlTransformer);

            if (registry.containsKey(uic.getId()) && !initDone) {
              log.warn("Overriding component with Id: " + uic.getId());
            }
            registry.put(uic.getId(), uic);
          }
        }
        //doReload = Configuration.getInstance().getReloadComponents();
        lastModified[fileCounter] = configFile.lastModified();
        doReload = false;
      } catch (Exception ex) {
        log.error("ComponentLayout initialization failed " + ex.getMessage(), ex);
        doReload = true;
      } finally {
        if (reader != null) {
          try {
            reader.close();
          } catch (IOException e) {
          }
        }
      }
    }
    initDone = true;
  }

  public static void configureComponent(Component comp,
      org.toobsframework.pres.component.Component uic, String fileName, Map<String, org.toobsframework.pres.component.Component> registry) throws DataSourceInitializationException, DataSourceNotFoundException, ComponentInitializationException {


    uic.setId(comp.getId());
    uic.setFileName(fileName);
    uic.setRenderErrorObject(comp.getRenderErrorObject());
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

  public List<String> getConfigFiles() {
    return configFiles;
  }

  public void setConfigFiles(List<String> configFiles) {
    this.configFiles = configFiles;
  }

  public void addConfigFiles(List<String> configFiles) {
    this.configFiles.addAll(configFiles);
  }

  public boolean isDoReload() {
    return doReload;
  }

  public void setDoReload(boolean doReload) {
    this.doReload = doReload;
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

}
