package org.toobsframework.pres.resources;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.Unmarshaller;
import org.springframework.core.io.Resource;
import org.toobsframework.pres.component.manager.ComponentManager;


/**
 * Boilerplate to load a spring reource into a castor unmarshaller
 * @author jaimegarza
 *
 */
public class ResourceUnmarshaller {

  private static Log log = LogFactory.getLog(ResourceUnmarshaller.class);

  /**
   * Unmarshall a resource of a given class into its corresponding object
   * @param resource - the resource that describes the file location
   * @param clazz - the class of the unmarshalled result
   * @return the unmarshalled object
   * @throws IOException
   */
  public Object unmarshall(Resource resource, Class clazz) {
    Object config = null;
    try {
      URL configFileURL = resource.getURL();
      InputStreamReader reader = new InputStreamReader(configFileURL.openStream());
      try {
        Unmarshaller unmarshaller = new Unmarshaller(clazz);
        unmarshaller.setValidation(false);
        config = unmarshaller.unmarshal(reader);
      } finally {
        reader.close();
      }
    } catch (Exception e) {
      log.warn("Configuration load failure on file " + resource.getFilename() +  ": " + e.getMessage(), e);
    }
    return config;
  }
}
