package org.toobsframework.pres.component.dataprovider.api;

import org.toobsframework.exception.BaseException;

/**
 * @author stewari
 */
public class DataProviderInitializationException extends BaseException {
    
    private String dataSourceId;
     
    public DataProviderInitializationException() {
        super();
    }
    
    public DataProviderInitializationException(String message) {
        super(message);
    }
    
    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }
    
    public String getDataSourceId() {
        return this.dataSourceId;
    }

    public DataProviderInitializationException(String message, Throwable cause) {
      super(message, cause);
    }

    public DataProviderInitializationException(Throwable cause) {
      super(cause);
    }
}
