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
      // TODO would like to just generate the error output here
      //transformerHelper.getConfiguration().getErrorComponentName();
      log.error("Exception running parallel component: " + e.getMessage(), e);
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
