package org.toobsframework.pres.xsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.serializer.SerializationHandler;
import org.toobsframework.data.beanutil.BeanMonkey;
import org.toobsframework.pres.component.Component;
import org.toobsframework.pres.component.Transform;
import org.toobsframework.pres.component.config.Parameter;
import org.toobsframework.pres.util.ParameterUtil;
import org.toobsframework.pres.util.PresConstants;
import org.toobsframework.transformpipeline.domain.IXMLTransformer;
import org.toobsframework.transformpipeline.domain.XMLTransformerException;
import org.toobsframework.util.Configuration;
import org.toobsframework.util.IRequest;
import org.w3c.dom.Node;


@SuppressWarnings("unchecked")
public class ComponentHelper {

  private static final String COMPONENT_HELPER_PARAMETERS = "componentHelperParameters";

  /** To get the logger instance */
  private static Log log = LogFactory.getLog(ComponentHelper.class);
  
  protected boolean debugComponents;
  protected String layoutExtension;
  protected String componentExtension;
  
  public ComponentHelper() {
    debugComponents = Configuration.getInstance().getDebugComponents();
    layoutExtension = Configuration.getInstance().getLayoutExtension();
    componentExtension = Configuration.getInstance().getComponentExtension();
  }
  
  /**
   * Function extention for decoding an xml escaped string.  Replaces
   * &lt; &gt; &quot etc. with actual characters.
   *
   * @exception XMLTransformerException if a Transform Exception Occurred.
   * @return String
   */
  public static String getSummary(String str, String length) throws
      XMLTransformerException {
    String strippedString = BeanMonkey.getSummary(str, Integer.parseInt(length));
    return strippedString;
  }

  // ----------------------------------- BEGIN Public Tag Definitions --------------------------------------- //
  
  /**
   * Public Tag - Component - insert a component in the result stream
   * <p>
   * <pre><code>
   *   &lt;toobs:component componentId="<i>id</i>" contentType="<i>type</i>" loader="<i>loader</i>"&gt;
   *   &lt;/toobs:component>
   * </code></pre>
   * 
   * implicit DTD for component
   * 
   * <pre><code>
   * &lt;!ELEMENT toobs:component (toobs:parameter*)&gt
   * &lt;!ATTLIST toobs:component
   * componentId CDATA #REQUIRED
   * contentType CDATA #IMPLIED
   * loader NMTOKENS #IMPLIED&gt;
   * </code></pre>
   * 
   * Where
   * <p>
   * <ul>
   * <li>componentId - is the Id of the component, as specified in the .cc.xml file
   * <li>contentType - is the type of content to be rendered - default="xhtml")
   * <li>loader - is the type of component loading desired,either "direct" or "lazy" (lazy is used for ajax) - default="direct")
   * </ul>
   */
  public void component(org.apache.xalan.extensions.XSLProcessorContext processorContext, 
      org.apache.xalan.templates.ElemExtensionCall extensionElement) throws TransformerException {
    
    // initialize
    TransformerImpl transformer = processorContext.getTransformer();
    Object th = transformer.getParameter(IXMLTransformer.TRANSFORMER_HELPER);
    if (th == null || !(th instanceof ComponentTransformerHelper)) {
      throw new TransformerException("Internal error: the property " + IXMLTransformer.TRANSFORMER_HELPER + " needs to be properly initialized prior to calling the transformation.");
    }
    ComponentTransformerHelper transformerHelper = (ComponentTransformerHelper) th;
    
    // Get tag attributes
    String componentId = extensionElement.getAttribute("componentId", processorContext.getContextNode(), processorContext.getTransformer());
    String contentType = extensionElement.getAttribute("contentType", processorContext.getContextNode(), processorContext.getTransformer());
    String loader = extensionElement.getAttribute("loader", processorContext.getContextNode(), processorContext.getTransformer());
    
    if (contentType == null || contentType.length() == 0) {
      contentType = "xhtml";
    }
    if (loader == null) {
      loader = "direct";
    }
    
    // Obtain parameters
    List<Node> parameterList = new ArrayList<Node>();
    transformer.setParameter(COMPONENT_HELPER_PARAMETERS, parameterList);
    transformer.executeChildTemplates(extensionElement, true);
    transformer.setParameter(COMPONENT_HELPER_PARAMETERS, new Boolean(false));

    // Compute result
    Random randomGenerator = new Random();
    StringBuffer sb = new StringBuffer(); 
    IRequest request = transformerHelper.getComponentRequestManager().get();
    if (request == null) {
      throw new TransformerException("Internal error: Invalid request passed to the layout throught the " + IXMLTransformer.TRANSFORMER_HELPER);
    }
    
    try {
  
      if(loader.equalsIgnoreCase("direct")) {
        Map<String, Object> inParams = getRequestParameters("Component:", componentId, request.getParams(), parameterList);
        Component component = transformerHelper.getComponentManager().getComponent(ParameterUtil.resolveParam(componentId, inParams)[0], getDeployTime(request));
        appendStyle(sb, component);
        String randId = componentId + "_"+ randomGenerator.nextInt();
        if(debugComponents && !component.getId().equalsIgnoreCase("componentFrame")) {
          prependDebug(sb, component, randId, contentType);
        }
        sb.append(transformerHelper.getComponentManager().renderComponent(component, contentType, inParams, request.getParams(), transformerHelper, false));
        if(debugComponents && !component.getId().equalsIgnoreCase("componentFrame")) {
          appendDebug(sb, component, randId, contentType);
        }
        appendControllers(sb, component);
        
      } else if (loader.equalsIgnoreCase("lazy")) {
        Map inParams = getRequestParameters("Component:", componentId, new HashMap(), parameterList);
        appendLazyAJAXCall(sb, componentId, inParams);
      }
      SerializationHandler handler = transformer.getResultTreeHandler();
      boolean previousEscaping = handler.setEscaping(false);
      processorContext.outputToResultTree(extensionElement.getStylesheet(), sb.toString());
      handler.setEscaping(previousEscaping);
    } catch (Exception e) {
      throw new TransformerException("Error executing toobs component insertion: " + e.getMessage(), e);
    }
  }
  
  /**
   * Public Tag - Parameter - pass a parameter to a component or layout
   * <p>
   * <pre><code>
   *   &lt;toobs:parameter useContext="<i>true-or-false</i>" /&gt;
   * </code></pre>
   * 
   * implicit DTD for parameter
   * 
   * <pre><code>
   * &lt;!ELEMENT toobs:parameter (EMPTY)?>
   * &lt;!ATTLIST toobs:parameter
   * useContext CDATA #IMPLIED&gt;
   * </code></pre>
   * 
   * Where
   * <p>
   * <ul>
   * <li>useContext - true or false, defines if the node to be used as a parameter is the node in context for
   *   the current path in the xslt.  If true, the node is obtained and passed to the surrounding component or layour tags
   * </ul>
   */
  public void parameter(org.apache.xalan.extensions.XSLProcessorContext processorContext, 
      org.apache.xalan.templates.ElemExtensionCall extensionElement) throws TransformerException {
    TransformerImpl transformer = processorContext.getTransformer();
    Object p = transformer.getParameter(COMPONENT_HELPER_PARAMETERS);
    
    if (p == null || !(p instanceof List)) {
      throw new TransformerException("toobs parameter declarartion needs to be nested inside of a toobs component");
    }
    List<Node> parameterList = (List<Node>) p;

    String useContext = extensionElement.getAttribute("use-context", processorContext.getContextNode(), processorContext.getTransformer());
    if (useContext != null && (useContext.equalsIgnoreCase("true") || useContext.equalsIgnoreCase("yes") || useContext.equalsIgnoreCase("1"))) {
      // in this case, use the context node to copy the parameter to the component call
      parameterList.add(processorContext.getContextNode());
    } else {
      // in this case, use the parameters supplied in the tag
      // TODO: P2- Implement direct parameter passing in xslt
    }
    
    // this method does not execute any child templates.  No xslt instructions can be nested under it
  }
  
  /**
   * Public Tag - Layout - insert a sub-layout in the result stream
   * <p>
   * <pre><code>
   *   &lt;toobs:layout layoutId="<i>id</i>"&gt;
   *   &lt;/toobs:layout>
   * </code></pre>
   * 
   * implicit DTD for layout
   * 
   * <pre><code>
   * &lt;!ELEMENT toobs:layout (toobs:parameter*)&gt
   * &lt;!ATTLIST toobs:layout
   * layoutId CDATA #REQUIRED&gt;
   * </code></pre>
   * 
   * Where
   * <p>
   * <ul>
   * <li>layoutId - is the Id of the layout, as specified in the .clc.xml file
   * </ul>
   */
  public void layout(org.apache.xalan.extensions.XSLProcessorContext processorContext, 
      org.apache.xalan.templates.ElemExtensionCall extensionElement) throws TransformerException {
    
    // Initialize
    TransformerImpl transformer = processorContext.getTransformer();
    Object th = transformer.getParameter(IXMLTransformer.TRANSFORMER_HELPER);
    if (th == null || !(th instanceof ComponentTransformerHelper)) {
      throw new TransformerException("Internal error: the property " + IXMLTransformer.TRANSFORMER_HELPER + " needs to be properly initialized prior to calling the transformation.");
    }
    ComponentTransformerHelper transformerHelper = (ComponentTransformerHelper) th;
    
    // Obtain Tag Attributes
    String layoutId = extensionElement.getAttribute("layoutId", processorContext.getContextNode(), processorContext.getTransformer());
  
    // Obtain parameters
    List<Node> parameterList = new ArrayList<Node>();
    transformer.setParameter(COMPONENT_HELPER_PARAMETERS, parameterList);
    transformer.executeChildTemplates(extensionElement, true);
    transformer.setParameter(COMPONENT_HELPER_PARAMETERS, new Boolean(false));

    // Compute Results
    IRequest request = transformerHelper.getComponentRequestManager().get();
    if (request == null) {
      throw new TransformerException("Internal error: Invalid request passed to the layout throught the " + IXMLTransformer.TRANSFORMER_HELPER);
    }

    try {
      request.setParams(getRequestParameters("Layout:", layoutId, request.getParams(), parameterList));      
      String s = transformerHelper.getComponentLayoutManager().getLayout(ParameterUtil.resolveParam(layoutId, request.getParams())[0], getDeployTime(request)).render(request, transformerHelper);
      SerializationHandler handler = transformer.getResultTreeHandler();

      boolean previousEscaping = handler.setEscaping(false);
      processorContext.outputToResultTree(extensionElement.getStylesheet(), s);
      handler.setEscaping(previousEscaping);
     
    } catch (Exception ex) {
      throw new TransformerException("Error obtaining layout with id=" + layoutId + ": " + ex.getMessage(), ex);
    }
  }

  /**
   * Public Tag - ComponentUrl - insert a component or sub-layout by URL the result stream
   * <p>
   * <pre><code>
   *   &lt;toobs:componentUrl url="<i>url</i>" contentType="<i>type</i>"</i>" /&gt;
   * </code></pre>
   * 
   * implicit DTD for componentUrl
   * 
   * <pre><code>
   * &lt;!ELEMENT toobs:componentUrl (EMPTY)?&gt
   * &lt;!ATTLIST toobs:componentUrl
   * url CDATA #REQUIRED
   * contentType CDATA #IMPLIED&gt;
   * </code></pre>
   * 
   * Where
   * <p>
   * <ul>
   * <li>url is the url coming from the browser
   * <li>contentType - is the type of content to be rendered - default="xhtml")
   * </ul>
   */
  public void componentUrl(org.apache.xalan.extensions.XSLProcessorContext processorContext, 
      org.apache.xalan.templates.ElemExtensionCall extensionElement) throws TransformerException {

    // Initialize
    TransformerImpl transformer = processorContext.getTransformer();
    Object th = transformer.getParameter(IXMLTransformer.TRANSFORMER_HELPER);
    if (th == null || !(th instanceof ComponentTransformerHelper)) {
      throw new TransformerException("Internal error: the property " + IXMLTransformer.TRANSFORMER_HELPER + " needs to be properly initialized prior to calling the transformation.");
    }
    ComponentTransformerHelper transformerHelper = (ComponentTransformerHelper) th;
    
    // Get attributes
    String componentUrl = extensionElement.getAttribute("url", processorContext.getContextNode(), processorContext.getTransformer());
    String contentType = extensionElement.getAttribute("contentType", processorContext.getContextNode(), processorContext.getTransformer());
    
    if (contentType == null || contentType.length() == 0) {
      contentType = "xhtml";
    }

    // Compute Results
    IRequest request = transformerHelper.getComponentRequestManager().get();
    if (request == null) {
      throw new TransformerException("Internal error: Invalid request passed to the layout throught the " + IXMLTransformer.TRANSFORMER_HELPER);
    }
    try {
      StringBuffer sb = new StringBuffer(); 
      Map inParams = new HashMap(request.getParams());
      String componentId = parseUrl("Component:", componentUrl, request, inParams);
      if (componentId.indexOf(layoutExtension) != -1) {
        sb.append(transformerHelper.getComponentLayoutManager().getLayout(ParameterUtil.resolveParam(componentId.replace(layoutExtension, ""), request.getParams())[0], getDeployTime(request)).render(request, transformerHelper));
      } else {
        Component component = transformerHelper.getComponentManager().getComponent(ParameterUtil.resolveParam(componentId, inParams)[0], getDeployTime(request));
        sb.append(transformerHelper.getComponentManager().renderComponent(component, contentType, inParams, request.getParams(), transformerHelper, false));
        appendControllers(sb, component);
      }

      SerializationHandler handler = transformer.getResultTreeHandler();
      boolean previousEscaping = handler.setEscaping(false);
      processorContext.outputToResultTree(extensionElement.getStylesheet(), sb.toString());
      handler.setEscaping(previousEscaping);
    } catch (Exception ex) {
      throw new TransformerException("Error obtaining component with url=" + componentUrl + ": " + ex.getMessage(), ex);
    }
  }
   
 
  /*
  public String getUserAgent(ComponentTransformerHelper transformerHelper) throws XMLTransformerException {
    try {
      IRequest request = transformerHelper.getComponentRequestManager().get();
      if (request == null) {
        throw new XMLTransformerException("Invalid request");
      }
      return request.getHttpRequest().getHeader("user-agent");
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
      throw new XMLTransformerException(ex);
    }
  }*/
  
  // ----------------------------------- END Public Tag Definitions --------------------------------------- //

  
  protected long getDeployTime(IRequest request) {
    long deployTime = 0L;
    if (request.getParams().containsKey(PresConstants.DEPLOY_TIME)) {
      deployTime = Long.parseLong((String)request.getParams().get(PresConstants.DEPLOY_TIME));
    } else {
      deployTime = Configuration.getInstance().getDeployTime();
    }
    return deployTime;
  }


  protected void appendLazyAJAXCall(StringBuffer sb, String componentId, Map parameters) {
    Random randomGenerator = new Random();
    //Create container id
    String container = componentId + "_"+ randomGenerator.nextInt();
    //Create url
    StringBuffer url = new StringBuffer();
    url.append(componentId + componentExtension + "?");
    //Create url params
    Iterator paramIt = parameters.keySet().iterator();
    while(paramIt.hasNext()) {
      String thisKey = (String) paramIt.next();
      
      url.append(thisKey + "=" + parameters.get(thisKey) + "&amp;");
    }
    //Create HTML
    sb.append("<div id=\"" + container + "\" class=\"loading\" >\n");
    sb.append("Loading...\n");
    sb.append("</div>\n");
    sb.append("<script type=\"text/javascript\">\n");
    sb.append("Toobs.Controller.lazyLoadComp('" + container + "','" + url + "');\n");
    sb.append("</script>\n");
  }

  protected void appendStyle(StringBuffer sb, Component component) {
    if (component.getStyles().length > 0) {
      for (int i=0; i < component.getStyles().length; i++) {
        if (component.getStyles()[i].length() > 0) {
          sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"/common/css/component/" + component.getStyles()[i] + ".css\"></link>");
        }
      }
    }
  }

  protected void prependDebug(StringBuffer sb, Component component, String randId, String contentType) {
    sb.append("<div id=\"" + randId + "_stats\" style=\"display:none;\" class=\"component_stats bluebox\">");
    sb.append("<h2>File: " + component.getFileName() + "</h2>");
    sb.append("<h2>Component: " + component.getId() + "</h2>");
    sb.append("<h2>XSL: ");
    Vector contentTransforms = (Vector) component.getTransforms().get(contentType);
    if (contentTransforms != null) {
      Iterator it = contentTransforms.iterator();
      while (it.hasNext()) {
        Transform transform = (Transform) it.next();
        sb.append(transform.getTransformName());
      }
    }
    sb.append(".xsl</h2>");
    sb.append("</div>");
    sb.append("<div id=\"" + randId + "\" class=\"component_stats_wrapper\" >");
  }

  
  protected void appendDebug(StringBuffer sb, Component component, String randId, String contentType) {
    sb.append("</div>");
  }

  protected void appendControllers(StringBuffer sb, Component component) {
    if (component.getControllerNames().length > 0) {
      sb.append("<script type=\"text/javascript\">\n");
      for (int i=0; i < component.getControllerNames().length; i++) {
        if (component.getControllerNames()[i].length() > 0) {
          sb.append("Toobs.Controller.useComp('" + component.getControllerNames()[i] + "');\n");
        }
      }
      sb.append("</script>\n");
    }
  }
  
  /*public String inlineUrl(String transformUrl, Object inputNode) throws
    XMLTransformerException {
  
    try {
      StringBuffer sb = new StringBuffer(); 
      IRequest request = componentRequestManager.get();
      if (request == null) {
        throw new XMLTransformerException("Invalid request");
      }
      IXMLTransformer xmlTransformer = null;
      xmlTransformer = XMLTransformerFactory.getInstance().getDefaultTransformer(null);

      Map inParams = new HashMap(); //new HashMap(request.getParams());
      String transformPath = parseUrl("Transform:", transformUrl, request, inParams);

      List xslSources = new ArrayList();
      List inputXML = new ArrayList();
      List outputXML = new ArrayList();
      xslSources.add(transformPath);

      // Prepare XML
      if (inputNode != null && inputNode instanceof org.w3c.dom.traversal.NodeIterator) {
        org.w3c.dom.traversal.NodeIterator nodeIter = (org.w3c.dom.traversal.NodeIterator)inputNode;

        inputXML.add(nodeIter.getRoot());
        // This is the Translet case
      } else if (inputNode != null && inputNode instanceof org.apache.xml.dtm.ref.DTMAxisIterNodeList) {
        org.apache.xml.dtm.ref.DTMAxisIterNodeList nodeList = (org.apache.xml.dtm.ref.DTMAxisIterNodeList)inputNode;
        inputXML.add(nodeList.toString());
      } else {
        inputXML.add("<inline/>");
      }

      outputXML = xmlTransformer.transform(xslSources, inputXML, inParams);
      for (int ox = 0; ox < outputXML.size(); ox++) {
        sb.append((String) outputXML.get(ox));
      }
      // Return
      return sb.toString();
    } catch (Exception ex) {
      throw new XMLTransformerException(ex);
    }
  }*/
  
  /*public String transformEmail(String transformUrl, String emailContent, Object inputNode) throws
    XMLTransformerException {

  try {
    StringBuffer sb = new StringBuffer(); 
    IRequest request = componentRequestManager.get();
    if (request == null) {
      throw new XMLTransformerException("Invalid request");
    }
    IXMLTransformer xmlTransformer = null;
    xmlTransformer = XMLTransformerFactory.getInstance().getDefaultTransformer(null);

    HashMap inParams = new HashMap(); //new HashMap(request.getParams());
    String transformPath = parseUrl("Transform:", transformUrl, request, inParams);

    List xslSources = new ArrayList();
    List inputXML = new ArrayList();
    List outputXML = new ArrayList();
    xslSources.add(transformPath);

    StringBuffer xmlBuf = new StringBuffer();
    String unescapedHtml = StringEscapeUtils.unescapeHtml(emailContent);
    if (log.isDebugEnabled()) {
      log.debug("Template HTML: " + unescapedHtml);
    }
    xmlBuf.append("<emaildata>").append("<EmailContent>")
      .append(unescapedHtml)
      .append("</EmailContent>");
    
    // Prepare XML
    if (inputNode != null && inputNode instanceof org.w3c.dom.traversal.NodeIterator) {
      org.w3c.dom.traversal.NodeIterator nodeIter = (org.w3c.dom.traversal.NodeIterator)inputNode;

      //inputXML.add(nodeIter.getRoot());
      
      TransformerFactory tf=TransformerFactory.newInstance();
      //identity
      Transformer t=tf.newTransformer();
      t.setOutputProperty(OutputKeys.INDENT, "yes");
      t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      t.transform(new DOMSource( (org.w3c.dom.Node)nodeIter.getRoot() ), new StreamResult( os ));
      xmlBuf.append(os.toString("UTF-8"));

      // This is the Translet case
    } else if (inputNode != null && inputNode instanceof org.apache.xml.dtm.ref.DTMAxisIterNodeList) {
      //org.apache.xml.dtm.ref.DTMAxisIterNodeList nodeList = (org.apache.xml.dtm.ref.DTMAxisIterNodeList)inputNode;
      throw new XMLTransformerException("transformEmail does not support translets");
      //inputXML.add(nodeList.toString());
    } else {
      inputXML.add("<inline/>");
    }
    xmlBuf.append("</emaildata>");
    
    inputXML.add(xmlBuf.toString());
    outputXML = xmlTransformer.transform(xslSources, inputXML, inParams);
    for (int ox = 0; ox < outputXML.size(); ox++) {
      sb.append((String) outputXML.get(ox));
    }
    // Return
    return sb.toString();
  } catch (Exception ex) {
    throw new XMLTransformerException(ex);
  }
}*/

  /*
  public String getParam(String name, String defaultValue) throws XMLTransformerException {
    if (name == null) {
      throw new XMLTransformerException("getParam: name must not be null");
    }
    Object value = defaultValue;
    try {
      IRequest request = componentRequestManager.get();
      Map params = request.getParams();
      // Not overriding existing
      if (params.containsKey(name)) {
        value = params.get(name);
      }
    } catch (Exception ex) {
      throw new XMLTransformerException(ex);
    }
    return String.valueOf(value);
  }

  public boolean setParam(String name, String value) throws XMLTransformerException {
    return setParam(name, value, false);
  }
*/
  
  /*
  public boolean setParam(String name, String value, boolean overwrite) throws XMLTransformerException {
    if (name == null || value == null) {
      throw new XMLTransformerException("setParam: name and value must not be null");
    }
    try {
      IRequest request = componentRequestManager.get();
      Map params = request.getParams();
      // Not overriding existing
      if (overwrite || !params.containsKey(name)) {
        if (value.length() == 0) {
          params.remove(name);
          if (log.isDebugEnabled()) {
            log.debug("setParam - removed - name: " + name);
          }
        } else {
          params.put(name, value);
          if (log.isDebugEnabled()) {
            log.debug("setParam - name: " + name + " value: " + value);
          }
        }
      }
    } catch (Exception ex) {
      throw new XMLTransformerException(ex);
    }
    return true;
  }
*/
  
  /*
  public boolean setSessionParam(String name, String value) throws XMLTransformerException {
    if (name == null || value == null) {
      throw new XMLTransformerException("setSessionParam: name and value must not be null");
    }
    try {
      IRequest request = componentRequestManager.get();
      HttpSession session = request.getHttpRequest().getSession();
      session.setAttribute(name, value);
      Map params = request.getParams();
      params.put(name, value);
    } catch (Exception ex) {
      throw new XMLTransformerException(ex);
    }
    return true;
  }
  */

  /*public Object getNumberParam(String name, boolean asAverage) throws XMLTransformerException {
    if (name == null) {
      throw new XMLTransformerException("getNumberParam: name must not be null");
    }
    Double value = new Double(0);
    try {
      IRequest request = componentRequestManager.get();
      Map params = request.getParams();
      // Not overriding existing
      if (params.containsKey(name)) {
        value = (Double)params.get(name);
        if (asAverage) {
          Integer count = (Integer)params.get(name + ".count");
          value = value / (double)count.intValue();
        }
      }
    } catch (Exception ex) {
      throw new XMLTransformerException(ex);
    }
    return value;
  }
*/
  
  /*
  public boolean setNumberParam(String name, Object value, boolean trackAverage) throws XMLTransformerException {
    if (name == null || value == null) {
      throw new XMLTransformerException("setParam: name and value must not be null");
    }
    try {
      IRequest request = componentRequestManager.get();
      Map params = request.getParams();

      if (params.containsKey(name)) {
        Double curVal = (Double)params.get(name);
        curVal += (Double)value; 
        if (trackAverage) {
          Integer count = (Integer)params.get(name + ".count");
          count++;
          params.put(name + ".count", count);
        }
        params.put(name, curVal);
      } else {
        params.put(name + ".count", new Integer(1));
        params.put(name, value);
      }
    } catch (Exception ex) {
      throw new XMLTransformerException(ex);
    }
    return true;
  }*/

  /*
  public boolean resetNumberParam(String name) throws XMLTransformerException {
    if (name == null) {
      throw new XMLTransformerException("setParam: name and value must not be null");
    }
    try {
      IRequest request = componentRequestManager.get();
      Map params = request.getParams();

      if (params.containsKey(name)) {
        params.put(name, new Double(0));
        if (params.containsKey(name + ".count")) {
          params.put(name + ".count", new Integer(0));
        }
      }
    } catch (Exception ex) {
      throw new XMLTransformerException(ex);
    }
    return true;
  }*/

  protected String parseUrl(String context, 
      String componentUrl, 
      IRequest request, 
      Map inParams) throws Exception {
    if (componentUrl.indexOf("?") == -1) {
      return componentUrl.replace(componentExtension, "");
    }
    if (log.isDebugEnabled()) {
      log.debug("parseUrl: " + componentUrl);
    }
    String[] compUri = componentUrl.split("\\?");
    String componentId = compUri[0];
    String[] params = compUri[1].split("&");
    for (int i=0; i<params.length; i++) {
      String[] kvp = params[i].split("=");
      if (log.isDebugEnabled()) {
        log.debug("param: " + params[i]);
      }
      if (inParams.containsKey(kvp[0])) {
        String[] newVal;
        Object val = inParams.get(kvp[0]);
        if (val.getClass().isArray()) {
          String[] valAry = (String[])val;
          boolean put = true;
          for (int j = 0; j < valAry.length; j++) {
            if (kvp.length > 1 && valAry[j].equals(kvp[1])) {
              put = false;
              break;
            }
          }
          if (put) {
            newVal = new String[valAry.length+1];
            System.arraycopy(valAry, 0, newVal, 0, valAry.length);
            newVal[valAry.length] = kvp.length > 1 ? kvp[1] : "";
            inParams.put(kvp[0], newVal);
          }
        } else if (kvp.length > 1 && !val.equals(kvp[1])) {
          newVal = new String[2];
          newVal[0] = (String)val;
          newVal[1] = kvp[1];
          inParams.put(kvp[0], newVal);
        }
      } else {
        inParams.put(kvp[0], kvp.length > 1 ? kvp[1] : "");
      }
    }
    return componentId.replace(componentExtension, "");
  }
  
  protected Map getRequestParameters(String context, String scopeId, Map requestParams, Object parameters) throws Exception {
    Map outParams = new HashMap(requestParams);
    if (parameters != null) {
      ArrayList paramList = new ArrayList();
      // This is the static XSL case
      if (parameters instanceof org.w3c.dom.traversal.NodeIterator) {
        org.w3c.dom.traversal.NodeIterator nodeIter = (org.w3c.dom.traversal.NodeIterator)parameters;
        org.w3c.dom.Node currentNode = null;
        while ((currentNode = nodeIter.nextNode()) != null) {
          processNode(currentNode, paramList);
        }
      } else //...
      // This is the Translet case
      if (parameters instanceof org.apache.xml.dtm.ref.DTMAxisIterNodeList) {
        org.apache.xml.dtm.ref.DTMAxisIterNodeList nodeList = (org.apache.xml.dtm.ref.DTMAxisIterNodeList)parameters;
        int nodes = nodeList.getLength();
        for (int i = 0; i < nodes; i++) {
          processNode(nodeList.item(i), paramList);
        }
      } else //...
      // this is the nested toobs tags case
      if (parameters instanceof List) {
        List<Node> nodes = (List<Node>) parameters;
        for (Node node : nodes) {
          processNode(node, paramList);
        }
      }
      Parameter[] paramMap = new Parameter[paramList.size()];
      paramMap = (Parameter[])paramList.toArray(paramMap);
      ParameterUtil.mapParameters(context + scopeId, paramMap, requestParams, outParams, scopeId);
    }
    return outParams;
  }
  
  protected void processNode(org.w3c.dom.Node currentNode, ArrayList paramList) throws Exception {
    if (currentNode != null && currentNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
      org.w3c.dom.NamedNodeMap nodeMap = currentNode.getAttributes();
      Map nodeAttributes = new HashMap();
      for (int a = 0; a < nodeMap.getLength(); a++) {
        org.w3c.dom.Node attrNode = nodeMap.item(a);
        nodeAttributes.put(attrNode.getNodeName(), attrNode.getNodeValue());
      }
      Parameter param = new Parameter();
      BeanMonkey.populate(param, nodeAttributes, true);
      if (param.getPath() == null && param.getIsStatic()) param.setPath("");
      //log.info("Ref Param - Name: " + param.getName() + " Value: " + param.getPath());
      paramList.add(param);
    }
  }
  
  /*
  public static String getToken() throws XMLTransformerException {
    IRequest request = reqManager.get();
    if (request == null) {
      throw new XMLTransformerException("Invalid request");
    }
    return (String)request.getParams().get(PlatformConstants.REQUEST_GUID);
  }
  */
  
  /*
  public static String pageLinks(int pageSize, int firstResult, int totalRows, int pagesDisplayed) {
    StringBuffer sb = new StringBuffer();
    int currentPage=firstResult;
    if (currentPage >= (pageSize*pagesDisplayed)) {
      sb.append("<a class=\"pageLink\" href=\"#\" page=\"");
      sb.append("0");
      sb.append("\" title=\"Page 1\">1</a>...");
    }
    for (int i=0; i<pagesDisplayed; i++) {
      int pageNum = currentPage/pageSize;
      sb.append("<a class=\"pageLink\" href=\"#\" page=\"");
      sb.append(currentPage);
      sb.append("\" title=\"Page ");
      sb.append(pageNum+1);
      sb.append("\">");
      sb.append(pageNum+1);
      sb.append("</a>");
      currentPage += pageSize;
      if (currentPage > totalRows) break;
    }
    
    return sb.toString();
  }*/
}
