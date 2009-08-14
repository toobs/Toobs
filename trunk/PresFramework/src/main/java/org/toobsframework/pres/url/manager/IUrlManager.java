package org.toobsframework.pres.url.manager;

import java.util.List;

import org.toobsframework.pres.url.UrlMapping;

public interface IUrlManager {

  public abstract UrlMapping getUrlMapping(String pattern, long deployTime) throws Exception;

  public void addConfigFiles(List<String> configFiles);
  
}