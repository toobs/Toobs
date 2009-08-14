package org.toobsframework.pres.layout.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.toobsframework.pres.layout.ComponentLayoutInitializationException;
import org.toobsframework.pres.layout.ComponentLayoutNotFoundException;
import org.toobsframework.pres.base.XslManagerBase;
import org.toobsframework.pres.component.config.ContentType;
import org.toobsframework.pres.layout.RuntimeLayout;
import org.toobsframework.pres.layout.RuntimeLayoutConfig;
import org.toobsframework.pres.layout.config.ComponentRef;
import org.toobsframework.pres.layout.config.Layout;
import org.toobsframework.pres.layout.config.Layouts;
import org.toobsframework.pres.layout.config.Section;
import org.toobsframework.transformpipeline.domain.IXMLTransformer;
import org.toobsframework.exception.PermissionException;

public final class ComponentLayoutManager extends XslManagerBase implements IComponentLayoutManager {

  private Map<String, RuntimeLayout> runtimeRegistry;
  private Map<String, LayoutConfigHolder> configRegistry;

  private ComponentLayoutManager() throws ComponentLayoutInitializationException {
    log.info("Constructing new ComponentLayoutManager");
  }

  // Read from config file
  public void afterPropertiesSet() throws Exception {
    super.afterPropertiesSet();
    configRegistry = new LinkedHashMap<String, LayoutConfigHolder>();
    loadConfig(Layouts.class);
    configureRegistry();
  }

  public RuntimeLayout getLayout(String Id)
      throws ComponentLayoutNotFoundException, ComponentLayoutInitializationException {
    if (isDoReload()) {
      Date initStart = new Date();
      this.loadConfig(Layouts.class);
      configureRegistry();
      Date initEnd = new Date();
      log.info("Init Time: " + (initEnd.getTime() - initStart.getTime()));
    }
    if (!runtimeRegistry.containsKey(Id)) {
      ComponentLayoutNotFoundException ex = new ComponentLayoutNotFoundException();
      ex.setComponentLayoutId(Id);
      throw ex;
    }
    return (RuntimeLayout) runtimeRegistry.get(Id);
  }
  
  public RuntimeLayout getLayout(PermissionException permissionException)
    throws ComponentLayoutNotFoundException, ComponentLayoutInitializationException {
    String objectErrorPage = permissionException.getAction() + permissionException.getObjectTypeName();
    if (!runtimeRegistry.containsKey(objectErrorPage)) {
      log.info("Permission Error page " + objectErrorPage + " not defined");
      return null;
    }
    return (RuntimeLayout) runtimeRegistry.get(objectErrorPage);
  }

  @Override
  protected void registerConfiguration(Object object, String fileName) {
    Layouts componentLayoutConfig = (Layouts) object;
    Layout[] layouts = componentLayoutConfig.getLayout();
    if ((layouts != null) && (layouts.length > 0)) {
      for (int j = 0; j < layouts.length; j ++) {
        try {
          if (configRegistry.containsKey(layouts[j].getId()) && !isInitDone()) {
            log.warn("Overriding layout with Id: " + layouts[j].getId());
          }
          configRegistry.put(layouts[j].getId(), new LayoutConfigHolder(layouts[j]));
        } catch (Exception e) {
          log.warn("Error configuring and registering component " + layouts[j].getId() + ": " + e.getMessage(), e);
        }
      }
    }
  }

  private void configureRegistry() throws ComponentLayoutInitializationException {
    runtimeRegistry = new HashMap<String, RuntimeLayout>();
    for (Map.Entry<String,LayoutConfigHolder> configEntry : configRegistry.entrySet()) {
      LayoutConfigHolder holder = configEntry.getValue();
      if (!holder.configured || !runtimeRegistry.containsKey(configEntry.getKey())) {
        processLayoutConfig(holder, null);
      }
    }
    if (!isDoReload()) {
      configRegistry.clear();
      configRegistry = null;
    }
  }

  private void processLayoutConfig(LayoutConfigHolder configHolder, String childName) throws ComponentLayoutInitializationException {
    String extend = configHolder.layout.getExtend();

    // Check to see if there is a parent and it has been initialized
    if (extend != null) {
      LayoutConfigHolder parentConfigHolder = configRegistry.get(extend);
      if (parentConfigHolder != null) {
        if (!runtimeRegistry.containsKey(extend)) {
          processLayoutConfig(parentConfigHolder, extend);
        }
        parentConfigHolder.addChild(extend);
      } else {
        log.warn("Extension layout " + extend + " not found for layout " + configHolder.layout.getId());
      }
    }

    RuntimeLayout runtimeLayout = new RuntimeLayout();
    configureLayout(configHolder.layout, runtimeLayout, defaultTransformer, htmlTransformer, xmlTransformer);
    runtimeRegistry.put(runtimeLayout.getId(), runtimeLayout);
    configHolder.configured = true;

    if (childName != null) {
      configHolder.addChild(extend);
    }
  }

  public void configureLayout(
      Layout compLayout, 
      RuntimeLayout layout, 
      IXMLTransformer defaultTransformer, 
      IXMLTransformer htmlTransformer, 
      IXMLTransformer xmlTransformer) throws ComponentLayoutInitializationException {

    RuntimeLayoutConfig layoutConfig = new RuntimeLayoutConfig();
    List<Section> tempSections = new ArrayList<Section>();

    // Inherited from extended definition
    String extendStr = compLayout.getExtend();
    if (extendStr != null) {
      String[] extSplit = extendStr.split(";");
      for (int ext = 0; ext < extSplit.length; ext++) {
        String extension = extSplit[ext];
        RuntimeLayout extend = runtimeRegistry.get(extension);
        if (extend == null) {
          log.error("The Layout extension " + extension + " for " + compLayout.getId() + 
              " could not be located in the registry.\n"
              + "Check the spelling and case of the extends property and ensure it is defined before\n"
              + "the dependent templates");
          throw new ComponentLayoutInitializationException("Missing extension " + extension + " for " + compLayout.getId());
        }
        RuntimeLayoutConfig extendConfig = extend.getConfig();
        if (extend == null) {
          throw new ComponentLayoutInitializationException("Layout " + compLayout.getId() + 
              " cannot extend " + extension + " cause it does not exist or has not yet been loaded");
        }
        layoutConfig.addParam(extendConfig.getAllParams());
        layoutConfig.addTransformParam(extendConfig.getAllTransformParams());

        tempSections.addAll(extendConfig.getAllSections());
        
        //layoutConfig.addSection(extendConfig.getAllSections());
        layoutConfig.setNoAccessLayout(extendConfig.getNoAccessLayout());
        //layout.addTransform(extend.getAllTransforms());
        layout.getTransforms().putAll(extend.getTransforms());
        //layout.setUseComponentScan(extend.isUseComponentScan());
        //layout.setEmbedded(extend.isEmbedded());
      }
    }
    
    if (compLayout.getParameters() != null) {
      layoutConfig.addParam(compLayout.getParameters().getParameter());
    }
    if (compLayout.getTransformParameters() != null) {
      layoutConfig.addTransformParam(compLayout.getTransformParameters().getParameter());
    }
    for (Section sec : compLayout.getSection()) {
      tempSections.add(sortComponents(sec));
    }

    sortSections(tempSections);
    layoutConfig.addSection(tempSections);
    //layoutConfig.addSection(compLayout.getSection());

    if (compLayout.getNoAccessLayout() != null) {
      layoutConfig.setNoAccessLayout(compLayout.getNoAccessLayout());
    }
    layout.setId(compLayout.getId());
    //layout.setUseComponentScan(compLayout.getUseComponentScan() || layout.isEmbedded());
    //layout.setEmbedded(compLayout.getEmbedded() || layout.isEmbedded());
    
    //Set component pipeline properties.
    if (compLayout.getPipeline() != null) {
      Enumeration<ContentType> contentTypeEnum = compLayout.getPipeline().enumerateContentType();
      while (contentTypeEnum.hasMoreElements()) {
        List<org.toobsframework.pres.component.Transform> theseTransforms = new ArrayList<org.toobsframework.pres.component.Transform>();
        ContentType thisContentType = (ContentType) contentTypeEnum.nextElement();
        Enumeration<org.toobsframework.pres.component.config.Transform> transEnum = thisContentType.enumerateTransform();
        while (transEnum.hasMoreElements()) {
          org.toobsframework.pres.component.config.Transform thisTransformConfig = (org.toobsframework.pres.component.config.Transform) transEnum.nextElement();                  
          org.toobsframework.pres.component.Transform thisTransform = new org.toobsframework.pres.component.Transform();

          thisTransform.setTransformName(thisTransformConfig.getName());
          thisTransform.setTransformParams(thisTransformConfig.getParameters());

          theseTransforms.add(thisTransform);
        }
        String[] ctSplit = thisContentType.getContentType().split(";");
        for (int ct = 0; ct < ctSplit.length; ct++) {
          layout.getTransforms().put(ctSplit[ct], theseTransforms);
        }
      }
    }
    /*
    if (compLayout.getTransformCount() > 0) {
      layout.getTransforms().clear();
      for (int t = 0; t < compLayout.getTransformCount(); t++) {
        layout.addTransform(new Transform(compLayout.getTransform(t)));
      }
    }
    */
    layout.setConfig(layoutConfig);
    
    layout.setDoItRef(compLayout.getDoItRef());
    layout.setHtmlTransformer(htmlTransformer);
    layout.setDefaultTransformer(defaultTransformer);
    layout.setXmlTransformer(xmlTransformer);
    
    if (log.isDebugEnabled()) {
      log.debug("Layout " + compLayout.getId() + " xml " + layout.getLayoutXml());
    }
    
  }

  private static Section sortComponents(Section sec) {
    List<ComponentRef> compRefs = Arrays.asList( sec.getComponentRef() );

    Collections.sort(compRefs, new Comparator<ComponentRef>() {
      public int compare(ComponentRef o1, ComponentRef o2) {
        return o1.getOrder() - o2.getOrder();
      }
    });

    ComponentRef[] sortedRefs = new ComponentRef[compRefs.size()];
    sec.setComponentRef(compRefs.toArray(sortedRefs));
    return sec;
  }

  private static void sortSections(List<Section> tempSections) {
    Collections.sort(tempSections, new Comparator<Section>() {
      public int compare(Section o1, Section o2) {
        return o1.getOrder() - o2.getOrder();
      }
    });
  }

  private class LayoutConfigHolder {
    boolean configured;
    Layout layout;
    Set<String> children;
    private LayoutConfigHolder(Layout layout) {
      this.layout = layout;
    }

    public Set<String> getChildren() {
      return children;
    }
    public void addChild(String child) {
      if (children == null) {
        children = new HashSet<String>();
      }
      this.children.add(child);
    }

    public boolean isConfigured() {
      return configured;
    }

    public void setConfigured(boolean configured) {
      this.configured = configured;
    }
    
  }
}