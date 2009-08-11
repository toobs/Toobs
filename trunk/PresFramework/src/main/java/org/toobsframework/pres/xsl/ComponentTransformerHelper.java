package org.toobsframework.pres.xsl;

import org.toobsframework.pres.component.dataprovider.api.IDataProvider;
import org.toobsframework.pres.component.manager.IComponentManager;
import org.toobsframework.pres.layout.manager.IComponentLayoutManager;
import org.toobsframework.pres.util.ComponentRequestManager;
import org.toobsframework.transformpipeline.domain.IXMLTransformerHelper;

public class ComponentTransformerHelper implements IXMLTransformerHelper {
  protected ComponentRequestManager componentRequestManager = null;
  protected IComponentManager componentManager = null;
  protected IComponentLayoutManager componentLayoutManager = null;
  protected IDataProvider dataProvider;

  
  public IDataProvider getDataProvider() {
    return dataProvider;
  }

  public void setDataProvider(IDataProvider dataProvider) {
    this.dataProvider = dataProvider;
  }
  
  /**
   * @return the componentRequestManager
   */
  public ComponentRequestManager getComponentRequestManager() {
    return componentRequestManager;
  }
  
  /**
   * @param componentRequestManager the componentRequestManager to set
   */
  public void setComponentRequestManager(
      ComponentRequestManager componentRequestManager) {
    this.componentRequestManager = componentRequestManager;
  }
  
  /**
   * @return the componentManager
   */
  public IComponentManager getComponentManager() {
    return componentManager;
  }
  
  /**
   * @param componentManager the componentManager to set
   */
  public void setComponentManager(IComponentManager componentManager) {
    this.componentManager = componentManager;
  }
  
  /**
   * @return the componentLayoutManager
   */
  public IComponentLayoutManager getComponentLayoutManager() {
    return componentLayoutManager;
  }
  
  /**
   * @param componentLayoutManager the componentLayoutManager to set
   */
  public void setComponentLayoutManager(
      IComponentLayoutManager componentLayoutManager) {
    this.componentLayoutManager = componentLayoutManager;
  }

}
