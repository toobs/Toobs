package org.toobsframework.pres.layout.manager;

import java.util.List;

import org.toobsframework.exception.PermissionException;
import org.toobsframework.pres.layout.ComponentLayoutInitializationException;
import org.toobsframework.pres.layout.ComponentLayoutNotFoundException;
import org.toobsframework.pres.layout.RuntimeLayout;


public interface IComponentLayoutManager {

  public abstract RuntimeLayout getLayout(String Id, long deployTime)
      throws ComponentLayoutNotFoundException, ComponentLayoutInitializationException;

  public abstract RuntimeLayout getLayout(PermissionException permissionException)
    throws ComponentLayoutNotFoundException, ComponentLayoutInitializationException;

  public void addConfigFiles(List<String> configFiles);

}