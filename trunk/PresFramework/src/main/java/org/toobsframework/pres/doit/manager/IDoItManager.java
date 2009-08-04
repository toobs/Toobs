package org.toobsframework.pres.doit.manager;

import java.util.List;

import org.toobsframework.pres.doit.config.DoIt;


public interface IDoItManager {

  public abstract DoIt getDoIt(String Id, long deployTime) throws Exception;

  public void addConfigFiles(List<String> configFiles);
  
}