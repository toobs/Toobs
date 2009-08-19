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
package org.toobsframework.pres.chart;

import org.toobsframework.pres.chart.config.Legend;
import org.toobsframework.pres.chart.config.Plot;
import org.toobsframework.pres.chart.config.Subtitle;
import org.toobsframework.pres.chart.config.Title;
import org.toobsframework.pres.component.config.Parameters;

public class ChartDefinition {
  
  private String id;
  private boolean showLegend;
  private String backgroundColor;
  private int chartHeight;
  private int chartWidth;
  private boolean doImageWithMap;
  private String urlFragmentBean;
  
  private Parameters parameterMapping;
  private Title title;
  private Subtitle subtitle;
  private Plot plot;
  private Legend legend;
  
  
  public String getBackgroundColor() {
    return backgroundColor;
  }
  public void setBackgroundColor(String backgroundColor) {
    this.backgroundColor = backgroundColor;
  }
  public int getChartHeight() {
    return chartHeight;
  }
  public void setChartHeight(int chartHeight) {
    this.chartHeight = chartHeight;
  }
  public int getChartWidth() {
    return chartWidth;
  }
  public void setChartWidth(int chartWidth) {
    this.chartWidth = chartWidth;
  }
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public Parameters getParameters() {
    return parameterMapping;
  }
  public void setParameters(Parameters parameterMapping) {
    this.parameterMapping = parameterMapping;
  }
  public Plot getPlot() {
    return plot;
  }
  public void setPlot(Plot plot) {
    this.plot = plot;
  }
  public boolean isShowLegend() {
    return showLegend;
  }
  public void setShowLegend(boolean showLegend) {
    this.showLegend = showLegend;
  }
  public Subtitle getSubtitle() {
    return subtitle;
  }
  public void setSubtitle(Subtitle subtitle) {
    this.subtitle = subtitle;
  }
  public Title getTitle() {
    return title;
  }
  public void setTitle(Title title) {
    this.title = title;
  }
  public Legend getLegend() {
    return legend;
  }
  public void setLegend(Legend legend) {
    this.legend = legend;
  }
  public boolean doImageWithMap() {
    return doImageWithMap;
  }
  public void setDoImageWithMap(boolean doImageWithMap) {
    this.doImageWithMap = doImageWithMap;
  }
  public String getUrlFragmentBean() {
    return urlFragmentBean;
  }
  public void setUrlFragmentBean(String urlFragmentBean) {
    this.urlFragmentBean = urlFragmentBean;
  }


}
