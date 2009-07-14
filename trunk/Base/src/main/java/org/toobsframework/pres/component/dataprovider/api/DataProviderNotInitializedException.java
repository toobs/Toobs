package org.toobsframework.pres.component.dataprovider.api;

import org.toobsframework.exception.BaseException;

/**
 * @author stewari
 */
public class DataProviderNotInitializedException extends BaseException {
    
    private String dataSourceId;
     
    public DataProviderNotInitializedException() {
        super();
    }
    
    public DataProviderNotInitializedException(String message) {
        super(message);
    }
    
    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }
    
    public String getDataSourceId() {
        return this.dataSourceId;
    }
}
