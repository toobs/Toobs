package org.toobsframework.doitref;

import java.util.Map;

import org.toobsframework.doitref.beans.DoItRefBean;
import org.toobsframework.jms.doitref.JmsDoItRefException;


public interface IDoItRefQueue {

  public abstract void put(String doItName, Map<String,Object> params) throws JmsDoItRefException;

  public abstract void runDoItRef(DoItRefBean bean) throws JmsDoItRefException;

}