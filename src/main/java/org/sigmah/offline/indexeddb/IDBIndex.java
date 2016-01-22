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
