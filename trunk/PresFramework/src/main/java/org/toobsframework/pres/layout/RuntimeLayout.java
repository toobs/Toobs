package org.toobsframework.pres.layout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobsframework.pres.component.ComponentException;
import org.toobsframework.pres.component.Transform;
import org.toobsframework.pres.component.config.Parameter;
import org.toobsframework.pres.layout.config.DoItRef;
import org.toobsframework.pres.layout.config.Section;
import org.toobsframework.exception.ParameterException;
import org.toobsframework.pres.util.ParameterUtil;
import org.toobsframework.transformpipeline.domain.IXMLTransformer;
import org.toobsframework.transformpipeline.domain.IXMLTransformerHelper;
import org.toobsframework.transformpipeline.domain.XMLTransformerException;
import org.toobsframework.util.BetwixtUtil;
import org.toobsframework.util.Configuration;
import org.toobsframework.util.IRequest;


@SuppressWarnings("unchecked")
public class RuntimeLayout {
  private static final Log log = LogFactory.getLog(RuntimeLayout.class);

  private static final String XML_HEADER = "<RuntimeLayout>";
  private static final String XML_FOOTER = "</RuntimeLayout>";
  private static final String XML_CP_HEADER = "<TransformParams>";
  private static final String XML_CP_FOOTER = "</TransformParams>";
  private String id;
  private Map<String, List> transforms = new HashMap<String, List>();
  private RuntimeLayoutConfig config;
  private String layoutXml;
  private DoItRef doItRef;

  private IXMLTransformer defaultTransformer;
  private IXMLTransformer htmlTransformer;
  private IXMLTransformer xmlTransformer;

  public RuntimeLayoutConfig getConfig() {
    return config;
  }
  public void setConfig(RuntimeLayoutConfig config) throws IOException {
    this.config = config;
    StringBuffer sb = new StringBuffer();
    sb.append(XML_HEADER);
    Parameter[] contentParams = config.getAllTransformParams();
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
    Map layoutParams = new HashMap();

    List outputXML = new ArrayList();
    try {
      List inputXSLs = new ArrayList();
      List inputXMLs = new ArrayList();
      List contentTransforms = this.getTransforms().get(contentType);
      if (contentTransforms != null && contentTransforms.size() > 0) {
        Iterator it = contentTransforms.iterator();
        while (it.hasNext()) {
          Transform transform = (Transform) it.next();
          inputXSLs.add(transform.getTransformName());
          //Fix the params using the param mapping for 
          //this configuration.
          if(transform.getTransformParams() != null){
            ParameterUtil.mapParameters("Transform:" + transform.getTransformName(), transform.getTransformParams().getParameter(), request.getParams(), layoutParams, this.id, request.getHttpRequest(), request.getHttpResponse());
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

      ParameterUtil.mapParameters("Layout:" + this.id, config.getAllTransformParams(), request.getParams(), layoutParams, this.id, request.getHttpRequest(), request.getHttpResponse());

      inputXMLs.add(this.layoutXml);
      layoutParams.put("context", Configuration.getInstance().getMainContext() + "/");
      if (request.getParams().get("appContext") != null) {
        layoutParams.put("appContext", request.getParams().get("appContext"));
      }
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

  public DoItRef getDoItRef() {
    return doItRef;
  }
  public void setDoItRef(DoItRef doItRef) {
    this.doItRef = doItRef;
  }
  public Map<String, List> getTransforms() {
    return transforms;
  }
  public void setTransforms(Map<String, List> transforms) {
    this.transforms = transforms;
  }

  public IXMLTransformer getDefaultTransformer() {
    return defaultTransformer;
  }

  public void setDefaultTransformer(IXMLTransformer defaultTransformer) {
    this.defaultTransformer = defaultTransformer;
  }

  public IXMLTransformer getHtmlTransformer() {
    return htmlTransformer;
  }

  public void setHtmlTransformer(IXMLTransformer htmlTransformer) {
    this.htmlTransformer = htmlTransformer;
  }

  public IXMLTransformer getXmlTransformer() {
    return xmlTransformer;
  }

  public void setXmlTransformer(IXMLTransformer xmlTransformer) {
    this.xmlTransformer = xmlTransformer;
  }
}
