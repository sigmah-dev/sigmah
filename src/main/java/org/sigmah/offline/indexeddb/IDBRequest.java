package org.sigmah.offline.indexeddb;

import com.google.gwt.core.client.JavaScriptObject;
import org.sigmah.offline.event.JavaScriptEvent;

/**
 * Native IndexedDB request.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @param <R>
 */
class IDBRequest<R> extends JavaScriptObject {
	public interface RequestSuccessHandler {
		void onSuccess(JavaScriptObject event);
		void onError(JavaScriptObject event);
	}
	
	protected IDBRequest() {
	}
	
	public final native R getResult() /*-{
		return this.result;
	}-*/;
	
	public final native int getResultAsInteger() /*-{
		return this.result;
	}-*/;
	
	public final native double getResultAsDouble() /*-{
		return this.result;
	}-*/;
	
	public final native boolean getResultAsBoolean() /*-{
		return this.result;
	}-*/;
	

	public final native DOMError getError() /*-{
		return this.error;
	}-*/;

	public final native Object getSource() /*-{
		return this.source;
	}-*/;

	public final native IDBTransaction getTransaction() /*-{
		return this.transaction;
	}-*/;

	/**
	 * pending	The request is pending.
	 * done	The request is done.
	 * @return 
	 */
	public final native String getReadyState() /*-{
		return this.readyState;
	}-*/;

	public final native void setOnSuccess(JavaScriptEvent handler) /*-{
		this.onsuccess = handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;);
	}-*/;
	
	public final native void setOnError(JavaScriptEvent handler) /*-{
		this.onerror = handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;);
	}-*/;
	
	public final native void setRequestSuccessHandler(RequestSuccessHandler handler) /*-{
		this.onsuccess = handler.@org.sigmah.offline.indexeddb.IDBRequest.RequestSuccessHandler::onSuccess(Lcom/google/gwt/core/client/JavaScriptObject;);
		this.onerror = handler.@org.sigmah.offline.indexeddb.IDBRequest.RequestSuccessHandler::onError(Lcom/google/gwt/core/client/JavaScriptObject;);
	}-*/;
}
