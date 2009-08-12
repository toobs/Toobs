package org.toobsframework.pres.app.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

public interface IAppHandler {

  /**
   * 
   * Retrieves the URL path to use for lookup and delegates to
   * <code>getViewNameForUrlPath</code>.
   * 
   * @throws Exception Exception fetching or rendering component.
   * @see #getViewNameForUrlPath
   * 
   */
  public abstract ModelAndView handleRequestInternal(
      HttpServletRequest request, HttpServletResponse response)
      throws Exception;

}