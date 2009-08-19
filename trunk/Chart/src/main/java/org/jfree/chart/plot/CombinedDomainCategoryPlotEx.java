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
package org.jfree.chart.plot;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.CategoryAxis;

@SuppressWarnings("unchecked")
public class CombinedDomainCategoryPlotEx extends CombinedDomainCategoryPlot {

  public CombinedDomainCategoryPlotEx(CategoryAxis domainAxis) {
    super(domainAxis);
  }

  /**
   * Returns a collection of legend items for the plot.
   *
   * @return The legend items.
   */
  public LegendItemCollection getLegendItems() {
      LegendItemCollection result = getFixedLegendItems();
      if (result == null) {
          result = new LegendItemCollection();
          if (this.getSubplots() != null) {
              Map itemMap = new LinkedHashMap();
              Iterator iterator = this.getSubplots().iterator();
              while (iterator.hasNext()) {
                  CategoryPlot plot = (CategoryPlot) iterator.next();
                  LegendItemCollection more = plot.getLegendItems();
                  for (int i = 0; i < more.getItemCount(); i++) {
                    itemMap.put(more.get(i).getLabel(), more.get(i));
                  }
              }
              Iterator iter = itemMap.values().iterator();
              while (iter.hasNext()) {
                result.add((LegendItem)iter.next());
              }
          }
      }
      return result;
  }

}
