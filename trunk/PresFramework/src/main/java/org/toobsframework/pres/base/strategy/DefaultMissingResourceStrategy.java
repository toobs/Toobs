package org.toobsframework.pres.base.strategy;

import org.toobsframework.pres.util.PresConstants;
import org.toobsframework.util.IRequest;

public class DefaultMissingResourceStrategy implements MissingResourceStrategy {

  public String resolveMissingLayout(IRequest componentRequest) {
    return PresConstants.DEFAULT_MISSING_LAYOUT;
  }

}
