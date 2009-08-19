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
package org.toobsframework.pres.layout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.toobsframework.pres.base.Transformable;
import org.toobsframework.pres.component.ComponentException;
import org.toobsframework.pres.component.Transform;
import org.toobsframework.pres.component.config.Parameter;
import org.toobsframework.pres.layout.config.DoItRef;
import org.toobsframework.pres.layout.config.Section;
import org.toobsframework.exception.ParameterException;
import org.toobsframework.pres.util.IComponentRequest;
import org.toobsframework.pres.util.ParameterUtil;
import org.toobsframework.pres.xsl.ComponentTransformerHelper;
import org.toobsframework.transformpipeline.domain.IXMLTransformer;
import org.toobsframework.transformpipeline.domain.IXMLTransformerHelper;
import org.toobsframework.transformpipeline.domain.XMLTransformerException;
import org.toobsframework.util.BetwixtUtil;


public class RuntimeLayout extends Transformable {

  private static final String XML_HEADER = "<layout>";
  private static final String XML_FOOTER = "</layout>";
  private static final String XML_CP_HEADER = "<transform-params>";
  private static final String XML_CP_FOOTER = "</transform-params>";

  private String id;
  private Map<String, List<Transform>> transforms = new HashMap<String, List<Transform>>();
  private RuntimeLayoutConfig config;
  private String layoutXml;
  private DoItRef doItRef;

  public RuntimeLayoutConfig getConfig() {
    return config;
  }

  public void setConfig(RuntimeLayoutConfig config) {
    this.config = config;
    StringBuffer sb = new StringBuffer();
    sb.append(XML_HEADER);
    Parameter[] contentParams = config.getAllTransformParams();
    try {
      if (contentParams != null && contentParams.length > 0) {
        sb.append(XML_CP_HEADER);
        for (int c = 0; c < contentParams.length; c++) {
          sb.append(BetwixtUtil.toXml(contentParams[c], true, false, false, null, null));
        }
        sb.append(XML_CP_FOOTER);
      }
      Collection<? extends Section> sections = config.getAllSections();
      if (sections != null) {
        for (Section section : sections) {
          sb.append(BetwixtUtil.toXml(section, true, false, false, null, null));
        }
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

  public String render(IComponentRequest request, IXMLTransformerHelper transformerHelper) throws ComponentException, ParameterException {
    return this.render(request, "xhtml", transformerHelper);  
  }

  public String render(IComponentRequest request, String contentType, IXMLTransformerHelper transformerHelper) throws ComponentException, ParameterException {
    ByteArrayOutputStream renderedOutput = new ByteArrayOutputStream();

    this.renderStream(renderedOutput, request, contentType, transformerHelper);

    return new String(renderedOutput.toByteArray());
  }

  public void renderStream(OutputStream stream, IComponentRequest request, String contentType, IXMLTransformerHelper transformerHelper) throws ComponentException, ParameterException {
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

      ParameterUtil.mapParameters(request, "Layout:" + this.id, config.getAllTransformParams(), request.getParams(), layoutParams, this.id);

      layoutParams.put(IXMLTransformer.COMPONENT_REQUEST, request);
      if (transformerHelper.getConfiguration().enableParallel() && config.getComponentRefs() != null) {
        for (ComponentRef componentRef : config.getComponentRefs().values()) {
          ((ComponentTransformerHelper)transformerHelper).getComponentRequestManager().execParallelComponent(request, componentRef, transformerHelper);
        }
      }
      getTransformer(inputXSLs, contentType).transformStream(stream, inputXSLs, this.layoutXml, layoutParams, transformerHelper);

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

}
