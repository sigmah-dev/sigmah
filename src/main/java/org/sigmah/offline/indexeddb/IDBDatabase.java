package org.sigmah.offline.indexeddb;

import org.sigmah.offline.event.JavaScriptEvent;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * Native IndexedDB database.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
final class IDBDatabase extends JavaScriptObject {
	
	protected IDBDatabase() {
		// Not accessible.
	}
	
	public native String getName() /*-{
		return this.name;
	}-*/;
	
	public native int getVersion() /*-{
		return this.version;
	}-*/;
	
	public native void close() /*-{
		this.close();
	}-*/;
	
	public native JsArrayString getObjectStoreNames() /*-{
		return this.objectStoreNames;
	}-*/;
	
	public native IDBObjectStore createObjectStore(String name) /*-{
		return this.createObjectStore(name);
	}-*/;
	
	public native IDBObjectStore createObjectStore(String name, String keyPath) /*-{
		return this.createObjectStore(name, {
			"keyPath": keyPath
		});
	}-*/;
	
	public native IDBObjectStore createObjectStore(String name, boolean autoIncrement) /*-{
		return this.createObjectStore(name, {
			"autoIncrement": autoIncrement
		});
	}-*/;
	
	public native IDBObjectStore createObjectStore(String name, String keyPath, boolean autoIncrement) /*-{
		return this.createObjectStore(name, {
			"keyPath": keyPath,
			"autoIncrement": autoIncrement
		});
	}-*/;
	
	public native void deleteObjectStore(String name) /*-{
		this.deleteObjectStore(name);
	}-*/;
	
	public native IDBTransaction getTransaction(JsArrayString storeNames, String readOnly) /*-{
		return this.transaction(storeNames, readOnly);
	}-*/;
		
	/**
	 * Toutes les erreurs non gérées dans les appels suivant seront attrapées ici.
	 * @param handler 
	 */
	public final native void setOnError(JavaScriptEvent handler) /*-{
		this.onerror = handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;);
	}-*/;
	
}
