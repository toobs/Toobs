package org.toobsframework.pres.component.dataprovider.api;

import org.toobsframework.exception.BaseException;

/**
 * @author stewari
 */
public class ObjectSaveException extends BaseException {

	private String dataSourceId;
    
    public ObjectSaveException(String message) {
        super(message);
    }
    
    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }
    
    public String getDataSourceId() {
        return this.dataSourceId;
    }
}