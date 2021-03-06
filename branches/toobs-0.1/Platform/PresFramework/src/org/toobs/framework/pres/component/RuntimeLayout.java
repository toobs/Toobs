package org.toobs.framework.pres.component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.toobs.framework.exception.ParameterException;
import org.toobs.framework.pres.component.config.Parameter;
import org.toobs.framework.pres.componentlayout.config.DoItRef;
import org.toobs.framework.pres.componentlayout.config.Section;
import org.toobs.framework.pres.util.ParameterUtil;
import org.toobs.framework.pres.util.PresConstants;
import org.toobs.framework.transformpipeline.domain.IXMLTransformer;
import org.toobs.framework.transformpipeline.domain.XMLTransformerException;
import org.toobs.framework.transformpipeline.domain.XMLTransformerFactory;
import org.toobs.framework.util.BetwixtUtil;
import org.toobs.framework.util.Configuration;
import org.toobs.framework.util.IRequest;


@SuppressWarnings("unchecked")
public class RuntimeLayout {
  private static final String XML_HEADER = "<RuntimeLayout>";
  private static final String XML_FOOTER = "</RuntimeLayout>";
  private static final String XML_CP_HEADER = "<ContentParams>";
  private static final String XML_CP_FOOTER = "</ContentParams>";
  private String id;
  private Map transforms = new HashMap();
  private RuntimeLayoutConfig config;
  private String layoutXml;
  private DoItRef doItRef;
  private boolean useComponentScan;
  private boolean embedded;
  
  public RuntimeLayoutConfig getConfig() {
    return config;
  }
  public void setConfig(RuntimeLayoutConfig config) throws IOException {
    this.config = config;
    StringBuffer sb = new StringBuffer();
    sb.append(XML_HEADER);
    Parameter[] contentParams = config.getAllContentParams();
    if (contentParams.length > 0) {
      sb.append(XML_CP_HEADER);
      for (int c = 0; c < contentParams.length; c++) {
        sb.append(BetwixtUtil.toXml(contentParams[c], true, false, false, null, null));
      }
      sb.append(XML_CP_FOOTER);
    }
    //sb.append(BetwixtUtil.toXml(config.getAllParams()));
    Section[] sections = config.getAllSections();
    for (int s = 0; s < sections.length; s++) {
      sb.append(BetwixtUtil.toXml(sections[s], true, false, false, null, null));
    }
    sb.append(XML_FOOTER);
    this.setLayoutXml(sb.toString());
  }
  /*
  public ArrayList getTransforms() {
    return transforms;
  }
  public Transform[] getAllTransforms() {
    Transform[] allTransforms = new Transform[transforms.size()];
    return (Transform[])transforms.toArray(allTransforms);
  }
  public void setTransforms(ArrayList transforms) {
    this.transforms = transforms;
  }
  public void addTransform(Transform transform) {
    addTransform(new Transform[] {transform});
  }
  public void addTransform(Transform[] transform) {
    for (int i = 0; i < transform.length; i++) {
      transforms.add(transform[i]);
    }
  }
  */
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  
  public String getLayoutXml() {
    return layoutXml;
  }
  public void setLayoutXml(String layoutXml) {
    this.layoutXml = layoutXml;
  }
  
  public String render(IRequest request, boolean embedded) throws ComponentException, ParameterException {
    return this.render(request, embedded, false, "xhtml");  
  }
  
  public String render(IRequest request, boolean embedded, boolean skipUrlScan) throws ComponentException, ParameterException {
    return this.render(request, embedded, skipUrlScan, "xhtml");
  }
  
  public String render(IRequest request, boolean embedded, boolean skipUrlScan, String contentType) throws ComponentException, ParameterException {
    IXMLTransformer xmlTransformer = null;
    StringBuffer outputString = new StringBuffer();
    HashMap layoutParams = new HashMap();
    
    Vector outputXML = new Vector();
    try {
      Vector inputXSLs = new Vector();
      Vector inputXMLs = new Vector();
      Vector contentTransforms = (Vector) this.getTransforms().get(contentType);
      if (contentTransforms != null && contentTransforms.size() > 0) {
        Iterator it = contentTransforms.iterator();
        while (it.hasNext()) {
          Transform transform = (Transform) it.next();
          inputXSLs.add(transform.getTransformName());
          //Fix the params using the param mapping for 
          //this configuration.
          if(transform.getTransformParams() != null){
            ParameterUtil.mapParameters("Transform:" + transform.getTransformName(), transform.getTransformParams().getParameter(), request.getParams(), layoutParams, this.id);
          }
        }
      } else {
        throw new ComponentException("Component Layout with id: " + this.id + " does not have a transform for content type: " + contentType);
      }
      /*
      for (int t = 0; t < transforms.size(); t++) {
        Transform trans = (Transform)transforms.get(t);
        inputXSLs.add(trans.getTransformName());
        if (trans.getTransformParams() != null) {
          ParameterUtil.mapParameters("Transform:" + trans.getTransformName(), trans.getTransformParams().getParameter(), request.getParams(), layoutParams, this.id);
        }
      }
      */
      if (!skipUrlScan) {
        if (embedded) {
          inputXSLs.add(PresConstants.XSL_URL_SCANNER_XML);
          xmlTransformer = XMLTransformerFactory.getInstance().getChainTransformer(XMLTransformerFactory.OUTPUT_FORMAT_XML);
          if (request.getParams().get("outputFormat") != null) {
            layoutParams.put("outputFormat", request.getParams().get("outputFormat"));
          }
        } else if (useComponentScan) {
          inputXSLs.add(PresConstants.XSL_URL_SCANNER_COMP);
          xmlTransformer = XMLTransformerFactory.getInstance().getChainTransformer(XMLTransformerFactory.OUTPUT_FORMAT_HTML);
        } else {
          inputXSLs.add(PresConstants.XSL_URL_SCANNER_LAYOUT);
          xmlTransformer = XMLTransformerFactory.getInstance().getChainTransformer(XMLTransformerFactory.OUTPUT_FORMAT_HTML);
        }
      } else {
        xmlTransformer = XMLTransformerFactory.getInstance().getDefaultTransformer();
      }
      ParameterUtil.mapParameters("Layout:" + this.id, config.getAllParams(), request.getParams(), layoutParams, this.id);

      inputXMLs.add(this.layoutXml);
      layoutParams.put("context", Configuration.getInstance().getMainContext() + "/");
      if (request.getParams().get("appContext") != null) {
        layoutParams.put("appContext", request.getParams().get("appContext"));
      }
      outputXML = xmlTransformer.transform(inputXSLs, inputXMLs, layoutParams);
    } catch (XMLTransformerException e) {
      throw new ComponentException(e);
    }

    for (int ox = 0; ox < outputXML.size(); ox++) {
      outputString.append((String) outputXML.get(ox));
    }
    // Return
    return outputString.toString();
  }

  public DoItRef getDoItRef() {
    return doItRef;
  }
  public void setDoItRef(DoItRef doItRef) {
    this.doItRef = doItRef;
  }
  public boolean isUseComponentScan() {
    return useComponentScan;
  }
  public void setUseComponentScan(boolean useComponentScan) {
    this.useComponentScan = useComponentScan;
  }
  public boolean isEmbedded() {
    return embedded;
  }
  public void setEmbedded(boolean embedded) {
    this.embedded = embedded;
  }
  public Map getTransforms() {
    return transforms;
  }
  public void setTransforms(Map transforms) {
    this.transforms = transforms;
  }
}
