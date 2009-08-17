package org.toobsframework.pres.doit;

import org.toobsframework.pres.doit.config.DoIt;
import org.toobsframework.pres.component.dataprovider.api.IDataProvider;
import org.toobsframework.util.IRequest;


public interface IDoItRunner {

  public abstract void runDoIt(IRequest request, DoIt doIt) throws Exception;

  public abstract IDataProvider getDataProvider();

}