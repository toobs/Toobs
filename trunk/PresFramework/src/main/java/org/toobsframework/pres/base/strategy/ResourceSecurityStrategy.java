package org.toobsframework.pres.base.strategy;

import org.toobsframework.util.IRequest;

public interface ResourceSecurityStrategy {

  String resolveNoAccessLayout(IRequest componentRequest);

  boolean hasAccess(IRequest componentRequest, String id);

}