package org.toobsframework.pres.layout.controller.strategy;

import org.toobsframework.pres.layout.config.DoItRef;
import org.toobsframework.util.IRequest;

public interface LayoutEventStrategy {

  void processPostEvent(IRequest componentRequest, DoItRef doItRef);

}
