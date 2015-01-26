package org.sigmah.offline.js;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Interface-like object that allow to retrieve the "id" value of every
 * JavaScriptObject.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class HasId extends JavaScriptObject {
	
	protected HasId() {
	}
	
	public native int getId() /*-{
		return this.id;
	}-*/;
}
