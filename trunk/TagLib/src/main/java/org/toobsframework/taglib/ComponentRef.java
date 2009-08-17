package org.toobsframework.taglib;

import org.toobsframework.pres.url.UrlDispatchInfo;
import org.toobsframework.pres.xsl.ComponentTransformerHelper;
import org.toobsframework.pres.component.ComponentException;
import org.toobsframework.pres.component.ComponentNotFoundException;
import org.toobsframework.pres.component.ComponentInitializationException;
import org.toobsframework.pres.component.ComponentNotInitializedException;

import org.toobsframework.util.IRequest;
import org.toobsframework.servlet.ContextHelper;
import org.toobsframework.exception.ParameterException;
import org.springframework.beans.factory.BeanFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
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
  private Map<String,Object>    parameterMap = new HashMap<String,Object>();
  private String dataObjectName;
  private Object dataObject;
  private String transformerHelper;

  public void setParameterMap(Map<String,Object> parameterMap) {
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

    UrlDispatchInfo dispatchInfo = new UrlDispatchInfo("jsptag:" + componentId, componentId, "xhtml");
    //Setup Component Request
    transformerHelper.getComponentRequestManager().set(dispatchInfo, (HttpServletRequest)pageContext.getRequest(), (HttpServletResponse)pageContext.getResponse(), parameterMap, false);

    IRequest request = transformerHelper.getComponentRequestManager().get();
    if(dataObject != null && dataObjectName != null) {
      request.putParam(dataObjectName, dataObject);
    }

    //Find component
    org.toobsframework.pres.component.Component component = null;
    try {
      component = transformerHelper.getComponentManager().getComponent(componentId);
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
      output = transformerHelper.getComponentManager().renderComponent(request, component, contentType, 
          transformerHelper.getComponentRequestManager().get().getParams(), 
          transformerHelper.getComponentRequestManager().get().getParams(), transformerHelper, true);
    } catch (ComponentNotInitializedException e) {
      throw new JspException("Component with Id:" + componentId +": is not intitialized.", e);
    } catch (ComponentException e) {
      throw new JspException("Could not render component with Id:" + componentId, e);
    } catch (ParameterException e) {
      throw new JspException("Could not resolve parameters for component with Id:" + componentId, e);
    } catch (IOException e) {
      throw new JspException("Could not render component with Id:" + componentId, e);
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