package org.toobsframework.pres.url.mapping.strategy;

import javax.servlet.http.HttpServletRequest;

import org.toobsframework.pres.url.UrlDispatchInfo;

public interface DispatchStrategy {
  public static final String DISPATCH_RESOURCE_ID_ATTRIBUTE = "org.toobsframework.pres.url.mapping.DISPATCH_RESOURCE_ID";
  public static final String DISPATCH_CONTENT_TYPE_ATTRIBUTE = "org.toobsframework.pres.url.mapping.DISPATCH_CONTENT_TYPE";
  public static final String DEFAULT_CONTENT_TYPE = "xhtml";

  public abstract UrlDispatchInfo resolveDispatchInfo(HttpServletRequest request);

}