package org.toobsframework.pres.url.mapping.strategy;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;
import org.toobsframework.pres.url.UrlDispatchInfo;

public class DefaultDispatchStrategy implements DispatchStrategy {

  private UrlPathHelper urlPathHelper = new UrlPathHelper();

  public UrlDispatchInfo resolveDispatchInfo(HttpServletRequest request) {
    String resourceId = resolveResouceId(request);
    String contentType = resolveContentType(request);
    return new UrlDispatchInfo(this.urlPathHelper.getLookupPathForRequest(request), resourceId, contentType);
  }

  protected String resolveResouceId(HttpServletRequest request) {
    String resourceId = (String)request.getAttribute(DISPATCH_RESOURCE_ID_ATTRIBUTE);
    if (resourceId == null) {
      String urlPath = this.urlPathHelper.getLookupPathForRequest(request);
      resourceId = WebUtils.extractFilenameFromUrlPath(urlPath);
    }
    return resourceId;
  }

  protected String resolveContentType(HttpServletRequest request) {
    String contentType = (String)request.getAttribute(DISPATCH_CONTENT_TYPE_ATTRIBUTE);
    if (contentType == null) {
      contentType = DEFAULT_CONTENT_TYPE;
    }
    return contentType;
  }

  public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
    this.urlPathHelper = urlPathHelper;
  }

}
