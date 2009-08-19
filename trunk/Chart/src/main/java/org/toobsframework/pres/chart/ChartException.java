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

import org.toobsframework.exception.BaseException;

public class ChartException extends BaseException {

  private static final long serialVersionUID = 1L;

  public ChartException() {
    super();
  }

  public ChartException(String message, Throwable cause) {
    super(message, cause);
  }

  public ChartException(String message) {
    super(message);
  }

  public ChartException(Throwable cause) {
    super(cause);
  }

}
