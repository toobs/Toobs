package org.toobsframework.pres.doit.manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.toobsframework.pres.base.ManagerBase;
import org.toobsframework.pres.doit.DoItInitializationException;
import org.toobsframework.pres.doit.config.DoIt;
import org.toobsframework.pres.doit.config.DoItConfig;
import org.toobsframework.util.Configuration;


/**
 * @author sean
 */
public final class DoItManager extends ManagerBase implements IDoItManager {

  private static Log log = LogFactory.getLog(DoItManager.class);

  private static long localDeployTime = 0L;

  private Map<String, DoIt> registry;

  private DoItManager() throws DoItInitializationException {
    log.info("Constructing new DoItManager");
    registry = new HashMap<String, DoIt>();
  }

  public DoIt getDoIt(String Id, long deployTime) throws DoItInitializationException {
    if (isDoReload() || deployTime > localDeployTime) {
      //Date initStart = new Date();
      this.init();
      //Date initEnd = new Date();
      //log.info("Init Time: " + (initEnd.getTime() - initStart.getTime()));
    }
    if (!registry.containsKey(Id)) {
      throw new DoItInitializationException("DoIt " + Id + " not found");
    }
    localDeployTime = deployTime;
    return registry.get(Id);
  }

  // Read from config file
  private void init() throws DoItInitializationException {
    loadConfig(DoItConfig.class);
  }

  @Override
  protected void registerConfiguration(Object object, String fileName) {
    DoItConfig doItConfig = (DoItConfig) object;
    DoIt[] doIts = doItConfig.getDoIt();
    for (int i = 0; i < doIts.length; i++) {
      DoIt thisDoIt = doIts[i];
      if (registry.containsKey(thisDoIt.getName()) && !isInitDone()) {
        log.warn("Overriding doit with Id: " + thisDoIt.getName());
      }
      this.registry.put(thisDoIt.getName(), thisDoIt);
    }
  }

}