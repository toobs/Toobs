package org.toobsframework.pres.base.strategy;

import org.toobsframework.pres.util.PresConstants;
import org.toobsframework.util.IRequest;

public class DefaultResourceSecurityStrategy implements ResourceSecurityStrategy {

  public boolean hasAccess(IRequest componentRequest, String id) {
    return true;
  }

  public String resolveNoAccessLayout(IRequest componentRequest) {
    return PresConstants.DEFAULT_DENIED_LAYOUT;
  }

}
