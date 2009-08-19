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
package org.toobsframework.util;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.commons.betwixt.strategy.ConvertUtilsObjectStringConverter;
import org.apache.commons.betwixt.strategy.DefaultIdStoringStrategy;
import org.apache.commons.betwixt.strategy.ObjectStringConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobsframework.pres.betwixt.strategy.MixedContentEncodingStrategy;
import org.toobsframework.pres.betwixt.strategy.PropertySuppressionStrategy;
import org.xml.sax.SAXException;


public class BetwixtUtil {

  private static Log log = LogFactory.getLog(BetwixtUtil.class);

  public static String toXml(Object obj) throws IOException {
    return toXml(obj, false, false, false, null, null);
  }
  
  public static String toXml(Object obj, 
      boolean attributesForPrimitives,
      boolean wrapCollections,
      boolean mapIds,
      PropertySuppressionStrategy pss,
      ObjectStringConverter osc) throws IOException {
    if(obj == null){
      return "";
    }
    
    // TODO Integrate SAX.
    StringWriter writer = new StringWriter();
    BeanWriter beanWriter = new BeanWriter(writer);
    beanWriter.setLog(log);
    // Set propertySuppressionStrategy.
    if (pss == null) {
      pss = new PropertySuppressionStrategy();
    }
    if (osc == null) {
      osc = new ConvertUtilsObjectStringConverter();
    }
    beanWriter.getXMLIntrospector().getConfiguration().setPropertySuppressionStrategy(pss);
    // Set collection settings.
    beanWriter.getXMLIntrospector().getConfiguration().setWrapCollectionsInElement(wrapCollections);
    beanWriter.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(attributesForPrimitives);

    beanWriter.getBindingConfiguration().setObjectStringConverter(osc);
    // MapIDs false makes it so a bean instance is always written 
    beanWriter.getBindingConfiguration().setMapIDs(mapIds);
    beanWriter.getBindingConfiguration().setIdMappingStrategy(new DefaultIdStoringStrategy());
    
    beanWriter.setMixedContentEncodingStrategy(new MixedContentEncodingStrategy());

    try {
      beanWriter.write(obj);
    } catch (SAXException e) {
      log.error(e);
      throw new IOException();
    } catch (IntrospectionException e) {
      log.error(e);
      throw new IOException();
    } catch (Exception e) {
      log.error(e);
      throw new IOException();
    }

    return writer.toString();

  }
  
}
