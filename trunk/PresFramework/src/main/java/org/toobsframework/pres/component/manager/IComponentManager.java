package org.toobsframework.pres.component.manager;

import java.util.List;
import java.util.Map;

import org.toobsframework.exception.ParameterException;
import org.toobsframework.pres.component.ComponentException;
import org.toobsframework.pres.component.ComponentInitializationException;
import org.toobsframework.pres.component.ComponentNotFoundException;
import org.toobsframework.pres.component.ComponentNotInitializedException;
import org.toobsframework.transformpipeline.domain.IXMLTransformerHelper;
import org.toobsframework.util.IRequest;

public interface IComponentManager {

  public abstract org.toobsframework.pres.component.Component getComponent(String Id) throws ComponentNotFoundException,
      ComponentInitializationException;

  public String renderComponent(
      IRequest request,
      org.toobsframework.pres.component.Component component,
      String contentType, 
      Map<String, Object> params, 
      Map<String, Object> paramsOut, 
      IXMLTransformerHelper transformerHelper, 
      boolean appendUrlScanner)
      throws ComponentNotInitializedException, ComponentException, ParameterException;

  public void addConfigFiles(List<String> configFiles);
}