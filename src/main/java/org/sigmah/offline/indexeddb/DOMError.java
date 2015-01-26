package org.sigmah.offline.indexeddb;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class DOMError extends JavaScriptObject {
	
	protected DOMError() {
	}
	
	public native String getName() /*-{
		return this.name;
	}-*/;
    
    public native String getMessage() /*-{
		return this.message;
	}-*/;
}
