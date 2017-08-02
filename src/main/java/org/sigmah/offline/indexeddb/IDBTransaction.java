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
		this.oncomplete = $entry(function(e) {
			handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
		});
	}-*/;
	
	public native IDBRequest get(String storeName, String objectKey) /*-{
		return this.transaction(storeName).objectStore(storeName).get(objectKey);
	}-*/;
}
