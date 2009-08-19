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
package org.toobsframework.pres.base;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobsframework.transformpipeline.domain.IXMLTransformer;

public class Transformable {
  protected final Log log = LogFactory.getLog(getClass());

  private IXMLTransformer defaultTransformer;
  private IXMLTransformer htmlTransformer;
  private IXMLTransformer xmlTransformer;

  protected IXMLTransformer getTransformer(List<String> inputXSLs, String contentType) {
    // Figure out which Transformer to run and prepare as
    // necessary for that Transformer.
    if (inputXSLs.size() > 1) {
      if (!"xhtml".equals(contentType)) {
        return this.xmlTransformer;
      } else {
        return this.htmlTransformer;
      }
    } else {
      return this.defaultTransformer;
    }
  }

  public IXMLTransformer getDefaultTransformer() {
    return defaultTransformer;
  }

  public void setDefaultTransformer(IXMLTransformer defaultTransformer) {
    this.defaultTransformer = defaultTransformer;
  }

  public IXMLTransformer getHtmlTransformer() {
    return htmlTransformer;
  }

  public void setHtmlTransformer(IXMLTransformer htmlTransformer) {
    this.htmlTransformer = htmlTransformer;
  }

  public IXMLTransformer getXmlTransformer() {
    return xmlTransformer;
  }

  public void setXmlTransformer(IXMLTransformer xmlTransformer) {
    this.xmlTransformer = xmlTransformer;
  }

}
