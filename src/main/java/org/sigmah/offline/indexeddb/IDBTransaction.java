package org.sigmah.offline.indexeddb;

import com.google.gwt.core.client.JavaScriptObject;
import org.sigmah.offline.event.JavaScriptEvent;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
final class IDBTransaction extends JavaScriptObject {
	public final static String MODE_READ_ONLY = "readonly";
	public final static String MODE_READ_WRITE = "readwrite";
	public final static String MODE_VERSION_CHANGE = "versionchange";
	
	protected IDBTransaction() {
	}
	
	public native IDBObjectStore getObjectStore(String storeName) /*-{
		return this.objectStore(storeName);
	}-*/;
	
	public native void onComplete(JavaScriptEvent handler) /*-{
		this.oncomplete = handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;);
	}-*/;
	
	public native IDBRequest get(String storeName, String objectKey) /*-{
		return this.transaction(storeName).objectStore(storeName).get(objectKey);
	}-*/;
}
