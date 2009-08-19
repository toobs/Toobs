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
package org.toobsframework.transformpipeline.xslExtentions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobsframework.transformpipeline.domain.XMLTransformerException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



/**
 * String Util for XSL Transforms
 */
public class URLHelper {
  private static Log log = LogFactory.getLog(URLHelper.class);

  private static Node findNodeByName(NodeList nodeList, String name) {
    Node ret = null;
    for(int i = 0; i < nodeList.getLength(); i++) {
      Node thisNode = (Node) nodeList.item(i);
      String nodeName = thisNode.getNodeName();
      String localName = thisNode.getLocalName();
      if(thisNode.getLocalName().equals(name)){
        ret = thisNode;
        break;
      }
    }
    return ret;
  }
  
  private  static String findNodeValueByName(NodeList nodeList, String name) {
    Node thisNode = findNodeByName(nodeList, name);
    String content = thisNode.getTextContent();
    if(content == null) {
      content = thisNode.getFirstChild().getNodeValue();      
    }
    return content;
  }

}
