package org.toobsframework.pres.base;

import javax.xml.transform.URIResolver;

import org.apache.xalan.trace.TraceListener;
import org.toobsframework.transformpipeline.domain.IXMLTransformer;
import org.toobsframework.transformpipeline.domain.XMLTransformerFactory;

public abstract class XslManagerBase extends ManagerBase {

  protected IXMLTransformer defaultTransformer;
  protected IXMLTransformer htmlTransformer;
  protected IXMLTransformer xmlTransformer;

  private URIResolver xslResolver;
  private TraceListener paramListener;

  public void afterPropertiesSet() throws Exception {
    super.afterPropertiesSet();

    if (this.xslResolver == null) {
      throw new Exception("xslResolver property must be set");
    }

    xmlTransformer = XMLTransformerFactory.getInstance().getChainTransformer(XMLTransformerFactory.OUTPUT_FORMAT_XML, xslResolver, paramListener, configuration);
    htmlTransformer = XMLTransformerFactory.getInstance().getChainTransformer(XMLTransformerFactory.OUTPUT_FORMAT_HTML, xslResolver, paramListener, configuration);
    defaultTransformer = XMLTransformerFactory.getInstance().getDefaultTransformer(xslResolver, configuration);

  }

  public void setParamListener(TraceListener paramListener) {
    this.paramListener = paramListener;
  }

  public void setXslResolver(URIResolver xslResolver) {
    this.xslResolver = xslResolver;
  }

}
