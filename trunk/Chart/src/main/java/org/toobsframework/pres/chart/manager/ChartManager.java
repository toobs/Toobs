package org.toobsframework.pres.chart.manager;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobsframework.pres.base.ManagerBase;
import org.toobsframework.pres.chart.ChartDefinition;
import org.toobsframework.pres.chart.ChartInitializationException;
import org.toobsframework.pres.chart.ChartNotFoundException;
import org.toobsframework.pres.chart.config.Chart;
import org.toobsframework.pres.chart.config.ChartConfig;

@SuppressWarnings("unchecked")
public final class ChartManager extends ManagerBase implements IChartManager {

  private static Log log = LogFactory.getLog(ChartManager.class);
  
  private static Map registry;

  private List configFiles = null;

  private ChartManager() throws ChartInitializationException {
    log.info("Constructing new ChartManager");
  }
  
  public ChartDefinition getChartDefinition(String Id)
      throws ChartNotFoundException, ChartInitializationException {
    if (isDoReload() || !isInitDone()) {
      Date initStart = new Date();
      this.loadConfig(ChartConfig.class);
      Date initEnd = new Date();
      log.info("Init Time: " + (initEnd.getTime() - initStart.getTime()));
    }
    synchronized (registry) {
      if (!registry.containsKey(Id)) {
        throw new ChartNotFoundException(Id);
      }
      return (ChartDefinition) registry.get(Id);
    }
  }
  
  public void afterPropertiesSet() throws ChartInitializationException {
    registry = new HashMap();
    this.loadConfig(ChartConfig.class);
  }

  @Override
  protected void registerConfiguration(Object object, String fileName) {
    ChartConfig chartConfig = (ChartConfig) object;
    registerCharts(chartConfig.getChart());
  }

  private void registerCharts(Chart[] charts) {
    if ((charts != null) && (charts.length > 0)) {
      Chart chart = null;
      ChartDefinition chartDefinition = null;

      for (int i = 0; i < charts.length; i ++) {
        chart = charts[i];
        chartDefinition = new ChartDefinition();
        
        chartDefinition.setId(chart.getId());
        chartDefinition.setChartHeight(chart.getHeight());
        chartDefinition.setChartWidth(chart.getWidth());
        chartDefinition.setBackgroundColor(chart.getBackgroundColor());
        chartDefinition.setShowLegend(chart.getShowLegend());
        chartDefinition.setDoImageWithMap(chart.getDoImageWithMap());
        chartDefinition.setUrlFragmentBean(chart.getUrlFragmentBean());
        
        if (chart.getParameters() != null) {
          chartDefinition.setParameters(chart.getParameters());
        }
        if (chart.getTitle() != null) {
          chartDefinition.setTitle(chart.getTitle());
        }
        if (chart.getSubtitle() != null) {
          chartDefinition.setSubtitle(chart.getSubtitle());
        }
        if (chart.getLegend() != null) {
          chartDefinition.setLegend(chart.getLegend());
        }
        chartDefinition.setPlot(chart.getPlot());

        
        if (registry.containsKey(chart.getId()) && !isInitDone()) {
          log.warn("Overriding chartDefinition with Id: " + chart.getId());
        }
        registry.put(chart.getId(), chartDefinition);
      }
    }
  }
  
  public List getConfigFiles() {
    return configFiles;
  }

  public void setConfigFiles(List configFiles) {
    this.configFiles = configFiles;
  }
  
  public void addConfigFiles(List configFiles) {
    this.configFiles.addAll(configFiles);
  }

}