package org.toobsframework.pres.base;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobsframework.transformpipeline.domain.IXMLTransformer;

public class Transformable {
  protected final Log log = LogFactory.getLog(getClass());

  private IXMLTransformer defaultTransformer;
  private IXMLTransformer htmlTransformer;
  private IXMLTransformer xmlTransformer;

  protected IXMLTransformer getTransformer(List<String> inputXSLs, String contentType) {
    // Figure out which Transformer to run and prepare as
    // necessary for that Transformer.
    if (inputXSLs.size() > 1) {
      if (!"xhtml".equals(contentType)) {
        return this.xmlTransformer;
      } else {
        return this.htmlTransformer;
      }
    } else {
      return this.defaultTransformer;
    }
  }

  public IXMLTransformer getDefaultTransformer() {
    return defaultTransformer;
  }

  public void setDefaultTransformer(IXMLTransformer defaultTransformer) {
    this.defaultTransformer = defaultTransformer;
  }

  public IXMLTransformer getHtmlTransformer() {
    return htmlTransformer;
  }

  public void setHtmlTransformer(IXMLTransformer htmlTransformer) {
    this.htmlTransformer = htmlTransformer;
  }

  public IXMLTransformer getXmlTransformer() {
    return xmlTransformer;
  }

  public void setXmlTransformer(IXMLTransformer xmlTransformer) {
    this.xmlTransformer = xmlTransformer;
  }

}
