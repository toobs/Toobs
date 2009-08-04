package org.toobsframework.pres.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HeaderUtil implements INamespaceParameterHelper {

  private static final String NAMESPACE = "header:";
  public static final int LEN_NAMESPACE = NAMESPACE.length();

  public boolean supports(String tagName) {
    return tagName.startsWith(NAMESPACE);
  }

  public String[] getValue(String tagName, HttpServletRequest request, HttpServletResponse response) {
    String[] ret = new String[]{""};

    if (!tagName.startsWith(NAMESPACE)) {
      return ret;
    }

    String headerName = tagName.substring(LEN_NAMESPACE);
    String value = request.getHeader(headerName);
    if (value != null) {
      ret = new String[] {value};
    }
    return ret;
  }


}
