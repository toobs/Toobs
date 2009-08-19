/*
 * This file is licensed to the Toobs Framework Group under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Toobs Framework Group licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file 
 * except in compliance with the License.  You may obtain a copy of the 
 * License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
