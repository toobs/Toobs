package org.toobsframework.pres.util;

import org.toobsframework.util.IRequest;

public class HeaderUtil implements INamespaceParameterHelper {

  private static final String NAMESPACE = "header:";
  public static final int LEN_NAMESPACE = NAMESPACE.length();

  public boolean supports(String tagName) {
    return tagName.startsWith(NAMESPACE);
  }

  public String[] getValue(IRequest request, String tagName) {
    String[] ret = new String[]{""};

    if (!tagName.startsWith(NAMESPACE)) {
      return ret;
    }

    String headerName = tagName.substring(LEN_NAMESPACE);
    String value = request.getHttpRequest().getHeader(headerName);
    if (value != null) {
      ret = new String[] {value};
    }
    return ret;
  }


}
