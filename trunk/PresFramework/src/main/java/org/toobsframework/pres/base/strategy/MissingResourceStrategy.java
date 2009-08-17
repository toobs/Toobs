package org.toobsframework.pres.base.strategy;

import org.toobsframework.util.IRequest;

public interface MissingResourceStrategy {

  public abstract String resolveMissingLayout(IRequest componentRequest);

}