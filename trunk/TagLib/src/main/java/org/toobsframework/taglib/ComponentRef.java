/*
 * Created by IntelliJ IDEA.
 * User: spudney
 * Date: Sep 26, 2008
 * Time: 11:15:02 AM
 */
package org.toobsframework.taglib;

import org.toobsframework.pres.util.ComponentRequestManager;
import org.toobsframework.pres.util.PresConstants;
import org.toobsframework.pres.xsl.ComponentTransformerHelper;
import org.toobsframework.pres.component.manager.IComponentManager;
import org.toobsframework.pres.component.ComponentException;
import org.toobsframework.pres.component.ComponentNotFoundException;
import org.toobsframework.pres.component.ComponentInitializationException;
import org.toobsframework.pres.component.ComponentNotInitializedException;
import org.toobsframework.transformpipeline.domain.IXMLTransformerHelper;
import org.toobsframework.util.Configuration;
import org.toobsframework.servlet.ContextHelper;
import org.toobsframework.exception.ParameterException;
import org.springframework.beans.factory.BeanFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.HashMap;

public class ComponentRef extends BodyTagSupport {

  private static final long serialVersionUID = -5899228093269316358L;

  private static BeanFactory beanFactory;

  //private static ComponentRequestManager reqManager;
  //private static IComponentManager compManager;

  static {
    beanFactory = ContextHelper.getWebApplicationContext();
    //reqManager = (ComponentRequestManager)beanFactory.getBean("componentRequestManager");
    //compManager = (IComponentManager)beanFactory.getBean("IComponentManager");
  }

  private String componentId;
  private String contentType = "xhtml";
  private Map    parameterMap = new HashMap();
  private String dataObjectName;
  private Object dataObject;
  private String transformerHelper;

  public void setParameterMap(Map parameterMap) {
    this.parameterMap = parameterMap;
  }

  protected void addParam(String name, Object value) {
    this.parameterMap.put(name, value);
  }

  public void setdataObject(Object dataObject) {
    this.dataObject = dataObject;
  }

  public void setdataObjectName(String dataObjectName) {
    this.dataObjectName = dataObjectName;
  }

  public void setComponentId(String componentId) {
    this.componentId = componentId;
  }
  public void setcontentType(String contentType) {
    this.contentType = contentType;
  }

  public int doEndTag() throws JspException {

    ComponentTransformerHelper transformerHelper = (ComponentTransformerHelper) beanFactory.getBean(this.transformerHelper);
    //Setup deploytime
    long deployTime;
    if (parameterMap.get(PresConstants.DEPLOY_TIME) == null) {
      deployTime = Configuration.getInstance().getDeployTime();
    } else {
      deployTime = Long.parseLong((String)parameterMap.get(PresConstants.DEPLOY_TIME));
    }

    //Setup Component Request
    transformerHelper.getComponentRequestManager().set(null, null, parameterMap);

    if(dataObject != null && dataObjectName != null) {
      transformerHelper.getComponentRequestManager().get().putParam(dataObjectName, dataObject);
    }

    //Find component
    org.toobsframework.pres.component.Component component = null;
    try {
      component = transformerHelper.getComponentManager().getComponent(componentId, deployTime);
    } catch (ComponentNotFoundException e) {
      throw new JspException("Could not find component with Id:" + componentId, e);
    } catch (ComponentInitializationException e) {
      throw new JspException("Could not initialize component with Id:" + componentId, e);
    }

    //Render Component
    if (contentType == null || contentType.length() == 0) {
      contentType = "xhtml";
    }
    String output = "";
    try {
      output = transformerHelper.getComponentManager().renderComponent(component, contentType, 
          transformerHelper.getComponentRequestManager().get().getParams(), 
          transformerHelper.getComponentRequestManager().get().getParams(), transformerHelper, (HttpServletRequest)pageContext.getRequest(), (HttpServletResponse)pageContext.getResponse(), true);
    } catch (ComponentNotInitializedException e) {
      throw new JspException("Component with Id:" + componentId +": is not intitialized.", e);
    } catch (ComponentException e) {
      throw new JspException("Could not render component with Id:" + componentId, e);
    } catch (ParameterException e) {
      throw new JspException("Could not resolve parameters for component with Id:" + componentId, e);
    } finally {
      transformerHelper.getComponentRequestManager().unset();
    }
    
    //Now output results
    try {
      pageContext.getOut().write(output);
    } catch (IOException e) {
      throw new JspException("Could not output result for component with Id:" + componentId, e);
    }

    return EVAL_PAGE;
  }

  /**
   * @return the transformerHelper
   */
  public String getTransformerHelper() {
    return transformerHelper;
  }

  /**
   * @param transformerHelper the transformerHelper to set
   */
  public void setTransformerHelper(String transformerHelper) {
    this.transformerHelper = transformerHelper;
  }

}