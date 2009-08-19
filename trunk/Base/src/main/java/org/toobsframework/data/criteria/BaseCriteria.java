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
package org.toobsframework.data.criteria;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public abstract class BaseCriteria {

  private java.lang.Boolean paged = new Boolean(false);
  private java.lang.Boolean activeInd;
  private ArrayList order;

  public java.lang.Boolean getPaged() {
    return this.paged;
  }

  public void setPaged(java.lang.Boolean paged) {
    this.paged = paged;
  }
  
  public ArrayList getOrder() {
    return order;
  }

  public void setOrder(ArrayList order) {
    this.order = new ArrayList();
    if (order != null) {
      for (int i = 0; i < order.size(); i++) {
        String orderInst = (String)order.get(i);
        if (orderInst != null) {
          String[] orderSplit = orderInst.split(";");
          for (int o = 0; o < orderSplit.length; o++) {
            this.order.add(orderSplit[o]);
          }
        }
      }
    }
  }

  public java.lang.Boolean getActiveInd() {
    return activeInd;
  }

  public void setActiveInd(java.lang.Boolean activeInd) {
    this.activeInd = activeInd;
  }

  public abstract java.lang.Integer getMaximumResultSize();
  
  public abstract java.lang.Integer getFirstResult();
  
  public abstract void setFirstResult(java.lang.Integer firstResult);
  
  public abstract void setTotalRows(java.lang.Integer totalRows);
  
  public boolean applyOrder(ICriteriaSearchParameter parameter, String attributeName) {
    if (this.getOrder() != null && this.getOrder().size() > 0) {
      if(this.getOrder().contains(attributeName + "_ASC")){
        parameter.setOrderDirection(ICriteriaSearchParameter.ORDER_ASC);
        parameter.setOrderRelevance(this.getOrder().indexOf(attributeName + "_ASC"));
        this.getOrder().remove(attributeName + "_ASC");
      }
      if(this.getOrder().contains(attributeName + "_DSC")){
        parameter.setOrderDirection(ICriteriaSearchParameter.ORDER_DESC);
        parameter.setOrderRelevance(this.getOrder().indexOf(attributeName + "_DSC"));
        this.getOrder().remove(attributeName + "_DSC");
      }
      return true;
    } else {
      return false;
    }
  }

}
