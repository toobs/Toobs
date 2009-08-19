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
package org.toobsframework.pres.xsl;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.toobsframework.pres.chart.ChartBuilder;
import org.toobsframework.pres.chart.ChartDefinition;
import org.toobsframework.pres.chart.manager.IChartManager;
import org.toobsframework.transformpipeline.domain.XMLTransformerException;
import org.toobsframework.util.IRequest;


@SuppressWarnings("unchecked")
public class ChartHelper extends ComponentHelper {

  private IChartManager chartManager = null;
  private ChartBuilder chartBuilder = null;

  public ChartHelper() {
    super();
  }

  public String chartUrl (
      org.apache.xalan.extensions.XSLProcessorContext processorContext, 
      org.apache.xalan.templates.ElemExtensionCall extensionElement) throws TransformerException {

    // Get tag attributes
    String componentUrl = extensionElement.getAttribute("componentUrl", processorContext.getContextNode(), processorContext.getTransformer());
    int width = Integer.parseInt(extensionElement.getAttribute("width", processorContext.getContextNode(), processorContext.getTransformer()));
    int height = Integer.parseInt(extensionElement.getAttribute("height", processorContext.getContextNode(), processorContext.getTransformer()));
    
    // Compute
    try {
      ComponentTransformerHelper transformerHelper = getTransformerHelper(processorContext);
      IRequest request = getComponentRequest(processorContext);
      if (request == null) {
        throw new XMLTransformerException("Invalid request");
      }

      Map inParams = new HashMap(request.getParams());
      String componentId = parseUrl(transformerHelper, "Component:", componentUrl, request, inParams);

      ChartDefinition chartDef = chartManager.getChartDefinition(componentId.replace(transformerHelper.getConfiguration().getChartExtension(), ""));
      request.getParams().putAll(inParams);
      return chartBuilder.buildAsImage(chartDef, request, width, height);

    } catch (Exception ex) {
      throw new TransformerException("Error executing toobs chart insertion: " + ex.getMessage(), ex);
    }
  }

}
