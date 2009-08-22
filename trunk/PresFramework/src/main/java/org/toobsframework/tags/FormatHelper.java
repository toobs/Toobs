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
package org.toobsframework.tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.transform.TransformerException;

import org.apache.xalan.extensions.XSLProcessorContext;
import org.apache.xalan.templates.ElemExtensionCall;
import org.apache.xalan.templates.ElemTextLiteral;
import org.apache.xalan.templates.ElemValueOf;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XRTreeFragSelectWrapper;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;

import org.toobsframework.pres.xsl.ComponentTransformerHelper;
import org.toobsframework.transformpipeline.domain.IXMLTransformer;
import org.toobsframework.util.IRequest;

import org.w3c.dom.Node;


public class FormatHelper extends TagBase {

  private static final String FORMAT_HELPER_PARAMETERS = "formatHelperParameters";

  /**
   * Public Tag - Message - insert a resource bundle message in the result stream
   * <p>
   * <pre><code>
   *   &lt;fmt:message key="<i>key</i>"&gt;
   *   &lt;/fmt:message>
   * </code></pre>
   * 
   * implicit DTD for message
   * 
   * <pre><code>
   * &lt;!ELEMENT fmt:message (fmt:param*)&gt
   * &lt;!ATTLIST fmt:message
   * key CDATA #REQUIRED
   * </code></pre>
   * 
   * Where
   * <p>
   * <ul>
   * <li>key - is the resource bundle message key
   * </ul>
   */
  @SuppressWarnings("unchecked")
  public void message(XSLProcessorContext processorContext, ElemExtensionCall extensionElement) throws TransformerException {

    // Initialize
    TransformerImpl transformer = processorContext.getTransformer();
    ComponentTransformerHelper transformerHelper = getTransformerHelper(processorContext);


    // Get attributes
    String messageKey = getRequiredStringProperty("key", "the property key needs to be provided for the message tag", processorContext, extensionElement);

    // Obtain parameters
    List<String> parameterList = new ArrayList<String>();
    Object oldParameterList = transformer.getParameter(FORMAT_HELPER_PARAMETERS);
    if (extensionElement.hasChildNodes()) {
      transformer.setParameter(FORMAT_HELPER_PARAMETERS, parameterList);
      transformer.executeChildTemplates(extensionElement, true);
      transformer.setParameter(FORMAT_HELPER_PARAMETERS, oldParameterList == null ? new Boolean(false) : oldParameterList);
    }

    // Compute Results
    IRequest request = getComponentRequest(processorContext);
    if (request == null) {
      throw new TransformerException("Internal error: Invalid request passed to the layout through the " + IXMLTransformer.TRANSFORMER_HELPER);
    }
    try {
      LocaleResolver localeResolver = (LocaleResolver)request.getHttpRequest().getAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE);
      Locale locale = localeResolver.resolveLocale(request.getHttpRequest());

      String message = transformerHelper.getApplicationContext().getMessage(messageKey, parameterList.toArray(), locale);

      if (oldParameterList == null) {
        serialize(processorContext, extensionElement, message, false);
      } else if (oldParameterList instanceof List) {
        ((List<String>)oldParameterList).add(message);
      }
    } catch (Exception ex) {
      throw new TransformerException("Error getting message for key=" + messageKey + ": " + ex.getMessage(), ex);
    }
  }

  /**
   * Public Tag - Parameter - pass a parameter to a message
   * <p>
   * <pre><code>
   *   &lt;fmt:parameter value="<i>string literal|variable value</i>" /&gt;
   *   &lt;fmt:parameter&gt;<i>string literal</i>&lt;/param&gt;
   *   &lt;fmt:parameter&gt;<i>value-of expression</i>&lt;/param&gt;
   *   &lt;fmt:parameter&gt;<i>nested fmt:message</i>&lt;/param&gt;
   * </code></pre>
   * 
   * implicit DTD for parameter
   * 
   * <pre><code>
   * &lt;!ELEMENT toobs:parameter (PCDATA)?>
   * &lt;!ATTLIST toobs:parameter
   * value CDATA #IMPLIED&gt;
   * </code></pre>
   * 
   * Where
   * <p>
   * <ul>
   * <li>value - a string literal or xsl variable
   * </ul>
   */
  @SuppressWarnings("unchecked")
  public void parameter(XSLProcessorContext processorContext, ElemExtensionCall extensionElement) throws TransformerException {
    TransformerImpl transformer = processorContext.getTransformer();
    Object p = transformer.getParameter(FORMAT_HELPER_PARAMETERS);

    if (p == null || !(p instanceof List)) {
      throw new TransformerException("fmt parameter declarartion needs to be nested inside of a fmt message");
    }

    // Get attributes
    String value = getStringProperty("value", null, processorContext, extensionElement);
    if (value == null && extensionElement.hasChildNodes()) {
      Node firstChild = extensionElement.getFirstChild();
      if (firstChild instanceof ElemTextLiteral) {
        value = ((ElemTextLiteral)firstChild).getNodeValue();
      } else if (firstChild instanceof ElemValueOf) {
        XPathContext xctxt = transformer.getXPathContext();
        XPath xPath = new XPath(new XRTreeFragSelectWrapper(((ElemValueOf)firstChild).getSelect().getExpression()));
        XObject var = xPath.execute(xctxt, processorContext.getContextNode(), extensionElement );
        value = var.str();
      } else if (firstChild instanceof ElemExtensionCall) {
        transformer.executeChildTemplates(extensionElement, true);
      }
    }
    if (value != null) {
      ((List<String>)p).add(value);
    }
    // this method does not execute any child templates.  No xslt instructions can be nested under it
  }
}
