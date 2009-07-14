package org.toobsframework.pres.component.dataprovider.manager;

import org.toobsframework.exception.BaseException;

/**
 * @author stewari
 */
public class DataProviderNotFoundException extends BaseException {
    
    private static final long serialVersionUID = -2708723307021279461L;
    
    private String dataSourceId;
    
    public DataProviderNotFoundException() {
    	super();
    }
    
    public DataProviderNotFoundException(String message) {
    	super(message);
    }
    
    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }
    
    public String getDataSourceId() {
        return this.dataSourceId;
    }
}
