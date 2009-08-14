package org.toobsframework.pres.util;

import org.toobsframework.util.IRequest;

public interface INamespaceParameterHelper {

  public boolean supports(String tagName);
  public String[] getValue(IRequest request, String tagName);

}
