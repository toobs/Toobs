package org.toobsframework.pres.doit;

import org.toobsframework.exception.BaseException;


public class DoItNotFoundException extends BaseException {

  private static final long serialVersionUID = 1L;

  private String doItId;

  public DoItNotFoundException(String doItId) {
    super("Component with Id " + doItId + " not found in registry");
    this.setDoItId(doItId);
  }
  public void setDoItId(String doItId) {
    this.doItId = doItId;
  }
  public String getDoItId() {
    return doItId;
  }

}
