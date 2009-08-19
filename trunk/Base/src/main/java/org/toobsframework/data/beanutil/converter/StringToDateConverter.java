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
package org.toobsframework.data.beanutil.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StringToDateConverter {

  private static Log log = LogFactory.getLog(StringToDateConverter.class);
  
  public static Object convert(Object value) {
    if (value == null) return (Date)null;
    Date parsed = null;
    String strDate;
    if (value.getClass().isArray()) {
      strDate = ((String[])value)[0];
    } else {
      strDate = (String)value;
    }
    DateFormat df;
    if (strDate.length() > 10) {
      df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    } else {
      df = new SimpleDateFormat("MM/dd/yyyy");
    }
    try {
      parsed = df.parse(strDate);
    } catch (ParseException e) {
      log.error("ParseError for " + strDate);
    }
    return parsed;
  }

}