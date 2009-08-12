package org.toobsframework.transformpipeline.domain;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

public class XSLUriResolverImpl implements URIResolver, ApplicationContextAware, InitializingBean {
  private static final Log log = LogFactory.getLog(XSLUriResolverImpl.class);

  public static String COMPONENT_MANAGER_XSL = "component-manager.xsl";

  protected ApplicationContext applicationContext;
  protected Map<String, URL> sourceMap;
  private boolean doReload;
  private List<String> base;

  public XSLUriResolverImpl() {
  }

  public XSLUriResolverImpl(List<String> base) {
    this.base = base;
  }

  public void afterPropertiesSet() throws Exception {
    if (this.base == null) {
      this.base = new ArrayList<String>();
      this.base.add("xsl/");
    }
    sourceMap = new ConcurrentHashMap<String,URL>();
    sourceMap.put(COMPONENT_MANAGER_XSL, resolveClasspathResource(COMPONENT_MANAGER_XSL, ""));
  }

  public Source resolve(String xslFile, String base) throws TransformerException {
    if (log.isDebugEnabled()) {
      log.debug("ENTER XSLUriResolverImpl.resolve('" + xslFile + "', '" + base + "');");
    }

    Source xslSource = null;
    URL xslUrl = sourceMap.get(xslFile);
    try {
      if (xslUrl == null || doReload) {
        if (applicationContext != null) {
          xslUrl = resolveContextResource(xslFile,"");
        }
        if (xslUrl == null) {
          xslUrl = resolveClasspathResource(xslFile,"");
        }
        if (xslUrl == null) {
          throw new TransformerException("xsl " + xslFile + " not found");
        }
        sourceMap.put(xslFile, xslUrl);
      }

      xslSource = new StreamSource(xslUrl.openStream());
      xslSource.setSystemId(xslUrl.getPath());
    } catch (IOException e) {
      log.error("XSL File " + xslFile + " had IOException " + e.getMessage(), e);
      throw new TransformerException("xsl " + xslFile + " cannot be loaded");
    }

    if (log.isDebugEnabled()) {
      log.debug("EXIT XSLUriResolverImpl.resolve('" + xslFile + "', '" + base + "');");
    }
    return xslSource;
  }

  protected URL resolveContextResource(String xslFile, String string) throws IOException {
    Resource configFileURL = null;
    String systemId = null;
    for (int i = 0; i < this.base.size(); i++) {
      systemId = this.base.get(i) + xslFile;

      if (log.isDebugEnabled()) {
        log.debug("Checking for: " + systemId);
      }
      configFileURL = applicationContext.getResource(systemId);

      if (null != configFileURL) {
        break;
      }
    }

    return configFileURL.getURL();
  }

  protected URL resolveClasspathResource(String xslFile, String base) throws TransformerException {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    URL configFileURL = null;
    String systemId = null;
    for (int i = 0; i < this.base.size(); i++) {
      systemId = this.base.get(i) + xslFile;

      if (log.isDebugEnabled()) {
        log.debug("Checking for: " + systemId);
      }
      configFileURL = classLoader.getResource(systemId);

      if (null != configFileURL) {
        break;
      }
    }

    return configFileURL;
  }

  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  public void setDoReload(boolean doReload) {
    this.doReload = doReload;
  }

  public void setBase(List<String> base) {
    this.base = base;
  }

}
