package org.toobsframework.pres.layout;

import org.toobsframework.pres.component.Component;
import org.toobsframework.pres.component.config.Parameters;

public class ComponentRef {
  private final Component component;
  private final Parameters parameters;

  public ComponentRef(Component component, Parameters parameters) {
    this.component = component;
    this.parameters = parameters;
  }

  public Component getComponent() {
    return component;
  }

  public Parameters getParameters() {
    return parameters;
  }
}
