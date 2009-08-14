package org.toobsframework.pres.url.manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobsframework.data.beanutil.converter.DateToStringConverter;
import org.toobsframework.pres.base.ManagerBase;
import org.toobsframework.pres.component.ComponentInitializationException;
import org.toobsframework.pres.component.config.Component;
import org.toobsframework.pres.component.config.Components;
import org.toobsframework.pres.component.manager.ComponentManager;
import org.toobsframework.pres.url.UrlMapping;
import org.toobsframework.pres.url.UrlMappingUtil;
import org.toobsframework.pres.url.config.Url;
import org.toobsframework.pres.url.config.Urls;
import org.toobsframework.transformpipeline.domain.XMLTransformerException;
import org.toobsframework.transformpipeline.domain.XMLTransformerFactory;
import org.toobsframework.transformpipeline.domain.XSLUriResolverImpl;

public class UrlManager extends ManagerBase implements IUrlManager {

  private static Log log = LogFactory.getLog(UrlManager.class);
  private Map<String, UrlMapping> registry;
  private static long localDeployTime = 0L;
  
  @Override
  protected void registerConfiguration(Object object, String fileName) {
    Urls urlConfig = (Urls) object;
    Url[] urls = urlConfig.getUrl();
    if ((urls != null) && (urls.length > 0)) {
      Url url = null;
      UrlMapping realizedUrl = null;
      for (int j = 0; j < urls.length; j++) {
        try {
          url = urls[j];
          realizedUrl = new UrlMapping();
          configureUrl(url, realizedUrl, fileName, registry);
          
          if (registry.containsKey(realizedUrl.getPattern()) && !isInitDone()) {
            log.warn("Overriding url with pattern: " + realizedUrl.getPattern());
          }
          registry.put(realizedUrl.getPattern(), realizedUrl);
        } catch (Exception e) {
          log.warn("Error configuring and registering component " + url.getPattern() + ": " + e.getMessage(), e);
        }
      }
    }
  }

  private void configureUrl(Url url, UrlMapping realizedUrl, String fileName, Map<String, UrlMapping> registry) throws IOException {
    if (url.getComponentId() == null && url.getLayoutId() == null && url.getDoItId() == null) {
      throw new IOException("Url with pattern " + url.getPattern() + " requires one of componentId or LayoutId or DoItId set");
    }
    if (url.getComponentId() != null && url.getLayoutId() != null) {
      throw new IOException("Ambiguous: Url with pattern " + url.getPattern() + " has both componentId and layoutId set");
    } else if (url.getComponentId() != null && url.getDoItId() != null) {
      throw new IOException("Ambiguous: Url with pattern " + url.getPattern() + " has both componentId and doItId set");
    } else if (url.getLayoutId() != null && url.getDoItId() != null) {
      throw new IOException("Ambiguous: Url with pattern " + url.getPattern() + " has both layoutId and doItId set");
    }
    realizedUrl.setPattern(url.getPattern());
    realizedUrl.setContentType(url.getContentType());
    realizedUrl.setDoItId(url.getDoItId());
    realizedUrl.setLayoutId(url.getLayoutId());
    realizedUrl.setComponentId(url.getComponentId());
    realizedUrl.setWildcardMatching(url.getWildcardMatch());
    realizedUrl.init();
    registry.put(url.getPattern(), realizedUrl);
  }
  
  public UrlMapping getUrlMapping(String pattern, long deployTime) throws Exception {
    if (isDoReload() || deployTime > localDeployTime) {
      //Date initStart = new Date();
      this.afterPropertiesSet();
      //Date initEnd = new Date();
      //log.info("Init Time: " + (initEnd.getTime() - initStart.getTime()));
    }
    localDeployTime = deployTime;

    String[] paths = UrlMappingUtil.tokenizePath(pattern);
    
    for (UrlMapping urlMapping : registry.values()) {
      if (urlMapping.matches(paths)) {
        return urlMapping;
      }
    }
    return null;
  }

  public void afterPropertiesSet() throws Exception {
    registry = new ConcurrentHashMap<String, UrlMapping>();
    loadConfig(Urls.class);
  }

}
