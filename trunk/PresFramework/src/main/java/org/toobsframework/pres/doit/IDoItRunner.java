package org.toobsframework.pres.doit;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.toobsframework.pres.doit.config.DoIt;
import org.toobsframework.pres.component.dataprovider.api.IDataProvider;


public interface IDoItRunner {

  public abstract void runDoIt(HttpServletRequest request, HttpServletResponse response, DoIt doIt, Map paramMap, Map responseMap) throws Exception;

  public abstract IDataProvider getDataProvider();

}