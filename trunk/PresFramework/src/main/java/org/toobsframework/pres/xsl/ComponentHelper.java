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
package org.toobsframework.pres.xsl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.apache.xalan.extensions.XSLProcessorContext;
import org.apache.xalan.templates.ElemExtensionCall;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.serializer.SerializationHandler;
import org.toobsframework.data.beanutil.BeanMonkey;
import org.toobsframework.pres.component.Component;
import org.toobsframework.pres.component.ParallelComponent;
import org.toobsframework.pres.component.Transform;
import org.toobsframework.pres.component.config.Parameter;
import org.toobsframework.pres.util.IComponentRequest;
import org.toobsframework.pres.util.ParameterUtil;
import org.toobsframework.pres.util.PresConstants;
import org.toobsframework.tags.TagBase;
import org.toobsframework.transformpipeline.domain.IXMLTransformer;
import org.toobsframework.transformpipeline.domain.XMLTransformerException;
import org.toobsframework.util.IRequest;
import org.w3c.dom.Node;


@SuppressWarnings("unchecked")
public class ComponentHelper extends TagBase {

  private static final String COMPONENT_HELPER_PARAMETERS = "componentHelperParameters";

  public ComponentHelper() {
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
  public void component(XSLProcessorContext processorContext, ElemExtensionCall extensionElement) throws TransformerException {
    
    // initialize
    TransformerImpl transformer = processorContext.getTransformer();
    ComponentTransformerHelper transformerHelper = getTransformerHelper(processorContext);
    
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
    IComponentRequest request = getComponentRequest(processorContext);
    if (request == null) {
      throw new TransformerException("Internal error: Invalid request passed to the layout through the " + IXMLTransformer.TRANSFORMER_HELPER);
    }

    try {
  
      if(loader.equalsIgnoreCase("direct")) {
        ParallelComponent pc = null;
        if ( (pc = request.getParallelComponent(componentId)) !=null ) {
          appendStyle(sb, pc.getComponent());
          sb.append(new String(pc.getOutput().toByteArray()));
          appendControllers(sb, pc.getComponent());
        } else {
          Map<String, Object> inParams = getRequestParameters(request, "Component:", componentId, request.getParams(), parameterList);
          Component component = transformerHelper.getComponentManager().getComponent(ParameterUtil.resolveParam(request, componentId, inParams)[0]);
          appendStyle(sb, component);
          String randId = componentId + "_"+ randomGenerator.nextInt();
          if(transformerHelper.getConfiguration().isDebug() && !component.getId().equalsIgnoreCase("componentFrame")) {
            prependDebug(sb, component, randId, contentType);
          }
          sb.append(transformerHelper.getComponentManager().renderComponent(request, component, contentType, inParams, request.getParams(), transformerHelper, false));
          if(transformerHelper.getConfiguration().isDebug() && !component.getId().equalsIgnoreCase("componentFrame")) {
            appendDebug(sb, component, randId, contentType);
          }
          appendControllers(sb, component);
        }
      } else if (loader.equalsIgnoreCase("lazy")) {
        Map inParams = getRequestParameters(request, "Component:", componentId, new HashMap(), parameterList);
        appendLazyAJAXCall(transformerHelper, sb, componentId, inParams);
      }
      serialize(processorContext, extensionElement, sb.toString(), false);
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
  public void parameter(XSLProcessorContext processorContext, ElemExtensionCall extensionElement) throws TransformerException {
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
  public void layout(XSLProcessorContext processorContext, ElemExtensionCall extensionElement) throws TransformerException {
    
    // Initialize
    TransformerImpl transformer = processorContext.getTransformer();
    ComponentTransformerHelper transformerHelper = getTransformerHelper(processorContext);
    
    // Obtain Tag Attributes
    String layoutId = getRequiredStringProperty("layoutId", "the tag layout needs the attribute layoutId", processorContext, extensionElement);
  
    // Obtain parameters
    List parameterList = new ArrayList<Node>();
    transformer.setParameter(COMPONENT_HELPER_PARAMETERS, parameterList);
    transformer.executeChildTemplates(extensionElement, true);
    transformer.setParameter(COMPONENT_HELPER_PARAMETERS, new Boolean(false));

    // Compute Results
    IComponentRequest request = getComponentRequest(processorContext);
    if (request == null) {
      throw new TransformerException("Internal error: Invalid request passed to the layout through the " + IXMLTransformer.TRANSFORMER_HELPER);
    }

    try {
      request.setParams(getRequestParameters(request, "Layout:", layoutId, request.getParams(), parameterList));
      String s = transformerHelper.getComponentLayoutManager().getLayout(ParameterUtil.resolveParam(request, layoutId, request.getParams())[0]).render(request, transformerHelper);
      serialize(processorContext, extensionElement, s, false);
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
  public void componentUrl(XSLProcessorContext processorContext, ElemExtensionCall extensionElement) throws TransformerException {

    // Initialize
    TransformerImpl transformer = processorContext.getTransformer();
    ComponentTransformerHelper transformerHelper = getTransformerHelper(processorContext);
    
    // Get attributes
    String componentUrl = getRequiredStringProperty("url", "componentUrl tag requires a url attribute", processorContext, extensionElement);
    String contentType = getStringProperty("contentType", "xhtml", processorContext, extensionElement);
    
    // Compute Results
    IComponentRequest request = getComponentRequest(processorContext);
    if (request == null) {
      throw new TransformerException("Internal error: Invalid request passed to the layout through the " + IXMLTransformer.TRANSFORMER_HELPER);
    }
    try {
      StringBuffer sb = new StringBuffer(); 
      Map inParams = new HashMap(request.getParams());
      String componentId = parseUrl(transformerHelper, "Component:", componentUrl, request, inParams);
      if (componentId.indexOf(transformerHelper.getConfiguration().getLayoutExtension()) != -1) {
        sb.append(transformerHelper.getComponentLayoutManager().getLayout(ParameterUtil.resolveParam(request, componentId.replace(transformerHelper.getConfiguration().getLayoutExtension(), ""), request.getParams())[0]).render(request, transformerHelper));
      } else {
        Component component = transformerHelper.getComponentManager().getComponent(ParameterUtil.resolveParam(request, componentId, inParams)[0]);
        sb.append(transformerHelper.getComponentManager().renderComponent(request, component, contentType, inParams, request.getParams(), transformerHelper, false));
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
  public void insert(XSLProcessorContext processorContext, ElemExtensionCall extensionElement) throws TransformerException {

    // Initialize
    TransformerImpl transformer = processorContext.getTransformer();
    ComponentTransformerHelper transformerHelper = getTransformerHelper(processorContext);
    
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
    IRequest request = getComponentRequest(processorContext);
    if (request == null) {
      throw new TransformerException("Internal error: Invalid request passed to the layout through the " + IXMLTransformer.TRANSFORMER_HELPER);
    }
    try {
      

      StringBuffer sb = new StringBuffer(); 
      Map<String, Object> inParams = getRequestParameters(request, "Insert:", action, request.getParams(), parameterList);
      Map outParams = new HashMap();
      Object result;
      if (isExtended) {
        result = transformerHelper.getDataProvider().dispatchActionEx(request, action, serviceProvider, "", "", guidParam, permissionContext, "", namespace, inParams, outParams);
      } else {
        result = transformerHelper.getDataProvider().dispatchAction(action, serviceProvider, "", "", guidParam, permissionContext, "", namespace, inParams, outParams);
      }
      sb.append(result);
      serialize(processorContext, extensionElement, sb.toString(), false);
    } catch (Exception ex) {
      throw new TransformerException("Error inserting action=" + action + ": " + ex.getMessage(), ex);
    }
  }

  public void displayExceptionMessage(XSLProcessorContext processorContext, ElemExtensionCall extensionElement) throws TransformerException {

    // Initialize
    IRequest componentRequest = getComponentRequest(processorContext);

    StringBuilder sb = new StringBuilder();
    sb.append("<pre>\n");
    try {
      if (componentRequest == null) {
        throw new TransformerException("Internal error: Invalid request passed to the layout through the " + IXMLTransformer.TRANSFORMER_HELPER);
      }
      Object exObj = componentRequest.getParam(PresConstants.TOOBS_EXCEPTION_ATTR_NAME);
      if (exObj != null && exObj instanceof Throwable ) {
        Throwable t = (Throwable) exObj;
        sb.append(t.getMessage());
      }
    } catch (Exception e) {
      sb.append("Unable to display exception message caused by: ").append(e.getMessage());
    }
    sb.append("\n</pre>");
    try {
      serialize(processorContext, extensionElement, sb.toString(), false);
    } catch (Exception e) {
      throw new TransformerException(e);
    }
  }

  public void displayExceptionStack(XSLProcessorContext processorContext, ElemExtensionCall extensionElement) throws TransformerException {

    // Initialize
    ComponentTransformerHelper transformerHelper = getTransformerHelper(processorContext);
    IRequest componentRequest = getComponentRequest(processorContext);

    StringWriter sb = new StringWriter();
    sb.append("<pre>\n");
    try {
      if (componentRequest == null) {
        throw new TransformerException("Internal error: Invalid request passed to the layout through the " + IXMLTransformer.TRANSFORMER_HELPER);
      }
      Object exObj = componentRequest.getParam(PresConstants.TOOBS_EXCEPTION_ATTR_NAME);
      if (exObj != null && exObj instanceof Throwable ) {
        Throwable t = (Throwable) exObj;
        sb.append("Exception message: ").append(t.getMessage()).append('\n');
        if (transformerHelper.getConfiguration().showStackTrace()) {
          PrintWriter pw = new PrintWriter(sb); 
          t.printStackTrace(pw);
        }
      }
    } catch (Exception e) {
      sb.append("Unable to display exception caused by: ").append(e.getMessage()).append('\n');
      if (transformerHelper.getConfiguration().showStackTrace()) {
        PrintWriter pw = new PrintWriter(sb); 
        e.printStackTrace(pw);
      }
    }
    sb.append("\n</pre>");
    try {
      serialize(processorContext, extensionElement, sb.toString(), false);
    } catch (Exception e) {
      throw new TransformerException(e);
    }
  }

  /*
  public String getUserAgent(ComponentTransformerHelper transformerHelper) throws XMLTransformerException {
    try {
      IRequest request = getComponentRequest(processorContext);
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

  protected void appendLazyAJAXCall(ComponentTransformerHelper transformerHelper, StringBuffer sb, String componentId, Map parameters) {
    Random randomGenerator = new Random();
    //Create container id
    String container = componentId + "_"+ randomGenerator.nextInt();
    //Create url
    StringBuffer url = new StringBuffer();
    url.append(componentId + transformerHelper.getConfiguration().getLayoutExtension() + "?");
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

  protected String parseUrl(ComponentTransformerHelper transformerHelper, String context, 
      String componentUrl, 
      IRequest request, 
      Map inParams) throws Exception {
    if (componentUrl.indexOf("?") == -1) {
      return componentUrl.replace(transformerHelper.getConfiguration().getComponentExtension(), "");
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
    return componentId.replace(transformerHelper.getConfiguration().getComponentExtension(), "");
  }
  
  protected Map getRequestParameters(IRequest request, String context, String scopeId, Map requestParams, Object parameters) throws Exception {
    Map outParams = new HashMap(requestParams);
    if (parameters != null) {
      ArrayList paramList = new ArrayList();
      org.w3c.dom.Node currentNode = null;
      // This is the static XSL case
      if (parameters instanceof org.w3c.dom.traversal.NodeIterator) {
        org.w3c.dom.traversal.NodeIterator nodeIter = (org.w3c.dom.traversal.NodeIterator)parameters;
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
      ParameterUtil.mapParameters(request, context + scopeId, paramMap, requestParams, outParams, scopeId);
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