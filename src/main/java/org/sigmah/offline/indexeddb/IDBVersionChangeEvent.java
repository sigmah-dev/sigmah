package org.sigmah.offline.indexeddb;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class IDBVersionChangeEvent extends JavaScriptObject {
	
	protected IDBVersionChangeEvent() {
	}
	
	public native int getOldVersion() /*-{
		return this.oldVersion;
	}-*/;

	public native int getNewVersion() /*-{
		return this.newVersion;
	}-*/;
}
