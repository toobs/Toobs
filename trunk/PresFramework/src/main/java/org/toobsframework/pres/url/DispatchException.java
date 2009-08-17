package org.toobsframework.pres.url;

import org.toobsframework.exception.BaseException;


public class DispatchException extends BaseException {

  private static final long serialVersionUID = 1L;

  private String uri;

  public DispatchException(String uri) {
    super("Dispatch failed for uri " + uri);
    this.uri = uri;
  }

  public DispatchException(String uri, Throwable cause) {
    super("Dispatch failed for uri " + uri + " : " + cause.getMessage(), cause);
    this.uri = uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getUri() {
    return uri;
  }

}
