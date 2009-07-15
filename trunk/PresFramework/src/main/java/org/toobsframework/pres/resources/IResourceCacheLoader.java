package org.toobsframework.pres.resources;

import java.io.IOException;

import org.springframework.core.io.Resource;

public interface IResourceCacheLoader {
  public void load(Resource resource) throws IOException;
}
