package org.toobsframework.transformpipeline.domain;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemTextLiteral;
import org.apache.xalan.trace.EndSelectionEvent;
import org.apache.xalan.trace.ExtensionEvent;
import org.apache.xalan.trace.GenerateEvent;
import org.apache.xalan.trace.SelectionEvent;
import org.apache.xalan.trace.TraceListenerEx3;
import org.apache.xalan.trace.TracerEvent;
import org.w3c.dom.Node;

public class TraceListenerImpl implements TraceListenerEx3 {

  private static Log log = LogFactory.getLog(TraceListenerImpl.class);
  /**
   * This needs to be set to true if the listener is to print an event whenever a template is invoked.
   */
  public boolean m_traceTemplates = true;

  /**
   * Set to true if the listener is to print events that occur as each node is 'executed' in the stylesheet.
   */
  public boolean m_traceElements = true;

  /**
   * Set to true if the listener is to print information after each result-tree generation event.
   */
  public boolean m_traceGeneration = true;

  /**
   * Set to true if the listener is to print information after each selection event.
   */
  public boolean m_traceSelection = true;

  /**
   * Set to true if the listener is to print information after each extension event.
   */
  public boolean m_traceExtension = false;
  private Map<String,Long> extMap;
  private Map<Node,Long> selMap;
  private Map<String,LinkedList<Long>> trcMap;
  private Map<String,Long> timeMap;

  public TraceListenerImpl() {
    extMap = new ConcurrentHashMap<String,Long>();
    selMap = new ConcurrentHashMap<Node,Long>();
    trcMap = new ConcurrentHashMap<String,LinkedList<Long>>();
    timeMap = new ConcurrentHashMap<String,Long>();
  }

  @SuppressWarnings("unchecked")
  public void extension(ExtensionEvent ee) {
    //log.info("extension");
    long time = System.currentTimeMillis();
    try {
      if (ee.m_method instanceof Method) {
        extMap.put(((Method)ee.m_method).getName(), time);
      }
      if (ee.m_method instanceof Constructor) {
        extMap.put(((Constructor)ee.m_method).getName(), time);
      }
      if (ee.m_method instanceof Class) {
        extMap.put(((Class)ee.m_method).getName(), time);
      }
    } catch (NullPointerException e) {
      log.error("null", e);
    }
  }

  @SuppressWarnings("unchecked")
  public void extensionEnd(ExtensionEvent ee) {
    //log.info("extensionEnd");
    long time = System.currentTimeMillis();
    try {
      if (ee.m_method instanceof Method) {
        String name = ((Method)ee.m_method).getName();
        Long start = extMap.remove(name);
        if (start != null) {
          Long cur = timeMap.get(name + "-ext-time");
          if (cur == null) cur = 0L;
          timeMap.put(name + "-ext-time", (cur + (time - start)));
        }
      }
      if (ee.m_method instanceof Constructor) {
        String name = ((Constructor)ee.m_method).getName();
        Long start = extMap.remove(name);
        if (start != null) {
          Long cur = timeMap.get(name + "-ext-time");
          if (cur == null) cur = 0L;
          timeMap.put(name + "-ext-time", (cur + (time - start)));
        }
      }
      if (ee.m_method instanceof Class) {
        String name = ((Class)ee.m_method).getName();
        Long start = extMap.remove(name);
        if (start != null) {
          Long cur = timeMap.get(name + "-ext-time");
          if (cur == null) cur = 0L;
          timeMap.put(name + "-ext-time", (cur + (time - start)));
        }
      }
    } catch (NullPointerException e) {
      log.error("null", e);
    }
  }

  public void trace(TracerEvent ev) {
    long time = System.currentTimeMillis();
    String key = _traceKey(ev);
    LinkedList<Long> q = null;
    if ( (q = trcMap.get(key)) == null) {
      q = new LinkedList<Long>();
      trcMap.put(key, q);
    }
    q.add(time);
    /*
    this._trace("#TRACESTART# ", ev);
    ev.m_sourceNode.isSameNode(null);
    trcMap.put(ev.m_sourceNode, System.currentTimeMillis());
    if (ev != null && ev.m_sourceNode != null) {
      log.info("trace " + ev.m_sourceNode.getLocalName());
    }
    */
  }

  public void traceEnd(TracerEvent ev) {
    long time = System.currentTimeMillis();
    String key = _traceKey(ev);
    LinkedList<Long> q = trcMap.get(key);
    Long start = q.getLast();
    if (start != null) {
      Long cur = timeMap.get(key + "-trace-time");
      if (cur == null) cur = 0L;
      timeMap.put(key + "-trace-time", (cur + (time - start)));
    }

    /*
    this._trace("#TRACEEND# ", ev);
    long time = System.currentTimeMillis();
    Long start = trcMap.remove(ev.m_sourceNode);
    if (start != null) {
      Long cur = timeMap.get("trace-time");
      if (cur == null) cur = 0L;
      timeMap.put("trace-time", (cur + (time - start)));
    }
    if (ev != null && ev.m_sourceNode != null) {
      log.info("traceEnd " + ev.m_sourceNode.getLocalName());
    }
    */
  }

  public void selected(SelectionEvent ev) throws TransformerException {
    /*
    selMap.put(ev.m_sourceNode, System.currentTimeMillis());
    if (ev != null && ev.m_attributeName != null && ev.m_selection != null) {
      log.info("selected " + ev.m_attributeName + " " + ev.m_selection.toString());
    }
    */
  }

  public void selectEnd(EndSelectionEvent ev) throws TransformerException {
    /*
    long time = System.currentTimeMillis();
    Long start = selMap.remove(ev.m_sourceNode);
    if (start != null) {
      Long cur = timeMap.get("select-time");
      if (cur == null) cur = 0L;
      timeMap.put("select-time", (cur + (time - start)));
    }
    if (ev != null && ev.m_attributeName != null) {
      log.info("selectEnd " + ev.m_attributeName);
    }
    */
  }

  public void generated(GenerateEvent ev) {
    //log.info("generated " + ev.m_name);
  }


  public String _traceKey(TracerEvent ev)
  {
    StringBuilder sb = new StringBuilder();
    switch (ev.m_styleNode.getXSLToken())
    {
    case Constants.ELEMNAME_TEXTLITERALRESULT :
      ElemTextLiteral etl = (ElemTextLiteral) ev.m_styleNode;
      String chars = new String(etl.getChars(), 0, etl.getChars().length);

      sb.append(ev.m_styleNode.getSystemId()).append("#");
      sb.append(ev.m_styleNode.getLineNumber()).append(",");
      sb.append(ev.m_styleNode.getColumnNumber()).append("[");
      sb.append(ev.m_styleNode.getNodeName()).append(":").append(chars.trim()).append("]");
      break;
    case Constants.ELEMNAME_TEMPLATE :
      ElemTemplate et = (ElemTemplate) ev.m_styleNode;

      sb.append(ev.m_styleNode.getSystemId()).append("#");
      sb.append(ev.m_styleNode.getLineNumber()).append(",");
      sb.append(ev.m_styleNode.getColumnNumber()).append("[");
      sb.append(ev.m_styleNode.getNodeName()).append(":");

      if (null != et.getMatch())
      {
        sb.append("match='" + et.getMatch().getPatternString() + "' ");
      }

      if (null != et.getName())
      {
        sb.append("name='" + et.getName() + "' ");
      }
      sb.append("]");
      break;
    default :
      sb.append(ev.m_styleNode.getSystemId()).append("#");
      sb.append(ev.m_styleNode.getLineNumber()).append(",");
      sb.append(ev.m_styleNode.getColumnNumber()).append("[");
      sb.append(ev.m_styleNode.getNodeName()).append("]");
    }
    return sb.toString();
  }

  public void _trace(String prefix, TracerEvent ev)
  {
    StringBuilder sb = new StringBuilder(prefix);
    switch (ev.m_styleNode.getXSLToken())
    {
    case Constants.ELEMNAME_TEXTLITERALRESULT :
      if (m_traceElements)
      {

        ElemTextLiteral etl = (ElemTextLiteral) ev.m_styleNode;
        String chars = new String(etl.getChars(), 0, etl.getChars().length);

        sb.append(ev.m_styleNode.getSystemId()+ " Line #" + ev.m_styleNode.getLineNumber() + ", "
            + "Column #" + ev.m_styleNode.getColumnNumber() + " -- "
            + ev.m_styleNode.getNodeName() + ": " + chars.trim());
      }
      break;
    case Constants.ELEMNAME_TEMPLATE :
      if (m_traceTemplates || m_traceElements)
      {
        ElemTemplate et = (ElemTemplate) ev.m_styleNode;

        sb.append(et.getSystemId()+ " Line #" + et.getLineNumber() + ", " + "Column #"
                   + et.getColumnNumber() + ": " + et.getNodeName() + " ");

        if (null != et.getMatch())
        {
          sb.append("match='" + et.getMatch().getPatternString() + "' ");
        }

        if (null != et.getName())
        {
          sb.append("name='" + et.getName() + "' ");
        }

      }
      break;
    default :
      if (m_traceElements)
      {
        sb.append(ev.m_styleNode.getSystemId()+ " Line #" + ev.m_styleNode.getLineNumber() + ", "
                     + "Column #" + ev.m_styleNode.getColumnNumber() + ": "
                     + ev.m_styleNode.getNodeName());
      }
    }
    log.info(sb.toString());
  }

  public void report() {
    List<CallData> callList = new ArrayList<CallData>();
    Iterator<Map.Entry<String,Long>> iter = timeMap.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry<String,Long> entry = iter.next();
      callList.add(new CallData(entry.getKey(), entry.getValue()));
    }
    Collections.sort(callList);
    for (int i = 0; i < callList.size(); i++) {
      log.info("Metric[" + callList.get(i).call + "] " + callList.get(i).time);
    }
  }

  private class CallData implements Comparable<CallData> {
    String call;
    Long time;
    public CallData(String call, Long time) {
      this.call = call;
      this.time = time;
    }
    public int compareTo(CallData o) {
      int sH = this.call.indexOf('#');
      int cH = o.call.indexOf('#');
      if (sH == -1 && cH == -1) return 0;
      if (sH == -1 && cH != -1) return 1;
      if (sH != -1 && cH == -1) return -1;
      if (sH != -1 && cH != -1) {
        int sL = 0;
        try {
          sL = Integer.parseInt( this.call.substring(sH+1,this.call.indexOf(',',sH+1)) );
        } catch (NumberFormatException e) {
          log.error("NFE for " + this.call.substring(sH+1,this.call.indexOf(',',sH+1)) + " in " + this.call);
        }
        int cL = 0;
        try {
          cL = Integer.parseInt( o.call.substring(cH+1,o.call.indexOf(',',cH+1)) );
        } catch (NumberFormatException e) {
          log.error("NFE for " + o.call.substring(cH+1,o.call.indexOf(',', cH+1)) + " in " + o.call);
        }
        return (sL == cL ? 0 : (sL < cL ? -1 : 1) );
      }

      return 0;
    }
  }
}
