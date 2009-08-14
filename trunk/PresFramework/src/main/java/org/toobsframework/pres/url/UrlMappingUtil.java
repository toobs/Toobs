package org.toobsframework.pres.url;

import java.util.StringTokenizer;

public class UrlMappingUtil {

  private static final String[] NO_STRINGS = new String[0];

  /**
   * Break a pattern into its components, separated by '/'
   * @param pathPattern is the pattern
   * @return a string of parts
   */
  public static String[] tokenizePath(String pathPattern) {
    String[] tokens;
    if (pathPattern != null) {
      StringTokenizer tok = new StringTokenizer(pathPattern, "/");
      tokens = new String[tok.countTokens()];
      int i = 0;
      while (tok.hasMoreTokens()) {
        tokens[i++] = tok.nextToken();
      }
    } else {
      tokens = NO_STRINGS;
    }
    return tokens;
  }


}
