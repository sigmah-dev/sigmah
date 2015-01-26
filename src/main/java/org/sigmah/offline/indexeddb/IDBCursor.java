package org.sigmah.offline.indexeddb;

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
	
	public native Object getKey() /*-{
		return this.key;
	}-*/;

	public native Object getPrimaryKey() /*-{
		return this.primaryKey;
	}-*/;
	
	public native T getValue() /*-{
		return this.value;
	}-*/;
}
