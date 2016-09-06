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
import org.sigmah.offline.event.JavaScriptEvent;

/**
 * Native IndexedDB request.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @param <R>
 */
class IDBRequest<R> extends JavaScriptObject {
	
	/**
	 * Defines a handler that will be called when a request is done.
	 */
	public interface RequestSuccessHandler {
		
		/**
		 * Method called if the request succeeded.
		 * 
		 * @param event 
		 *			JavaScript event.
		 */
		void onSuccess(JavaScriptObject event);
		
		/**
		 * Method called if the request failed.
		 * 
		 * @param event 
		 *			Javascript event.
		 */
		void onError(JavaScriptObject event);
		
	}
	
	/**
	 * Empty protected constructor. Required for subclasses of JavaScriptObject.
	 */
	protected IDBRequest() {
		// Empty.
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
	 * Returns the 'ready' state of this request.
	 * <ul>
	 * <li><code>pending</code>: The request is pending.
	 * <li><code>done</code>: The request is done.
	 * </ul>
	 * @return The ready state.
	 */
	public final native String getReadyState() /*-{
		return this.readyState;
	}-*/;

	public final native void setOnSuccess(JavaScriptEvent<?> handler) /*-{
		this.onsuccess = handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;);
	}-*/;
	
	public final native void setOnError(JavaScriptEvent<?> handler) /*-{
		this.onerror = handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;);
	}-*/;
	
	public final native void setRequestSuccessHandler(RequestSuccessHandler handler) /*-{
		this.onsuccess = handler.@org.sigmah.offline.indexeddb.IDBRequest.RequestSuccessHandler::onSuccess(Lcom/google/gwt/core/client/JavaScriptObject;);
		this.onerror = handler.@org.sigmah.offline.indexeddb.IDBRequest.RequestSuccessHandler::onError(Lcom/google/gwt/core/client/JavaScriptObject;);
	}-*/;
}
