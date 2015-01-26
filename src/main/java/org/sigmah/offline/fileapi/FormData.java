package org.sigmah.offline.fileapi;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * FormData act as a builder of POST and GET queries content.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class FormData extends JavaScriptObject {
	public native static FormData createFormData();
	
	protected FormData() {
	}
	
	public native void append(String name, String value) /*-{
		this.append(name, value);
	}-*/;
	
	public native void append(String name, Blob value) /*-{
		this.append(name, value);
	}-*/;
	
	public native void append(String name, Blob value, String filename) /*-{
		this.append(name, value, filename);
	}-*/;
}
