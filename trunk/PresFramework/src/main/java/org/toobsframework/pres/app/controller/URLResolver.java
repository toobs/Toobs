package org.toobsframework.pres.app.controller;

import org.toobsframework.pres.app.AppReader;

public interface URLResolver {

  public static String DEFAULT_VIEW = "[default]";

  public AppRequest resolve(AppReader appReader, String url, String method);

}
