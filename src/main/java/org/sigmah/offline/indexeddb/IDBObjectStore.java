package org.sigmah.offline.indexeddb;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @param <V> Type des objets contenus dans le store
 * @param <K> Type de la clef des objets
 */
final class IDBObjectStore<K, V> extends JavaScriptObject {
	protected IDBObjectStore() {
	}
	
	/**
	 * Adds a new object to the object store. If an object with the same key
	 * already exists, an exception will be thrown.
	 * Result is the key of the added object.
	 * 
	 * @param object
	 * @return 
	 */
	public native IDBRequest<K> add(V object) /*-{
		return this.add(object);
	}-*/;
	
	public native IDBRequest<K> add(V object, K key) /*-{
		return this.add(object, key);
	}-*/;
	
	public native IDBRequest<K> add(V object, int key) /*-{
		return this.add(object, key);
	}-*/;
	
	public native IDBRequest<K> add(V object, double key) /*-{
		return this.add(object, key);
	}-*/;
	
	public native IDBRequest<K> add(V object, float key) /*-{
		return this.add(object, key);
	}-*/;
	
	public native IDBRequest<K> add(V object, char key) /*-{
		return this.add(object, key);
	}-*/;
	
	public native IDBRequest<K> add(V object, boolean key) /*-{
		return this.add(object, key);
	}-*/;
	
	/**
	 * Adds or update an object to the object store.
	 * Result is the key of the added object.
	 * 
	 * @param object
	 * @return 
	 */
	public native IDBRequest<K> put(V object) /*-{
		return this.put(object);
	}-*/;
	
	public native IDBRequest<K> put(V object, K key) /*-{
		return this.put(object, key);
	}-*/;
	
	public native IDBRequest<K> put(V object, int key) /*-{
		return this.put(object, key);
	}-*/;
	
	public native IDBRequest<K> put(V object, double key) /*-{
		return this.put(object, key);
	}-*/;
	
	public native IDBRequest<K> put(V object, float key) /*-{
		return this.put(object, key);
	}-*/;
	
	public native IDBRequest<K> put(V object, char key) /*-{
		return this.put(object, key);
	}-*/;
	
	public native IDBRequest<K> put(V object, boolean key) /*-{
		return this.put(object, key);
	}-*/;
	
	public native IDBRequest clear() /*-{
		return this.clear();
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
	
	/**
	 * Removes the object associated with the given key.
	 * 
	 * @param key
	 * @return 
	 */
	public native IDBRequest<Object> delete(K key) /*-{
		return this['delete'](key);
	}-*/;
	
	public native IDBRequest<Object> delete(int key) /*-{
		return this['delete'](key);
	}-*/;
	
	public native IDBRequest<Object> delete(double key) /*-{
		return this['delete'](key);
	}-*/;
	
	public native IDBRequest<Object> delete(float key) /*-{
		return this['delete'](key);
	}-*/;
	
	public native IDBRequest<Object> delete(char key) /*-{
		return this['delete'](key);
	}-*/;
	
	public native IDBRequest<Object> delete(boolean key) /*-{
		return this['delete'](key);
	}-*/;
	
	/**
	 * Retrieves the object associated with the given key.
	 * 
	 * @param key
	 * @return 
	 */
	public native IDBRequest<V> get(K key) /*-{
		return this.get(key);
	}-*/;
	
	public native IDBRequest<V> get(int key) /*-{
		return this.get(key);
	}-*/;
	
	public native IDBRequest<V> get(double key) /*-{
		return this.get(key);
	}-*/;
	
	public native IDBRequest<V> get(float key) /*-{
		return this.get(key);
	}-*/;
	
	public native IDBRequest<V> get(char key) /*-{
		return this.get(key);
	}-*/;
	
	public native IDBRequest<V> get(boolean key) /*-{
		return this.get(key);
	}-*/;
	
	public native IDBIndex<K, V> index(String name) /*-{
		return this.index(name);
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
	
	public native void createIndex(String name, String keyPath) /*-{
		this.createIndex(name, keyPath);
	}-*/;
	
	public native void createIndex(String name, String keyPath, boolean unique, boolean multiEntry) /*-{
		this.createIndex(name, keyPath, {
			"unique": unique,
			"multiEntry": multiEntry
		});
	}-*/;
	
	public native final String getName() /*-{
		return this.objectStore;
	}-*/;
	
	public native final IDBTransaction getTransaction() /*-{
		return this.transaction;
	}-*/;
}
