package org.toobsframework.social.session;

import java.util.Map;

public class SessionBase {
  protected String getParameter(Map<String, Object>map, String key) {
    Object o = map.get(key);
    
    if (o == null) {
      return null;
    }
    
    if (o.getClass().isArray()) {
      Object[] oa = (Object[]) o;
      if (oa.length == 0) {
        return null;
      }
      return oa[0].toString();
    } else {
      return o.toString();
    }
  }

}
