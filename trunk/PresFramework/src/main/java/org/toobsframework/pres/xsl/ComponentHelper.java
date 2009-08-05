package org.toobsframework.pres.xsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    String componentId = getRequiredStringProperty("componentId", "component tag requires a componentId attribute", processorContext, extensionElement);
    String contentType = getStringProperty("contentType", "xhtml", processorContext, extensionElement);
    String loader = getStringProperty("loader", "direct", processorContext, extensionElement);
    
    // Obtain parameters
    List parameterList = new ArrayList();
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
        Map<String, Object> inParams = getRequestParameters("Component:", componentId, request.getParams(), parameterList, request.getHttpRequest(), request.getHttpResponse());
        Component component = transformerHelper.getComponentManager().getComponent(ParameterUtil.resolveParam(componentId, inParams, request.getHttpRequest(), request.getHttpResponse())[0], getDeployTime(request));
        appendStyle(sb, component);
        String randId = componentId + "_"+ randomGenerator.nextInt();
        if(debugComponents && !component.getId().equalsIgnoreCase("componentFrame")) {
          prependDebug(sb, component, randId, contentType);
        }
        sb.append(transformerHelper.getComponentManager().renderComponent(component, contentType, inParams, request.getParams(), transformerHelper, request.getHttpRequest(), request.getHttpResponse(), false));
        if(debugComponents && !component.getId().equalsIgnoreCase("componentFrame")) {
          appendDebug(sb, component, randId, contentType);
        }
        appendControllers(sb, component);
        
      } else if (loader.equalsIgnoreCase("lazy")) {
        Map inParams = getRequestParameters("Component:", componentId, new HashMap(), parameterList, request.getHttpRequest(), request.getHttpResponse());
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
    List parameterList = (List) p;

    String useContext = getStringProperty("use-context", processorContext, extensionElement);
    if (useContext != null && (useContext.equalsIgnoreCase("true") || useContext.equalsIgnoreCase("yes") || useContext.equalsIgnoreCase("1"))) {
      // in this case, use the context node to copy the parameter to the component call
      parameterList.add(processorContext.getContextNode());
    } else {
      String name = getRequiredStringProperty("name", "the property name needs to be provided for the property tag", processorContext, extensionElement);
      String path = getRequiredStringProperty("path", "the property path needs to be provided for the property tag '" + name + "'", processorContext, extensionElement);
      String condition = getStringProperty("condition", processorContext, extensionElement);
      String _default = getStringProperty("default", processorContext, extensionElement);
      String jexlExpression = getStringProperty("jexlExpression", processorContext, extensionElement);
      String jexlScript = getStringProperty("jexlScript", processorContext, extensionElement);
      String scope = getStringProperty("scope", processorContext, extensionElement);
      String sessionPath = getStringProperty("sessionPath", processorContext, extensionElement);
      boolean isStatic = getBooleanProperty("isStatic", false, processorContext, extensionElement);
      boolean ignoreNull = getBooleanProperty("ignoreNull", false, processorContext, extensionElement);
      boolean isList = getBooleanProperty("isList", false, processorContext, extensionElement);
      boolean isObject = getBooleanProperty("isObject", false, processorContext, extensionElement);
      boolean overwriteExisting = getBooleanProperty("overwriteExisting", true, processorContext, extensionElement);
      int objectIndex = getIntegerProperty("objectIndex", 0, processorContext, extensionElement);
      
      Parameter param = new Parameter();
      param.setName(name);
      param.setPath(path);
      param.setIsStatic(isStatic);
      param.setCondition(condition);
      param.setDefault(_default);
      param.setIgnoreNull(ignoreNull);
      param.setIsList(isList);
      param.setIsObject(isObject);
      param.setJexlExpression(jexlExpression);
      param.setJexlScript(jexlScript);
      param.setObjectIndex(objectIndex);
      param.setOverwriteExisting(overwriteExisting);
      param.setScope(scope);
      param.setSessionPath(sessionPath);
      parameterList.add(param);
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
    String layoutId = getRequiredStringProperty("layoutId", "the tag layout needs the attribute layoutId", processorContext, extensionElement);
  
    // Obtain parameters
    List parameterList = new ArrayList<Node>();
    transformer.setParameter(COMPONENT_HELPER_PARAMETERS, parameterList);
    transformer.executeChildTemplates(extensionElement, true);
    transformer.setParameter(COMPONENT_HELPER_PARAMETERS, new Boolean(false));

    // Compute Results
    IRequest request = transformerHelper.getComponentRequestManager().get();
    if (request == null) {
      throw new TransformerException("Internal error: Invalid request passed to the layout throught the " + IXMLTransformer.TRANSFORMER_HELPER);
    }

    try {
      request.setParams(getRequestParameters("Layout:", layoutId, request.getParams(), parameterList, request.getHttpRequest(), request.getHttpResponse()));      
      String s = transformerHelper.getComponentLayoutManager().getLayout(ParameterUtil.resolveParam(layoutId, request.getParams(), request.getHttpRequest(), request.getHttpResponse())[0], getDeployTime(request)).render(request, transformerHelper);
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
   *   &lt;toobs:componentUrl url="<i>url</i>" contentType="<i>type</i>" /&gt;
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
    String componentUrl = getRequiredStringProperty("url", "componentUrl tag requires a url attribute", processorContext, extensionElement);
    String contentType = getStringProperty("contentType", "xhtml", processorContext, extensionElement);
    
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
        sb.append(transformerHelper.getComponentLayoutManager().getLayout(ParameterUtil.resolveParam(componentId.replace(layoutExtension, ""), request.getParams(), request.getHttpRequest(), request.getHttpResponse())[0], getDeployTime(request)).render(request, transformerHelper));
      } else {
        Component component = transformerHelper.getComponentManager().getComponent(ParameterUtil.resolveParam(componentId, inParams, request.getHttpRequest(), request.getHttpResponse())[0], getDeployTime(request));
        sb.append(transformerHelper.getComponentManager().renderComponent(component, contentType, inParams, request.getParams(), transformerHelper, request.getHttpRequest(), request.getHttpResponse(), false));
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
   
 

  /**
   * Public Tag - insert - insert a component or sub-layout by URL the result stream
   * <p>
   * <pre><code>
   *   &lt;toobs:insert serviceProvider="<i>provider</i>" action="<i>action</i>" guidParam="<i>param-name</i>" permissionContext="<i>context</i>" namespace="<i>namespace</i>" /&gt;
   * </code></pre>
   * 
   * implicit DTD for insert
   * 
   * <pre><code>
   * &lt;!ELEMENT toobs:insert (toobs:parameter*)?&gt
   * &lt;!ATTLIST toobs:insert
   * serviceProvider CDATA #REQUIRED
   * action CDATA #REQUIRED
   * guidParam CDATA #IMPLIED&gt;
   * permissionContext CDATA #IMPLIED&gt;
   * namespace CDATA #IMPLIED&gt;
   * extended CDATA #IMPLIED&gt;
   * </code></pre>
   * 
   * Where
   * <p>
   * <ul>
   * <li>serviceProvider is the dataProvider for the information - for camel (the default) it is the camel bean name
   * <li>action is the action being taken - for camel (the default) is the camel starting route marker (direct: name)
   * <li>guidParam is the name of the param that contains the guid if there is such
   * <li>permissionCntext is the context setting for the permssioning.  The meaning of this string is left to the business bean implementation
   * <li>namespace is the context of the application, usually left to the business implementation for deambiguation.
   * </ul>
   */
  public void insert(org.apache.xalan.extensions.XSLProcessorContext processorContext, 
      org.apache.xalan.templates.ElemExtensionCall extensionElement) throws TransformerException {

    // Initialize
    TransformerImpl transformer = processorContext.getTransformer();
    Object th = transformer.getParameter(IXMLTransformer.TRANSFORMER_HELPER);
    if (th == null || !(th instanceof ComponentTransformerHelper)) {
      throw new TransformerException("Internal error: the property " + IXMLTransformer.TRANSFORMER_HELPER + " needs to be properly initialized prior to calling the transformation.");
    }
    ComponentTransformerHelper transformerHelper = (ComponentTransformerHelper) th;
    
    // Get attributes
    String serviceProvider = getRequiredStringProperty("serviceProvider", "the property servicePorovider needs to be provided for the insert tag", processorContext, extensionElement);
    String action = getRequiredStringProperty("action", "the property action needs to be provided for the insert tag", processorContext, extensionElement);
    String guidParam = getStringProperty("guidParam", "", processorContext, extensionElement);
    String permissionContext = getStringProperty("permissionContext", "", processorContext, extensionElement);
    String namespace = getStringProperty("namespace", "", processorContext, extensionElement);
    boolean isExtended = getBooleanProperty("extended", false, processorContext, extensionElement);
    

    // Obtain parameters
    List parameterList = new ArrayList();
    transformer.setParameter(COMPONENT_HELPER_PARAMETERS, parameterList);
    transformer.executeChildTemplates(extensionElement, true);
    transformer.setParameter(COMPONENT_HELPER_PARAMETERS, new Boolean(false));

    // Compute Results
    IRequest request = transformerHelper.getComponentRequestManager().get();
    if (request == null) {
      throw new TransformerException("Internal error: Invalid request passed to the layout throught the " + IXMLTransformer.TRANSFORMER_HELPER);
    }
    try {
      

      StringBuffer sb = new StringBuffer(); 
      Map<String, Object> inParams = getRequestParameters("Insert:", action, request.getParams(), parameterList, request.getHttpRequest(), request.getHttpResponse());
      Map outParams = new HashMap();
      Object result;
      if (isExtended) {
        result = transformerHelper.getDataProvider().dispatchActionEx(request.getHttpRequest(), request.getHttpResponse(), action, serviceProvider, "", "", guidParam, permissionContext, "", namespace, inParams, outParams);
      } else {
        result = transformerHelper.getDataProvider().dispatchAction(action, serviceProvider, "", "", guidParam, permissionContext, "", namespace, inParams, outParams);
      }
      sb.append(result);

      SerializationHandler handler = transformer.getResultTreeHandler();
      boolean previousEscaping = handler.setEscaping(false);
      processorContext.outputToResultTree(extensionElement.getStylesheet(), sb.toString());
      handler.setEscaping(previousEscaping);
    } catch (Exception ex) {
      throw new TransformerException("Error inserting action=" + action + ": " + ex.getMessage(), ex);
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
  
  protected Map getRequestParameters(String context, String scopeId, Map requestParams, Object parameters, HttpServletRequest request, HttpServletResponse response) throws Exception {
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
        List ps = (List) parameters;
        for (Object o : ps) {
          if (o instanceof Node) {
            processNode((Node) o, paramList);
          } else if (o instanceof Parameter) {
            paramList.add(o);
          }
        }
      }
      Parameter[] paramMap = new Parameter[paramList.size()];
      paramMap = (Parameter[])paramList.toArray(paramMap);
      ParameterUtil.mapParameters(context + scopeId, paramMap, requestParams, outParams, scopeId, request, response);
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
  /**
   * Get a property for a tag from the context passed
   * @param name is the name of the attribute
   * @param processorContext passed to the tag
   * @param extensionElement passed to the tag
   * @return the value or null if the tag is not there
   * @throws TransformerException on error
   */
  private String getStringProperty(String name, org.apache.xalan.extensions.XSLProcessorContext processorContext, org.apache.xalan.templates.ElemExtensionCall extensionElement) throws TransformerException {
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
  private String getStringProperty(String name, String defaultValue, org.apache.xalan.extensions.XSLProcessorContext processorContext, org.apache.xalan.templates.ElemExtensionCall extensionElement) throws TransformerException {
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
  private String getRequiredStringProperty(String name, String message, org.apache.xalan.extensions.XSLProcessorContext processorContext, org.apache.xalan.templates.ElemExtensionCall extensionElement) throws TransformerException {
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
  @SuppressWarnings("unused")
  private boolean getBooleanProperty(String name, org.apache.xalan.extensions.XSLProcessorContext processorContext, org.apache.xalan.templates.ElemExtensionCall extensionElement) throws TransformerException {
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
  private boolean getBooleanProperty(String name, boolean defaultValue, org.apache.xalan.extensions.XSLProcessorContext processorContext, org.apache.xalan.templates.ElemExtensionCall extensionElement) throws TransformerException {
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
  @SuppressWarnings("unused")
  private boolean getRequiredBooleanProperty(String name, String message, org.apache.xalan.extensions.XSLProcessorContext processorContext, org.apache.xalan.templates.ElemExtensionCall extensionElement) throws TransformerException {
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
  @SuppressWarnings("unused")
  private int getIntegerProperty(String name, org.apache.xalan.extensions.XSLProcessorContext processorContext, org.apache.xalan.templates.ElemExtensionCall extensionElement) throws TransformerException {
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
  private int getIntegerProperty(String name, int defaultValue, org.apache.xalan.extensions.XSLProcessorContext processorContext, org.apache.xalan.templates.ElemExtensionCall extensionElement) throws TransformerException {
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
  @SuppressWarnings("unused")
  private int getRequiredIntegerProperty(String name, String message, org.apache.xalan.extensions.XSLProcessorContext processorContext, org.apache.xalan.templates.ElemExtensionCall extensionElement) throws TransformerException {
    String value = getStringProperty(name, processorContext, extensionElement);
    if (value == null || value.length() == 0) {
      throw new TransformerException(message);
    }
    value = value.trim();
    return Integer.parseInt(value);
  }
  
}