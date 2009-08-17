package org.toobsframework.pres.util;

import org.toobsframework.pres.component.ParallelComponent;
import org.toobsframework.util.IRequest;

public interface IComponentRequest extends IRequest {

  public abstract void addParallelComponent(ParallelComponent parallelComponent);
  
  public abstract ParallelComponent getParallelComponent(String id);

}
