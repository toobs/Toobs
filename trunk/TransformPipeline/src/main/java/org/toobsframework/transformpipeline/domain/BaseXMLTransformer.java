package org.toobsframework.transformpipeline.domain;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;

import org.apache.xalan.trace.TraceListener;
import org.apache.xml.utils.DefaultErrorHandler;

public abstract class BaseXMLTransformer implements IXMLTransformer {

  protected URIResolver uriResolver;
  protected TraceListener paramListener;

  public void setURIResolver(URIResolver resolver) {
    this.uriResolver = resolver;
  }

  protected void setFactoryResolver(TransformerFactory tFactory) {
    if (uriResolver == null) {
      uriResolver = new XSLUriResolverImpl();
    }
    tFactory.setURIResolver(uriResolver);
    tFactory.setErrorListener(new DefaultErrorHandler(true));
  }

  public void setParamListener(TraceListener paramListener) {
    this.paramListener = paramListener;
  }
}
