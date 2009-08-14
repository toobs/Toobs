package org.toobsframework.pres.component.dataprovider.api;

import java.util.Map;

import org.toobsframework.util.IRequest;

public class DispatchContextFactory {
  public static DispatchContext createDispatchContext(String action, String guid, String permissionContext, String namespace, Map<String, Object> inputParameters, Map<String, Object> outputParameters) {
    DispatchContext context = new DispatchContext();
    context.setAction(action);
    context.setGuid(guid);
    context.setPermissionContext(permissionContext);
    context.setNamespace(namespace);
    context.setInputParameters(inputParameters);
    context.setOutputParameters(outputParameters);
    return context;
  }

  public static DispatchContextEx createDispatchContextEx(IRequest request, String action, String guid, String permissionContext, String namespace, Map<String, Object> inputParameters, Map<String, Object> outputParameters) {
    DispatchContextEx context = new DispatchContextEx();
    context.setRequest(request);
    context.setAction(action);
    context.setGuid(guid);
    context.setPermissionContext(permissionContext);
    context.setNamespace(namespace);
    context.setInputParameters(inputParameters);
    context.setOutputParameters(outputParameters);
    return context;
  }
}
