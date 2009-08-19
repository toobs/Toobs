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

import javax.xml.transform.URIResolver;

import org.apache.xalan.trace.TraceListener;
import org.toobsframework.transformpipeline.domain.IXMLTransformer;
import org.toobsframework.transformpipeline.domain.XMLTransformerFactory;

public abstract class XslManagerBase extends ManagerBase {

  protected IXMLTransformer defaultTransformer;
  protected IXMLTransformer htmlTransformer;
  protected IXMLTransformer xmlTransformer;

  private URIResolver xslResolver;
  private TraceListener paramListener;

  public void afterPropertiesSet() throws Exception {
    super.afterPropertiesSet();

    if (this.xslResolver == null) {
      throw new Exception("xslResolver property must be set");
    }

    xmlTransformer = XMLTransformerFactory.getInstance().getChainTransformer(XMLTransformerFactory.OUTPUT_FORMAT_XML, xslResolver, paramListener, configuration);
    htmlTransformer = XMLTransformerFactory.getInstance().getChainTransformer(XMLTransformerFactory.OUTPUT_FORMAT_HTML, xslResolver, paramListener, configuration);
    defaultTransformer = XMLTransformerFactory.getInstance().getDefaultTransformer(xslResolver, configuration);

  }

  public void setParamListener(TraceListener paramListener) {
    this.paramListener = paramListener;
  }

  public void setXslResolver(URIResolver xslResolver) {
    this.xslResolver = xslResolver;
  }

}
