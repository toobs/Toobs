package org.toobsframework.pres.url.manager;

import java.util.List;

import org.toobsframework.pres.url.config.Url;


public interface IUrlManager {

  public abstract Url getUrl(String Id, long deployTime) throws Exception;

  public void addConfigFiles(List<String> configFiles);
  
}