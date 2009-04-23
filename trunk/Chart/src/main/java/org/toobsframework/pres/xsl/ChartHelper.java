package org.toobsframework.pres.xsl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.toobsframework.pres.chart.ChartBuilder;
import org.toobsframework.pres.chart.ChartDefinition;
import org.toobsframework.pres.chart.manager.IChartManager;
import org.toobsframework.transformpipeline.domain.XMLTransformerException;
import org.toobsframework.util.Configuration;
import org.toobsframework.util.IRequest;


@SuppressWarnings("unchecked")
public class ChartHelper extends ComponentHelper {

  /** To get the logger instance */
  private static Log log = LogFactory.getLog(ChartHelper.class);
  
  private static IChartManager chartManager;
  private static ChartBuilder chartBuilder;
  private static String chartExtension;

  static {
    chartManager = (IChartManager)beanFactory.getBean("IChartManager");
    chartBuilder = (ChartBuilder)beanFactory.getBean("chartBuilder");
    chartExtension = Configuration.getInstance().getChartExtension();
  }

  public static String chartUrl(String componentUrl, int width, int height) throws
    XMLTransformerException {
  
    try {
      IRequest request = reqManager.get();
      if (request == null) {
        throw new XMLTransformerException("Invalid request");
      }

      Map inParams = new HashMap(request.getParams());
      String componentId = parseUrl("Component:", componentUrl, request, inParams);

      ChartDefinition chartDef = chartManager.getChartDefinition(componentId.replace(chartExtension, ""));
      request.getParams().putAll(inParams);
      return chartBuilder.buildAsImage(chartDef, request, width, height);

    } catch (Exception ex) {
      throw new XMLTransformerException(ex);
    }
  }

}
