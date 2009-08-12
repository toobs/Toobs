package org.toobsframework.pres.layout.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;

import javax.xml.transform.URIResolver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xalan.trace.TraceListener;
import org.springframework.beans.factory.InitializingBean;
import org.toobsframework.pres.layout.ComponentLayoutInitializationException;
import org.toobsframework.pres.layout.ComponentLayoutNotFoundException;
import org.toobsframework.pres.base.ManagerBase;
import org.toobsframework.pres.component.config.ContentType;
import org.toobsframework.pres.layout.RuntimeLayout;
import org.toobsframework.pres.layout.RuntimeLayoutConfig;
import org.toobsframework.pres.layout.config.ComponentRef;
import org.toobsframework.pres.layout.config.Layout;
import org.toobsframework.pres.layout.config.Layouts;
import org.toobsframework.pres.layout.config.Section;
import org.toobsframework.transformpipeline.domain.IXMLTransformer;
import org.toobsframework.transformpipeline.domain.XMLTransformerException;
import org.toobsframework.transformpipeline.domain.XMLTransformerFactory;
import org.toobsframework.transformpipeline.domain.XSLUriResolverImpl;
import org.toobsframework.exception.PermissionException;

public final class ComponentLayoutManager extends ManagerBase implements IComponentLayoutManager, InitializingBean {

  private static Log log = LogFactory.getLog(ComponentLayoutManager.class);

  private Map<String, RuntimeLayout> registry;
  private long localDeployTime = 0L;

  private boolean useTranslets = false;
  private boolean useChain = false;

  private URIResolver xslResolver;
  private IXMLTransformer defaultTransformer;
  private IXMLTransformer htmlTransformer;
  private IXMLTransformer xmlTransformer;
  private TraceListener paramListener;

  private ComponentLayoutManager() throws ComponentLayoutInitializationException {
    log.info("Constructing new ComponentLayoutManager");
  }

  // Read from config file
  public void afterPropertiesSet() throws ComponentLayoutInitializationException, XMLTransformerException {
    XMLTransformerFactory.getInstance().setUseChain(useChain);
    XMLTransformerFactory.getInstance().setUseTranslets(useTranslets);

    xmlTransformer = XMLTransformerFactory.getInstance().getChainTransformer(XMLTransformerFactory.OUTPUT_FORMAT_XML, xslResolver, paramListener);
    htmlTransformer = XMLTransformerFactory.getInstance().getChainTransformer(XMLTransformerFactory.OUTPUT_FORMAT_HTML, xslResolver, paramListener);
    defaultTransformer = XMLTransformerFactory.getInstance().getDefaultTransformer(xslResolver);

    registry = new HashMap<String, RuntimeLayout>();
    if (this.xslResolver == null) {
      this.xslResolver = new XSLUriResolverImpl();
    }

    loadConfig(Layouts.class);
  }

  public RuntimeLayout getLayout(String Id, long deployTime)
      throws ComponentLayoutNotFoundException, ComponentLayoutInitializationException {
    if (isDoReload() || deployTime > localDeployTime) {
      Date initStart = new Date();
      this.loadConfig(Layouts.class);
      Date initEnd = new Date();
      log.info("Init Time: " + (initEnd.getTime() - initStart.getTime()));
    }
    if (!registry.containsKey(Id)) {
      ComponentLayoutNotFoundException ex = new ComponentLayoutNotFoundException();
      ex.setComponentLayoutId(Id);
      throw ex;
    }
    localDeployTime = deployTime;
    return (RuntimeLayout) registry.get(Id);
  }
  
  public RuntimeLayout getLayout(PermissionException permissionException)
    throws ComponentLayoutNotFoundException, ComponentLayoutInitializationException {
    String objectErrorPage = permissionException.getAction() + permissionException.getObjectTypeName();
    if (!registry.containsKey(objectErrorPage)) {
      log.info("Permission Error page " + objectErrorPage + " not defined");
      return null;
    }
    return (RuntimeLayout) registry.get(objectErrorPage);
  }

  @Override
  protected void registerConfiguration(Object object, String fileName) {
    Layouts componentLayoutConfig = (Layouts) object;
    Layout[] layouts = componentLayoutConfig.getLayout();
    if ((layouts != null) && (layouts.length > 0)) {
      Layout compLayout = null;
      RuntimeLayout layout = null;
      for (int j = 0; j < layouts.length; j ++) {
        try {
          compLayout = layouts[j];
          layout = new RuntimeLayout();
          configureLayout(compLayout, layout, defaultTransformer, htmlTransformer, xmlTransformer, registry);
        
          if (registry.containsKey(compLayout.getId()) && !isInitDone()) {
            log.warn("Overriding layout with Id: " + compLayout.getId());
          }
          registry.put(compLayout.getId(), layout);
        } catch (Exception e) {
          log.warn("Error configuring and registering component " + compLayout.getId() + ": " + e.getMessage(), e);
        }
      }
    }
  }
  public static void configureLayout(Layout compLayout, RuntimeLayout layout, 
      IXMLTransformer defaultTransformer, IXMLTransformer htmlTransformer, IXMLTransformer xmlTransformer, 
      Map<String, RuntimeLayout> registry) throws ComponentLayoutInitializationException, IOException {
    RuntimeLayoutConfig layoutConfig = new RuntimeLayoutConfig();
    List<Section> tempSections = new ArrayList<Section>();

    // Inherited from extended definition
    String extendStr = compLayout.getExtend();
    if (extendStr != null) {
      String[] extSplit = extendStr.split(";");
      for (int ext = 0; ext < extSplit.length; ext++) {
        String extension = extSplit[ext];
        RuntimeLayout extend = registry.get(extension);
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

  public void setParamListener(TraceListener paramListener) {
    this.paramListener = paramListener;
  }

  public void setXslResolver(URIResolver xslResolver) {
    this.xslResolver = xslResolver;
  }

  public void setUseTranslets(boolean useTranslets) {
    this.useTranslets = useTranslets;
  }

  public void setUseChain(boolean useChain) {
    this.useChain = useChain;
  }

}