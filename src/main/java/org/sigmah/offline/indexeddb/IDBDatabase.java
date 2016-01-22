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
