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
package org.toobsframework.transformpipeline.domain;

import org.toobsframework.exception.BaseException;

public class XMLTransformerException extends BaseException {
  /**
   * 
   */
  private static final long serialVersionUID = -5261738594889638341L;

  /**
   * Default constructor
   */
  public XMLTransformerException() {
    super();
  }

  /**
   * Constructor with a message parameter
   *
   * @param message String
   */
  public XMLTransformerException(String message) {
    super(message);
  }


  /**
   * Constructor with message, and cause
   *
   * @param message String
   * @param cause Throwable
   */
  public XMLTransformerException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor with cause
   *
   * @param cause Throwable
   */
  public XMLTransformerException(Throwable cause) {
    super(cause);
  }

}
