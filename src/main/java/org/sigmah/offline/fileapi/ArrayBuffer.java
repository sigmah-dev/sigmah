package org.sigmah.offline.fileapi;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Data buffer usable like a JavaScript array.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class ArrayBuffer extends JavaScriptObject {
	protected ArrayBuffer() {
	}
	
	public native int length() /*-{
		return this.byteLength;
	}-*/;
}
