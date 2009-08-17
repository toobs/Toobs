package org.toobsframework.pres.layout;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.toobsframework.pres.component.config.Parameter;
import org.toobsframework.pres.layout.config.Section;


public class RuntimeLayoutConfig {
  private Map<String,Section> sections;
  private Map<String,ComponentRef> componentRefs;
  private Map<String,Parameter> params;
  private Map<String,Parameter> transformParams;
  private String noAccessLayout;

  public Map<String,Parameter> getParams() {
    return params;
  }
  public Parameter[] getAllParams() {
    if (params == null) {
      return null;
    }
    Parameter[] allParams = new Parameter[params.size()];
    return (Parameter[])params.values().toArray(allParams);
  }
  public void addParam(Parameter param) {
    addParam(new Parameter[] {param});
  }
  public void addParam(Parameter[] param) {
    if (this.params == null) {
      params = new LinkedHashMap<String,Parameter>();
    }
    for (int i = 0; i < param.length; i++) {
      params.put(param[i].getName(), param[i]);
    }
  }

  public Map<String,Parameter> getTransformParams() {
    return transformParams;
  }
  public Parameter[] getAllTransformParams() {
    if (transformParams == null) {
      return null;
    }
    Parameter[] allParams = new Parameter[transformParams.size()];
    return (Parameter[])transformParams.values().toArray(allParams);
  }
  public void addTransformParam(Parameter param) {
    addTransformParam(new Parameter[] {param});
  }
  public void addTransformParam(Parameter[] param) {
    if (this.transformParams == null) {
      transformParams = new LinkedHashMap<String,Parameter>();
    }
    for (int i = 0; i < param.length; i++) {
      transformParams.put(param[i].getName(), param[i]);
    }
  }

  public Map<String,Section> getSections() {
    return sections;
  }
  public Collection<? extends Section> getAllSections() {
    if (sections == null) {
      return null;
    }
    return sections.values();
  }
  public void addSection(Section section) {
    addSection(new Section[] {section});
  }
  public void addSection(Section[] section) {
    if (this.sections == null) {
      sections = new LinkedHashMap<String,Section>();
    }
    for (int i = 0; i < section.length; i++) {
      sections.put(section[i].getId(), section[i]);
    }
  }
  public void addSection(List<Section> sectionList) {
    if (this.sections == null) {
      sections = new LinkedHashMap<String,Section>();
    }
    for (int i = 0; i < sectionList.size(); i++) {
      sections.put(sectionList.get(i).getId(), sectionList.get(i));
    }
  }

  public String getNoAccessLayout() {
    return noAccessLayout;
  }
  public void setNoAccessLayout(String noAccessLayout) {
    this.noAccessLayout = noAccessLayout;
  }

  public void addComponentRef(ComponentRef componentRef) {
    if (this.componentRefs == null) {
      componentRefs = new LinkedHashMap<String,ComponentRef>();
    }
    this.componentRefs.put(componentRef.getComponent().getId(), componentRef);
  }
  public void setComponentRefs(Map<String,ComponentRef> componentRefs) {
    this.componentRefs = componentRefs;
  }
  public Map<String,ComponentRef> getComponentRefs() {
    return componentRefs;
  }

}
