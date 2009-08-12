package org.toobsframework.pres.resources;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * Organize and keep all the resources associated to a pattern.  
 * <p>
 * The resources are loaded
 * with spring PathMatchingResourcePatternResolver, which in theory could produce
 * performance problems if continuously called.  For this reason, the pattern
 * provides for full control on both when to call the checking of modified files and the 
 * checking of modified files date.
 * 
 * The two expected use cases are as follows:
 * <p>
 * <strong>with checking of dates and reloading</strong>
 * <pre><code>
 * Resource[] resources = resourceCache.checkIfModified();
 * if (modified != null) {
 *   resourceCache.load(resources, true, loader);
 * }
 * </code></pre>
 * <strong>without checking of dates and reloading (load once)</strong>
 * <pre><code>
 * resourceCache.load(null, false, loader);
 * </code></pre>
 * 
 * @author jaimeg@yahoo-inc.com
 *
 */
public class ResourceCacheDescriptor {

  //private static PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
  private static Log log = LogFactory.getLog(ResourceCacheDescriptor.class);

  String pattern;
  ApplicationContext applicationContext;
  
  /** How many elements are counted in this descriptor */
  int count = 0;
  
  /** The modification date for each of the files */
  private long lastModified[] = new long[0];
  
  private Resource[] resources = new Resource[0];

  public ResourceCacheDescriptor(ApplicationContext applicationContext, String pattern) {
    this.applicationContext = applicationContext;
    this.pattern = pattern;
  }
  
  /**
   * This function checks if the resources described by the spec
   * have been modified by relocating them.  If they have changed (either
   * there are more or less of them) or any of them has changed its date
   * the resource array is returned, otherwise a null is returned. The array
   * is returned so that it does not need to be reloaded in other calls, and
   * passed down to, load.
   * 
   * <pre><code>
   * Resource[] resources = resourceCache.checkIfModified();
   * if (modified != null) {
   *   resourceCache.load(resources, true, loader);
   * }
   * </code></pre>
   * 
   * WARNING: This routine re-scans the resources for matches
   * <p>
   * @return the modified resources, or null if no changes
   * @throws IOException 
   */
  public Resource[] checkIfModified() throws IOException {
    Resource[] resources = applicationContext.getResources(pattern);
    if (resources == null || resources.length == 0) {
      log.warn("Configuration file spec " + pattern + "did not resolve to any resource");
      if (resources == null) {
        resources = new Resource[0];
      }
    }
    
    if (resources.length != count) {
      return resources;
    }
    
    for (int i = 0; i < count; i++) {
      URL configFileURL = resources[i].getURL();
      File configFile = new File(configFileURL.getFile());
      if (configFile.lastModified() != lastModified[i]) {
        return resources;
      }
    }
    return null;
  }

  /**
   * Unconditionally loads the resources described in the pattern,
   * updating the modification dates and its number.
   * 
   * WARNING: This routine re-scans the resources for matches
   * 
   */
  public void load(Resource[] inResources, boolean checkModificationDates, IResourceCacheLoader loader) throws IOException {
    // performance optimization that lets me pass the resources from the outside (perhaps from a changed check)§
    if (inResources == null) {
      resources = applicationContext.getResources(pattern);
    } else {
      resources = inResources;
    }
    if (resources == null || resources.length == 0) {
      log.warn("Configuration file spec " + pattern + "did not resolve to any resource");
    }
    count = resources.length;
    lastModified = new long[count];
    
    for (int i = 0; i < count; i++) {
      URL configFileURL = resources[i].getURL();
      if (configFileURL == null) {
        log.warn("Skipping missing configuration file [" + resources[i].getFilename() + "]");
        continue;
      }
      File configFile = new File(configFileURL.getFile());
      if (checkModificationDates) {
        if (log.isDebugEnabled()) {
          log.debug("Config: " + resources[i].getFilename() + " File Mod: " + new Date(configFile.lastModified()) + " Last Mod: " + new Date(lastModified[i]));
        }
        if (configFile.lastModified() == lastModified[i]) {
          continue;
        }
      }
      log.info("Loading configuration file [" + resources[i].getFilename() + "]");
      loader.load(resources[i]);
      lastModified[i] = configFile.lastModified();
    }
  }

  /**
   * @return the pattern
   */
  public String getPattern() {
    return pattern;
  }

  /**
   * @param pattern the pattern to set
   */
  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  /**
   * @return the count
   */
  public int getCount() {
    return count;
  }

  /**
   * @param count the count to set
   */
  public void setCount(int count) {
    this.count = count;
  }

  /**
   * @return the lastModified
   */
  public long[] getLastModified() {
    return lastModified;
  }

  /**
   * @param lastModified the lastModified to set
   */
  public void setLastModified(long[] lastModified) {
    this.lastModified = lastModified;
  }

  /**
   * @return the resources
   */
  public Resource[] getResources() {
    return resources;
  }

  /**
   * @param resources the resources to set
   */
  public void setResources(Resource[] resources) {
    this.resources = resources;
  }
}
