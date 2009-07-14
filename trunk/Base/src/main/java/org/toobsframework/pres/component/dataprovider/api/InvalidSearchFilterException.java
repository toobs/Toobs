package org.toobsframework.pres.component.dataprovider.api;

import org.toobsframework.exception.BaseException;

/**
 * @author stewari
 */
public class InvalidSearchFilterException extends BaseException {
    
    private String filterExpression;
    private String dataSourceId;
    
    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }
    
    public String getDataSourceId() {
        return this.dataSourceId;
    }
    
    public void setFilterExpression(String filterExpression) {
        this.filterExpression = filterExpression;
    }

    public String getFilterExpression() {
        return this.filterExpression;
    }
}