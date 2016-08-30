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

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @param <T>
 */
final class IDBCursor<T> extends JavaScriptObject {
	public static final String ORDER_ASCENDING = "next";
	public static final String ORDER_DESCENDING = "prev";
	public static final String ORDER_UNIQUE_ASCENDING = "nextunique";
	public static final String ORDER_UNIQUE_DESCENDING = "prevunique";
	
	protected IDBCursor() {
	}
	
	public native void advance(int steps) /*-{
		this.advance(steps);
	}-*/;
	
	public native void next() /*-{
		this['continue']();
	}-*/;
	
	public native IDBRequest<T> delete() /*-{
		return this['delete']();
	}-*/;
	
	public native IDBRequest<T> update(T newValue) /*-{
		return this.update(newValue);
	}-*/;
	
	public native IDBObjectStore getSource() /*-{
		return this.source;
	}-*/;
	
	public native String getDirection() /*-{
		return this.direction;
	}-*/;
	
	public native Object getKeyObject() /*-{
		return this.key;
	}-*/;
	
	public native int getKeyInteger() /*-{
		return this.key;
	}-*/;

	public native Object getPrimaryKeyObject() /*-{
		return this.primaryKey;
	}-*/;

	public native int getPrimaryKeyInteger() /*-{
		return this.primaryKey;
	}-*/;
	
	public native T getValue() /*-{
		return this.value;
	}-*/;
}
