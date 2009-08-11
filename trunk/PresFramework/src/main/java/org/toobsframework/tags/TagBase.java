package org.toobsframework.tags;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.xml.transform.TransformerException;

import org.apache.xalan.extensions.XSLProcessorContext;
import org.apache.xalan.templates.ElemExtensionCall;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.serializer.SerializationHandler;
import org.toobsframework.pres.xsl.ComponentTransformerHelper;
import org.toobsframework.transformpipeline.domain.IXMLTransformer;
import org.xml.sax.SAXException;

public class TagBase {

  /**
   * Obtain the helper (which is stored as a property in the xsl)
   * @param processorContext is the context call for th transformation
   * @return the context
   * @throws TransformerException if not foud
   */
  protected ComponentTransformerHelper getTransformerHelper(XSLProcessorContext processorContext) throws TransformerException {
    TransformerImpl transformer = processorContext.getTransformer();
    Object th = transformer.getParameter(IXMLTransformer.TRANSFORMER_HELPER);
    if (th == null || !(th instanceof ComponentTransformerHelper)) {
      throw new TransformerException("Internal error: the property " + IXMLTransformer.TRANSFORMER_HELPER + " needs to be properly initialized prior to calling the transformation.");
    }
    ComponentTransformerHelper transformerHelper = (ComponentTransformerHelper) th;
    return transformerHelper;
  }

  /**
   * Write a string to the xml output stream, controlling escaping of XML characters
   * @param processorContext - passed to the tags
   * @param extensionElement - passed to the tags
   * @param s is the string to stream out
   * @param escape - if true, XML characters are converted to XML equivalents, otherwise they are streamed straight
   */
  protected void serialize(XSLProcessorContext processorContext, ElemExtensionCall extensionElement, String s, boolean escape) throws SAXException, MalformedURLException, FileNotFoundException, TransformerException, IOException {
    TransformerImpl transformer = processorContext.getTransformer();
    SerializationHandler handler = transformer.getResultTreeHandler();
    boolean previousEscaping = handler.setEscaping(escape);
    processorContext.outputToResultTree(extensionElement.getStylesheet(), s);
    handler.setEscaping(previousEscaping);
  }

  /**
   * Get a property for a tag from the context passed
   * @param name is the name of the attribute
   * @param processorContext passed to the tag
   * @param extensionElement passed to the tag
   * @return the value or null if the tag is not there
   * @throws TransformerException on error
   */
  protected String getStringProperty(String name, org.apache.xalan.extensions.XSLProcessorContext processorContext, org.apache.xalan.templates.ElemExtensionCall extensionElement) throws TransformerException {
    return extensionElement.getAttribute(name, processorContext.getContextNode(), processorContext.getTransformer());    
  }
  
  /**
   * Get a property for a tag from the context passed, or the default if it does not exist
   * @param name is the name of the attribute
   * @param defaultValue is the value to be used in case it is not there
   * @param processorContext passed to the tag
   * @param extensionElement passed to the tag
   * @return the value or the default if the tag is not there
   * @throws TransformerException on error
   */
  protected String getStringProperty(String name, String defaultValue, org.apache.xalan.extensions.XSLProcessorContext processorContext, org.apache.xalan.templates.ElemExtensionCall extensionElement) throws TransformerException {
    String value = extensionElement.getAttribute(name, processorContext.getContextNode(), processorContext.getTransformer());
    if (value == null) {
      value = defaultValue;
    }
    return value;
  }
  
  /**
   * Get a property for a tag from the context passed, and err if it is not there
   * @param name is the name of the attribute
   * @param processorContext passed to the tag
   * @param extensionElement passed to the tag
   * @return the value 
   * @throws TransformerException on error or the tag missing
   */
  protected String getRequiredStringProperty(String name, String message, org.apache.xalan.extensions.XSLProcessorContext processorContext, org.apache.xalan.templates.ElemExtensionCall extensionElement) throws TransformerException {
    String value = extensionElement.getAttribute(name, processorContext.getContextNode(), processorContext.getTransformer());
    if (value == null || value.length() == 0) {
      throw new TransformerException(message);
    }
    return value;
  }
  
  /**
   * Get a property for a tag from the context passed
   * @param name is the name of the attribute
   * @param processorContext passed to the tag
   * @param extensionElement passed to the tag
   * @return the value or null if the tag is not there
   * @throws TransformerException on error
   */
  protected boolean getBooleanProperty(String name, org.apache.xalan.extensions.XSLProcessorContext processorContext, org.apache.xalan.templates.ElemExtensionCall extensionElement) throws TransformerException {
    String value = getStringProperty(name, processorContext, extensionElement);
    if (value == null) {
      return false;
    }
    value = value.trim();
    return value.equals("true") || value.equals("yes") || value.equals("1");
  }
  
  /**
   * Get a property for a tag from the context passed, or the default if it does not exist
   * @param name is the name of the attribute
   * @param defaultValue is the value to be used in case it is not there
   * @param processorContext passed to the tag
   * @param extensionElement passed to the tag
   * @return the value or the default if the tag is not there
   * @throws TransformerException on error
   */
  protected boolean getBooleanProperty(String name, boolean defaultValue, org.apache.xalan.extensions.XSLProcessorContext processorContext, org.apache.xalan.templates.ElemExtensionCall extensionElement) throws TransformerException {
    String value = getStringProperty(name, processorContext, extensionElement);
    if (value == null) {
      return defaultValue;
    }
    value = value.trim();
    return value.equals("true") || value.equals("yes") || value.equals("1");
  }
  
  /**
   * Get a property for a tag from the context passed, and err if it is not there
   * @param name is the name of the attribute
   * @param processorContext passed to the tag
   * @param extensionElement passed to the tag
   * @return the value 
   * @throws TransformerException on error or the tag missing
   */
  protected boolean getRequiredBooleanProperty(String name, String message, org.apache.xalan.extensions.XSLProcessorContext processorContext, org.apache.xalan.templates.ElemExtensionCall extensionElement) throws TransformerException {
    String value = getStringProperty(name, processorContext, extensionElement);
    if (value == null || value.length() == 0) {
      throw new TransformerException(message);
    }
    value = value.trim();
    return value.equals("true") || value.equals("yes") || value.equals("1");
  }

  /**
   * Get a property for a tag from the context passed
   * @param name is the name of the attribute
   * @param processorContext passed to the tag
   * @param extensionElement passed to the tag
   * @return the value or null if the tag is not there
   * @throws TransformerException on error
   */
  protected int getIntegerProperty(String name, org.apache.xalan.extensions.XSLProcessorContext processorContext, org.apache.xalan.templates.ElemExtensionCall extensionElement) throws TransformerException {
    String value = getStringProperty(name, processorContext, extensionElement);
    if (value == null) {
      return 0;
    }
    value = value.trim();
    return Integer.parseInt(value);
  }
  
  /**
   * Get a property for a tag from the context passed, or the default if it does not exist
   * @param name is the name of the attribute
   * @param defaultValue is the value to be used in case it is not there
   * @param processorContext passed to the tag
   * @param extensionElement passed to the tag
   * @return the value or the default if the tag is not there
   * @throws TransformerException on error
   */
  protected int getIntegerProperty(String name, int defaultValue, org.apache.xalan.extensions.XSLProcessorContext processorContext, org.apache.xalan.templates.ElemExtensionCall extensionElement) throws TransformerException {
    String value = getStringProperty(name, processorContext, extensionElement);
    if (value == null) {
      return defaultValue;
    }
    value = value.trim();
    return Integer.parseInt(value);
  }
  
  /**
   * Get a property for a tag from the context passed, and err if it is not there
   * @param name is the name of the attribute
   * @param processorContext passed to the tag
   * @param extensionElement passed to the tag
   * @return the value 
   * @throws TransformerException on error or the tag missing
   */
  protected int getRequiredIntegerProperty(String name, String message, org.apache.xalan.extensions.XSLProcessorContext processorContext, org.apache.xalan.templates.ElemExtensionCall extensionElement) throws TransformerException {
    String value = getStringProperty(name, processorContext, extensionElement);
    if (value == null || value.length() == 0) {
      throw new TransformerException(message);
    }
    value = value.trim();
    return Integer.parseInt(value);
  }
}
