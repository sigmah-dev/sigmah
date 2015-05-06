package org.sigmah.offline.indexeddb;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptException;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class IndexedDBException extends RuntimeException {
    
    private DOMError nativeError;
    private DOMErrorType type;

    public IndexedDBException(DOMError nativeError) {
        super(nativeError.getName() + ": " + nativeError.getMessage());
        
        this.nativeError = nativeError;
        try {
            this.type = DOMErrorType.valueOf(nativeError.getName());
            
        } catch(IllegalArgumentException e) {
            Log.error("Unknown DOM error type: " + nativeError.getName(), e);
            this.type = DOMErrorType.UnknownError;
        }
    }
	
	public IndexedDBException(JavaScriptException e) {
		this((DOMError) e.getException());
	} 

    public DOMError getNativeError() {
        return nativeError;
    }

    public void setNativeError(DOMError nativeError) {
        this.nativeError = nativeError;
    }

    public DOMErrorType getType() {
        return type;
    }

    public void setType(DOMErrorType type) {
        this.type = type;
    }
}
