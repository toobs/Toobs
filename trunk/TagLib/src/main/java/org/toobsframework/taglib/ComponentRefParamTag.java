package org.toobsframework.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

public class ComponentRefParamTag extends BodyTagSupport {

  private static final long serialVersionUID = -8334042535228392249L;

  protected String name; // 'value' attribute
  protected Object value; // 'value' attribute
  protected boolean valueSpecified; // status

  public ComponentRefParamTag() {
    super();
    init();
  }

  private void init() {
    name = null;
    value = null;
    valueSpecified = false;
  }

  public int doEndTag() throws JspException {
    Tag t = findAncestorWithClass(this, ComponentRef.class);
    if (t == null) {
      throw new JspTagException("Toobs param cannot be used outside of a component");
    }
    ComponentRef parent = (ComponentRef) t;

    Object input = null;
    // determine the input by...
    if (valueSpecified) {
      // ... reading 'value' attribute
      input = value;
    } else {
      // ... retrieving and trimming our body (TLV has ensured that it's
      // non-empty)
      input = bodyContent.getString().trim();
    }
    parent.addParam(name, input);

    return EVAL_PAGE;
  }

  // Releases any resources we may have (or inherit)
  public void release() {
    init();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    if (value != null) valueSpecified = true;
    this.value = value;
  }
}
