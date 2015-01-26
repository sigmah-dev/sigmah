package org.sigmah.offline.indexeddb;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Native implementation of IndexedDB.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
final class NativeIndexedDB extends JavaScriptObject {
	protected NativeIndexedDB() {
	}

	static native NativeIndexedDB getIndexedDB() /*-{
		return $wnd.indexedDB;
	}-*/;

	native IDBOpenDBRequest open(String name) /*-{
		return this.open(name);
	}-*/;

	/**
	 * Create or open an IndexedDB database.
	 * 
	 * @param name Name of the database to open.
	 * @param version Version number of the database. Should be a long but long type is not supported by GWT.
	 * @return 
	 */
	native IDBOpenDBRequest open(String name, int version) /*-{
		return this.open(name, version);
	}-*/;

	/**
	 * Remove an IndexedDB database.
	 * 
	 * @param name Name of the database to remove.
	 * @return 
	 */
	native IDBOpenDBRequest deleteDatabase(String name) /*-{
		return this.deleteDatabase(name);
	}-*/;
}