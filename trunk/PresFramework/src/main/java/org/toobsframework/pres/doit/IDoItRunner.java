package org.toobsframework.pres.doit;

import java.util.Map;

import org.toobsframework.pres.doit.config.DoIt;
import org.toobsframework.pres.component.dataprovider.api.IDataProvider;
import org.toobsframework.util.IRequest;


public interface IDoItRunner {

  public abstract void runDoIt(IRequest request, DoIt doIt, Map<String,Object> paramMap, Map<String,Object> responseMap) throws Exception;

  public abstract IDataProvider getDataProvider();

}