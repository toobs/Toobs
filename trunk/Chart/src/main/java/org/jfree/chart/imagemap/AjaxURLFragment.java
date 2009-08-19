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
package org.jfree.chart.imagemap;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Generates URLs using the HTML href attribute for image map area tags.
 */
public class AjaxURLFragment 
    implements URLTagFragmentGenerator {

    /**
     * Generates a URL string to go in an HTML image map.
     *
     * @param urlText  the URL.
     * 
     * @return The formatted text
     */
    public String generateURLFragment(String urlText) {
      String decoded;
      String category = "";
      String baseChart = "";
      String chartMod = "";
      String chartParams = "";
      try {
        decoded = URLDecoder.decode(urlText, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        decoded = urlText;
      }
      int catIdx = decoded.indexOf("category=");
      if (catIdx != -1) {
        category = decoded.substring(catIdx+9);
      }
      String[] split = urlText.split("\\?");
      int spIdx = split[0].indexOf("SinglePlot");
      if (spIdx != -1) {
        chartMod = "SinglePlot";
      } else {
        spIdx = split[0].indexOf(".xchart");
      }
      baseChart = split[0].substring(0,spIdx);
      if (split.length > 1) {
        chartParams = split[1];
      }
      return " href=\"javascript:void(0)\" baseChart=\"" + baseChart + "\" chartMod=\"" + chartMod + "\" params=\"" + chartParams + "\" class=\"ajaxMapArea\" category=\"" + category + "\"";
    }

}
