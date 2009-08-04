package org.toobsframework.pres.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface INamespaceParameterHelper {

  public boolean supports(String tagName);
  public String[] getValue(String tagName, HttpServletRequest request, HttpServletResponse response);

}
