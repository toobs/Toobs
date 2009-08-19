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

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.beanutils.Converter;

public class DateToStringConverter implements Converter {

  public Object convert(Class arg0, Object value) {
    if (value == null) {
      return ((String) null);
    } else if(value instanceof java.util.Date) {
      Date date = (Date) value;
      return String.valueOf(date.getTime());
    } else if(value instanceof BigDecimal) {
      NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);
      BigDecimal bigD = (BigDecimal) value;
      return n.format(bigD.doubleValue());
    }  else {
      return (value.toString());
    }
  }

}