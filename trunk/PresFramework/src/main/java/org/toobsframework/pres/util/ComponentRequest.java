package org.toobsframework.pres.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.toobsframework.pres.component.ParallelComponent;
import org.toobsframework.pres.url.UrlDispatchInfo;
import org.toobsframework.util.BaseRequest;

public class ComponentRequest extends BaseRequest implements IComponentRequest {

  private Map<String,ParallelComponent> parallelComponents;

  public ComponentRequest(UrlDispatchInfo dispatchInfo, HttpServletRequest httpRequest, HttpServletResponse httpResponse, Map<String,Object> params, boolean expectResponse) {
    super(dispatchInfo, httpRequest, httpResponse, params, expectResponse);
  }

  public void addParallelComponent(ParallelComponent parallelComponent) {
    if (parallelComponents == null) {
      parallelComponents = new HashMap<String,ParallelComponent>(); 
    }
    this.parallelComponents.put(parallelComponent.getId(),parallelComponent);
  }

  public ParallelComponent getParallelComponent(String id) {
    return parallelComponents.get(id);
  }

}
