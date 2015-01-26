package org.sigmah.offline.indexeddb;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Request {
	final IDBRequest request;
    private final List<AsyncCallback<Request>> callbacks = new ArrayList<AsyncCallback<Request>>();

	public Request() {
		this.request = null;
	}
	
	Request(IDBRequest request) {
		this.request = request;
		registerEvents(request);
	}
	
	public <T> T getResult() {
		return (T) request.getResult();
	}
	
	public int getResultAsInteger() {
		return request.getResultAsInteger();
	}
    
    public IndexedDBException getException() {
        return new IndexedDBException(request.getError());
    }
	
	private native void registerEvents(IDBRequest request) /*-{
		if(typeof $wnd.Object.getPrototypeOf != 'undefined') {
			$wnd.Object.getPrototypeOf(this).handleEvent = function(event) {
				switch(event.type) {
					case 'success':
						this.@org.sigmah.offline.indexeddb.Request::fireSuccess(Lcom/google/gwt/core/client/JavaScriptObject;)(event);
						break;
					case 'error':
						this.@org.sigmah.offline.indexeddb.Request::fireError(Lcom/google/gwt/core/client/JavaScriptObject;)(event);
						break;
					default:
						break;
				}
			};
			request.addEventListener('success', this);
			request.addEventListener('error', this);
		}
	}-*/;
	
	protected void fireSuccess(JavaScriptObject event) {
		for(final AsyncCallback<Request> callback : callbacks) {
            callback.onSuccess(this);
		}
	}
	
	protected void fireError(JavaScriptObject event) {
        final IndexedDBException exception = getException();
        for(final AsyncCallback<Request> callback : callbacks) {
            callback.onFailure(exception);
		}
	}
    
    public void addCallback(final AsyncCallback<Request> callback) {
        callbacks.add(callback);
    }
}
