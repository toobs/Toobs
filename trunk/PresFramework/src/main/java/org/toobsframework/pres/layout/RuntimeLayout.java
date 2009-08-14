package org.toobsframework.pres.layout;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobsframework.pres.component.ComponentException;
import org.toobsframework.pres.component.Transform;
import org.toobsframework.pres.component.config.Parameter;
import org.toobsframework.pres.layout.config.ComponentRef;
import org.toobsframework.pres.layout.config.DoItRef;
import org.toobsframework.pres.layout.config.Section;
import org.toobsframework.exception.ParameterException;
import org.toobsframework.pres.util.ParameterUtil;
import org.toobsframework.transformpipeline.domain.IXMLTransformer;
import org.toobsframework.transformpipeline.domain.IXMLTransformerHelper;
import org.toobsframework.transformpipeline.domain.XMLTransformerException;
import org.toobsframework.util.BetwixtUtil;
import org.toobsframework.util.IRequest;

public class RuntimeLayout {
  private static final Log log = LogFactory.getLog(RuntimeLayout.class);

  private static final String XML_HEADER = "<layout>";
  private static final String XML_FOOTER = "</layout>";
  private static final String XML_CP_HEADER = "<transform-params>";
  private static final String XML_CP_FOOTER = "</transform-params>";

  private String id;
  private Map<String, List<Transform>> transforms = new HashMap<String, List<Transform>>();
  private RuntimeLayoutConfig config;
  private String layoutXml;
  private List<ComponentRef> compRefList;
  private DoItRef doItRef;

  private IXMLTransformer defaultTransformer;
  private IXMLTransformer htmlTransformer;
  private IXMLTransformer xmlTransformer;

  public RuntimeLayoutConfig getConfig() {
    return config;
  }
  public void setConfig(RuntimeLayoutConfig config) {
    this.config = config;
    StringBuffer sb = new StringBuffer();
    sb.append(XML_HEADER);
    Parameter[] contentParams = config.getAllTransformParams();
    try {
      if (contentParams.length > 0) {
        sb.append(XML_CP_HEADER);
        for (int c = 0; c < contentParams.length; c++) {
          sb.append(BetwixtUtil.toXml(contentParams[c], true, false, false, null, null));
        }
        sb.append(XML_CP_FOOTER);
      }
      //sb.append(BetwixtUtil.toXml(config.getAllParams()));
      compRefList = new ArrayList<ComponentRef>();
      Collection<? extends Section> sections = config.getAllSections();
      for (Section sec : sections) {
        sb.append(BetwixtUtil.toXml(sec, true, false, false, null, null));
        compRefList.addAll(Arrays.asList( sec.getComponentRef() ) );
      }
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
    sb.append(XML_FOOTER);
    this.setLayoutXml(sb.toString());
  }

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

  public String render(IRequest request, IXMLTransformerHelper transformerHelper) throws ComponentException, ParameterException {
    return this.render(request, "xhtml", transformerHelper);  
  }

  public String render(IRequest request, String contentType, IXMLTransformerHelper transformerHelper) throws ComponentException, ParameterException {
    IXMLTransformer xmlTransformer = null;
    StringBuffer outputString = new StringBuffer();
    Map<String,Object> layoutParams = new HashMap<String,Object>();

    List<String> outputXML = new ArrayList<String>();
    try {
      List<String> inputXSLs = new ArrayList<String>();
      List<String> inputXMLs = new ArrayList<String>();
      List<Transform> contentTransforms = this.getTransforms().get(contentType);
      if (contentTransforms != null && contentTransforms.size() > 0) {
        Iterator<Transform> it = contentTransforms.iterator();
        while (it.hasNext()) {
          Transform transform = (Transform) it.next();
          inputXSLs.add(transform.getTransformName());
          //Fix the params using the param mapping for 
          //this configuration.
          if(transform.getTransformParams() != null){
            ParameterUtil.mapParameters(request, "Transform:" + transform.getTransformName(), transform.getTransformParams().getParameter(), request.getParams(), layoutParams, this.id);
          }
        }
      } else {
        throw new ComponentException("Component Layout with id: " + this.id + " does not have a transform for content type: " + contentType);
      }

      if (inputXSLs.size() > 1) {
        if (!"xhtml".equals(contentType)) {
          xmlTransformer = this.xmlTransformer;
        } else {
          xmlTransformer = this.htmlTransformer;
        }
      } else {
        xmlTransformer = this.defaultTransformer;
      }

      ParameterUtil.mapParameters(request, "Layout:" + this.id, config.getAllTransformParams(), request.getParams(), layoutParams, this.id);

      inputXMLs.add(this.layoutXml);
      layoutParams.put(IXMLTransformer.COMPONENT_REQUEST, request);
      outputXML = xmlTransformer.transform(inputXSLs, inputXMLs, layoutParams, transformerHelper);
    } catch (XMLTransformerException e) {
      throw new ComponentException(e);
    }

    for (int ox = 0; ox < outputXML.size(); ox++) {
      outputString.append((String) outputXML.get(ox));
    }
    // Return
    return outputString.toString();
  }

  public void renderStream(OutputStream stream, IRequest request, String contentType, IXMLTransformerHelper transformerHelper) throws ComponentException, ParameterException {
    IXMLTransformer xmlTransformer = null;

    Map<String,Object> layoutParams = new HashMap<String,Object>();

    try {
      List<String> inputXSLs = new ArrayList<String>();
      List<Transform> contentTransforms = this.getTransforms().get(contentType);
      if (contentTransforms != null && contentTransforms.size() > 0) {
        Iterator<Transform> it = contentTransforms.iterator();
        while (it.hasNext()) {
          Transform transform = (Transform) it.next();
          inputXSLs.add(transform.getTransformName());
          //Fix the params using the param mapping for 
          //this configuration.
          if(transform.getTransformParams() != null){
            ParameterUtil.mapParameters(request, "Transform:" + transform.getTransformName(), transform.getTransformParams().getParameter(), request.getParams(), layoutParams, this.id);
          }
        }
      } else {
        throw new ComponentException("Component Layout with id: " + this.id + " does not have a transform for content type: " + contentType);
      }

      if (inputXSLs.size() > 1) {
        if (!"xhtml".equals(contentType)) {
          xmlTransformer = this.xmlTransformer;
        } else {
          xmlTransformer = this.htmlTransformer;
        }
      } else {
        xmlTransformer = this.defaultTransformer;
      }

      ParameterUtil.mapParameters(request, "Layout:" + this.id, config.getAllTransformParams(), request.getParams(), layoutParams, this.id);

      layoutParams.put(IXMLTransformer.COMPONENT_REQUEST, request);
      xmlTransformer.transformStream(stream, inputXSLs, this.layoutXml, layoutParams, transformerHelper);
    } catch (XMLTransformerException e) {
      throw new ComponentException(e);
    }

  }

  public DoItRef getDoItRef() {
    return doItRef;
  }
  public void setDoItRef(DoItRef doItRef) {
    this.doItRef = doItRef;
  }

  public Map<String, List<Transform>> getTransforms() {
    return transforms;
  }
  public void setTransforms(Map<String, List<Transform>> transforms) {
    this.transforms = transforms;
  }

  public void setDefaultTransformer(IXMLTransformer defaultTransformer) {
    this.defaultTransformer = defaultTransformer;
  }

  public void setHtmlTransformer(IXMLTransformer htmlTransformer) {
    this.htmlTransformer = htmlTransformer;
  }

  public void setXmlTransformer(IXMLTransformer xmlTransformer) {
    this.xmlTransformer = xmlTransformer;
  }
}
