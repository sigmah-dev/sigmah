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