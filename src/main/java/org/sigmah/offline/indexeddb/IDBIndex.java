package org.sigmah.offline.indexeddb;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @param <K>
 * @param <V>
 */
final class IDBIndex<K, V> extends JavaScriptObject {
	protected IDBIndex() {
	}
	
	/**
	 * Récupère l'objet avec la clef donnée depuis le store.
	 * 
	 * @param value
	 * @return 
	 */
	public native IDBRequest<V> get(Object value) /*-{
		return this.get(value);
	}-*/;
	
	public native IDBRequest<V> get(int value) /*-{
		return this.get(value);
	}-*/;
	
	public native IDBRequest<V> get(double value) /*-{
		return this.get(value);
	}-*/;
	
	public native IDBRequest<V> get(float value) /*-{
		return this.get(value);
	}-*/;
	
	public native IDBRequest<V> get(char value) /*-{
		return this.get(value);
	}-*/;
	
	public native IDBRequest<Integer> count() /*-{
		return this.count();
	}-*/;
	
	public native IDBRequest<Integer> count(Object value) /*-{
		return this.count();
	}-*/;
	
	public native IDBRequest<Integer> count(int value) /*-{
		return this.count();
	}-*/;
	
	public native IDBRequest<Integer> count(float value) /*-{
		return this.count();
	}-*/;
	
	public native IDBRequest<Integer> count(double value) /*-{
		return this.count();
	}-*/;
	
	public native IDBRequest<Integer> count(char value) /*-{
		return this.count();
	}-*/;
	
	public native IDBRequest<Integer> count(boolean value) /*-{
		return this.count();
	}-*/;
	
	public native IDBRequest<Integer> count(IDBKeyRange keyRange) /*-{
		return this.count(keyRange);
	}-*/;
	
	public native IDBRequest<IDBCursor<V>> openCursor() /*-{
		return this.openCursor();
	}-*/;
	
	public native IDBRequest<IDBCursor<V>> openCursor(IDBKeyRange keyRange) /*-{
		return this.openCursor(keyRange);
	}-*/;
	
	public native IDBRequest<IDBCursor<V>> openCursor(IDBKeyRange keyRange, String order) /*-{
		return this.openCursor(keyRange, order);
	}-*/;
	
	public native IDBRequest<IDBCursor<K>> openKeyCursor() /*-{
		return this.openKeyCursor();
	}-*/;
	
	public native IDBRequest<IDBCursor<K>> openKeyCursor(IDBKeyRange keyRange) /*-{
		return this.openKeyCursor(keyRange);
	}-*/;
	
	public native IDBRequest<IDBCursor<K>> openKeyCursor(IDBKeyRange keyRange, String order) /*-{
		return this.openKeyCursor(keyRange, order);
	}-*/;
}
