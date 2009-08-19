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
package org.toobsframework.pres.util;

import java.util.Date;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.ConversionException;

/**
 * Converts a date string into java.util.Date
 * @author stewari
 */
public class DateConverter implements Converter {

	public DateConverter() {
	}
	
	/**
	 * @param type - should be java.util.Date
	 * @param value - should be specified in milliseconds as a String
	 */
	public Object convert(Class type, Object value) 
		throws ConversionException {
		Date convertedDate = null;
		if (!java.util.Date.class.isAssignableFrom(type)) {
			throw new ConversionException("Invalid input type. Should be java.util.Date");
		}
		if (!String.class.isAssignableFrom(value.getClass())) {
			throw new ConversionException("Value should be a string");
		}
		String timeStr = (String) value;
		convertedDate = new Date((new Long(timeStr)).longValue());
		return convertedDate;
	}
}
