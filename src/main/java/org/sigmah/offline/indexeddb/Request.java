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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import java.util.List;
import org.sigmah.shared.command.result.Result;

/**
 * IndexedDB request.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Request implements Result {
	
	IDBRequest request;
	
    private List<AsyncCallback<Request>> callbacks = new ArrayList<AsyncCallback<Request>>();

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
