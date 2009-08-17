package org.toobsframework.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobsframework.pres.url.UrlDispatchInfo;

public class BaseRequestManager {
  protected final Log log = LogFactory.getLog(getClass());

  protected static ThreadLocal<IRequest> requestHolder = new ThreadLocal<IRequest>();

  public IRequest set(UrlDispatchInfo dispatchInfo, HttpServletRequest httpRequest, HttpServletResponse httpResponse, Map<String,Object> params, boolean expectResponse) {
    IRequest request = new BaseRequest(dispatchInfo, httpRequest, httpResponse, params, expectResponse);
    if (get() != null) {
      log.warn("REQUEST ALREADY SET");
    }
    requestHolder.set(request);
    return request;
  }

  public IRequest get() {
    IRequest request = requestHolder.get();
    return request;
  }

  public void unset() {
    requestHolder.set(null);
  }
}
