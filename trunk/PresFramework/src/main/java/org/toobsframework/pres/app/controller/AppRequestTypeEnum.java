package org.toobsframework.pres.app.controller;

public enum AppRequestTypeEnum {
  LAYOUT(0),
  COMPONENT(1),
  DOIT(2);

  int id;
  AppRequestTypeEnum(int id) {
    this.id = id;
  }

  public int asInt() {
    return this.id;
  }
}
