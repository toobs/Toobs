package org.toobsframework.pres.url.mapping;

import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;

public class PatternUrlMapping {
  private String controllerBean;
  private String[] pathParts;
  private String urlPattern;
  private boolean wildcardSuffixMatch;

  private static final String[] ZERO_STRINGS = new String[0];

  public PatternUrlMapping(String controllerBean, String path) {
    this.urlPattern = path;
    this.controllerBean = controllerBean;

    pathParts = tokenizePath(path);
    if (pathParts.length > 0 && pathParts[pathParts.length - 1].equals("*")) {
      wildcardSuffixMatch = true;
    }
  }

  public String getUrlPattern() {
    return urlPattern;
  }

  /**
   * Returns true iff the request path parts matches the path parts
   * for this mapping (i.e., the request should be dispatched to the
   * controller specified in this mapping).
   */
  public boolean matches(String[] requestPathParts) {
    boolean matches = true;
    if (pathParts.length == requestPathParts.length || (wildcardSuffixMatch && requestPathParts.length >= pathParts.length - 1)) {
      for (int i = 0; i < requestPathParts.length && matches; i++) {
        if (i < pathParts.length && !isPartMatch(pathParts[i], requestPathParts[i])) {
          matches = false;
        }
      }
    } else {
      matches = false;
    }

    return matches;
  }

  /**
   * Returns a DispatchInfo containing the controllerBean to dispatch to and a map of the dispatch
   * parameters.
   * <p/>
   * The client MUST ensure that this.matches(requestPathParts) is true before calling. The
   * normal client flow is to find a ControllerUrlMapping that matches and then call this method.
   */
  /*
  public DispatchInfo createDispatchInfo(String[] requestPathParts, String urlPattern) {
    Map<String, String> params = new HashMap<String, String>();

    String lastParamName = null;
    for (int i = 0; i < requestPathParts.length; i++) {
      String requestPathPart = requestPathParts[i];
      if (i < pathParts.length && !pathParts[i].equals("*")) {
        String paramName = pathParts[i];
        if (paramName.startsWith("?")) {
          paramName = paramName.substring(1);
          params.put(paramName, requestPathPart);
          lastParamName = paramName;
        }
      } else {
        if (lastParamName != null) {
          params.put(lastParamName, params.get(lastParamName) + "/" + requestPathPart);
        }
      }
    }

    return new DispatchInfo(controllerBean, params, urlPattern);
  }
  */

  public static String[] tokenizePath(String uriPath) {
    String[] tokens;
    if (uriPath != null) {
      StringTokenizer tok = new StringTokenizer(uriPath, "/");
      tokens = new String[tok.countTokens()];
      int i = 0;
      while (tok.hasMoreTokens()) {
        tokens[i++] = tok.nextToken();
      }
    } else {
      tokens = ZERO_STRINGS;
    }
    return tokens;
  }
  
  private boolean isPartMatch(String part, String requestPart) {
    return part.startsWith("?") || part.equals("*") || part.equals(requestPart);
  }
}