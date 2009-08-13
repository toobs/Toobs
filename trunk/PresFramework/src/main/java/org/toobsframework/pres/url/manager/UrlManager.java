package org.toobsframework.pres.url.manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
import org.toobsframework.pres.url.config.Url;
import org.toobsframework.pres.url.config.Urls;
import org.toobsframework.transformpipeline.domain.XMLTransformerException;
import org.toobsframework.transformpipeline.domain.XMLTransformerFactory;
import org.toobsframework.transformpipeline.domain.XSLUriResolverImpl;

public class UrlManager extends ManagerBase implements IUrlManager {

  private static Log log = LogFactory.getLog(UrlManager.class);
  private ConcurrentHashMap<String, org.toobsframework.pres.url.Url> registry;
  
  public Url getUrl(String Id, long deployTime) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected void registerConfiguration(Object object, String fileName) {
    Urls urlConfig = (Urls) object;
    Url[] urls = urlConfig.getUrl();
    if ((urls != null) && (urls.length > 0)) {
      Url url = null;
      org.toobsframework.pres.url.Url realizedUrl = null;
      for (int j = 0; j < urls.length; j++) {
        try {
          url = urls[j];
          realizedUrl = new org.toobsframework.pres.url.Url();
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

  private void configureUrl(Url url, org.toobsframework.pres.url.Url realizedUrl, String fileName, ConcurrentHashMap<String, org.toobsframework.pres.url.Url> registry) throws IOException {
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
    realizedUrl.init();
  }

  public void afterPropertiesSet() throws Exception {
    registry = new ConcurrentHashMap<String, org.toobsframework.pres.url.Url>();
    loadConfig(Urls.class);
  }

}
