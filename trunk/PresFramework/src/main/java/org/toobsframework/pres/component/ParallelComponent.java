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
package org.toobsframework.pres.component;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobsframework.pres.layout.ComponentRef;
import org.toobsframework.pres.util.IComponentRequest;
import org.toobsframework.pres.util.ParameterUtil;
import org.toobsframework.pres.util.PresConstants;
import org.toobsframework.pres.xsl.ComponentTransformerHelper;
import org.toobsframework.transformpipeline.domain.IXMLTransformerHelper;

public class ParallelComponent implements Runnable {
  protected final Log log = LogFactory.getLog(getClass());

  private final IComponentRequest componentRequest;
  private final ComponentRef componentRef;
  private final IXMLTransformerHelper transformerHelper;
  private final AtomicBoolean complete = new AtomicBoolean(false);
  private final AtomicBoolean failed = new AtomicBoolean(false);

  private ByteArrayOutputStream output;
  private Throwable cause;

  public ParallelComponent(IComponentRequest componentRequest, ComponentRef componentRef, IXMLTransformerHelper transformerHelper) {
    this.componentRequest = componentRequest;
    this.componentRef = componentRef;
    this.transformerHelper = transformerHelper;
  }

  public void run() {
    Map<String,Object> outParams = new HashMap<String,Object>();
    Component component = componentRef.getComponent();

    try {
      output = new ByteArrayOutputStream();
      if (componentRef.getParameters() != null) {
        ParameterUtil.mapParameters(componentRequest, "ParallelComponent:" + component.getId(), componentRef.getParameters().getParameter(), componentRequest.getParams(), outParams, component.getId());
      }
      component.renderStream(output, componentRequest, componentRequest.getDispatchInfo().getContentType(), transformerHelper);
      complete.set(true);
    } catch (Exception e) {

      log.error("Exception running parallel component: " + e.getMessage(), e);
      try {
        componentRequest.getParams().put(PresConstants.TOOBS_EXCEPTION_ATTR_NAME, e);
        Component errorComponent = ((ComponentTransformerHelper)transformerHelper).getComponentManager().getComponent(transformerHelper.getConfiguration().getErrorComponentName());
        errorComponent.renderStream(output, componentRequest, componentRequest.getDispatchInfo().getContentType(), transformerHelper);
      } catch (Exception e1) {
        log.error("Exception getting error for running parallel component: " + e.getMessage(), e);
      }

      this.cause = e;
      this.failed.set(true);
    }
    synchronized(this) {
      notify();
    }
  }


  public String getId() {
    return componentRef.getComponent().getId();
  }

  public Component getComponent() {
    return componentRef.getComponent();
  }

  public ByteArrayOutputStream getOutput() throws InterruptedException {
    if (!this.isComplete() && !this.isFailed()) {
      synchronized(this) {
        this.wait();
      }
    }
    return output;
  }

  public Throwable getCause() {
    return cause;
  }

  public boolean isComplete() {
    return complete.get();
  }

  public boolean isFailed() {
    return failed.get();
  }

}
