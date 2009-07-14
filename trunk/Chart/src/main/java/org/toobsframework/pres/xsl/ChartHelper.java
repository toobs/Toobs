package org.toobsframework.pres.xsl;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xalan.transformer.TransformerImpl;

import org.toobsframework.pres.chart.ChartBuilder;
import org.toobsframework.pres.chart.ChartDefinition;
import org.toobsframework.pres.chart.manager.IChartManager;
import org.toobsframework.transformpipeline.domain.IXMLTransformer;
import org.toobsframework.transformpipeline.domain.XMLTransformerException;
import org.toobsframework.util.Configuration;
import org.toobsframework.util.IRequest;


@SuppressWarnings("unchecked")
public class ChartHelper extends ComponentHelper {

  /** To get the logger instance */
  private static Log log = LogFactory.getLog(ChartHelper.class);
  
  private IChartManager chartManager = null;
  private ChartBuilder chartBuilder = null;
  private String chartExtension;

  static {
    //chartManager = (IChartManager)beanFactory.getBean("IChartManager");
    //chartBuilder = (ChartBuilder)beanFactory.getBean("chartBuilder");
  }
  
  public ChartHelper() {
    super();
    chartExtension = Configuration.getInstance().getChartExtension();
  }

  public String chartUrl (
      org.apache.xalan.extensions.XSLProcessorContext processorContext, 
      org.apache.xalan.templates.ElemExtensionCall extensionElement) throws TransformerException {

    // initialize
    TransformerImpl transformer = processorContext.getTransformer();
    Object th = transformer.getParameter(IXMLTransformer.TRANSFORMER_HELPER);
    if (th == null || !(th instanceof ComponentTransformerHelper)) {
      throw new TransformerException("Internal error: the property " + IXMLTransformer.TRANSFORMER_HELPER + " needs to be properly initialized prior to calling the transformation.");
    }
    ComponentTransformerHelper transformerHelper = (ComponentTransformerHelper) th;

    // Get tag attributes
    String componentUrl = extensionElement.getAttribute("componentUrl", processorContext.getContextNode(), processorContext.getTransformer());
    int width = Integer.parseInt(extensionElement.getAttribute("width", processorContext.getContextNode(), processorContext.getTransformer()));
    int height = Integer.parseInt(extensionElement.getAttribute("height", processorContext.getContextNode(), processorContext.getTransformer()));
    
    // Compute
    try {
      IRequest request = transformerHelper.getComponentRequestManager().get();
      if (request == null) {
        throw new XMLTransformerException("Invalid request");
      }

      Map inParams = new HashMap(request.getParams());
      String componentId = parseUrl("Component:", componentUrl, request, inParams);

      ChartDefinition chartDef = chartManager.getChartDefinition(componentId.replace(chartExtension, ""));
      request.getParams().putAll(inParams);
      return chartBuilder.buildAsImage(chartDef, request, width, height);

    } catch (Exception ex) {
      throw new TransformerException("Error executing toobs chart insertion: " + ex.getMessage(), ex);
    }
  }

}
