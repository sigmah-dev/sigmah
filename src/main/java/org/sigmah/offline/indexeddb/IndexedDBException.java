package org.sigmah.offline.indexeddb;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
