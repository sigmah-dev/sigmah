package org.sigmah.offline.js;

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
import com.google.gwt.core.client.JsArrayString;
import java.util.Map;

/**
 * Simple JavaScript map. Usable with IndexedDB.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @param <K> Key
 * @param <V> Value
 */
public final class JsMap<K,V> extends JavaScriptObject {
	
	protected JsMap() {
	}
	
	public static native <K,V> JsMap<K,V> createMap() /*-{
		return {};
	}-*/;

	public native boolean containsKey(K key) /*-{
		return typeof this[key] != 'undefined';
	}-*/;
	
	public native boolean containsKey(int key) /*-{
		return typeof this[key] != 'undefined';
	}-*/;
	
	public native boolean containsKey(short key) /*-{
		return typeof this[key] != 'undefined';
	}-*/;
	
	public native boolean containsKey(byte key) /*-{
		return typeof this[key] != 'undefined';
	}-*/;
	
	public native boolean containsKey(float key) /*-{
		return typeof this[key] != 'undefined';
	}-*/;
	
	public native boolean containsKey(double key) /*-{
		return typeof this[key] != 'undefined';
	}-*/;
	
	public native boolean containsKey(char key) /*-{
		return typeof this[key] != 'undefined';
	}-*/;
	
	public native boolean containsKey(boolean key) /*-{
		return typeof this[key] != 'undefined';
	}-*/;

	public native V get(K key) /*-{
		return this[key];
	}-*/;

	public native V get(int key) /*-{
		return this[key];
	}-*/;

	public native V get(short key) /*-{
		return this[key];
	}-*/;
	
	public native V get(byte key) /*-{
		return this[key];
	}-*/;
	
	public native V get(double key) /*-{
		return this[key];
	}-*/;

	public native V get(float key) /*-{
		return this[key];
	}-*/;

	public native V get(char key) /*-{
		return this[key];
	}-*/;

	public native V get(boolean key) /*-{
		return this[key];
	}-*/;
	
	public native V put(K key, V value) /*-{
		var previous = this[key];
		this[key] = value;
		return previous;
	}-*/;

	public native V put(int key, V value) /*-{
		var previous = this[key];
		this[key] = value;
		return previous;
	}-*/;
	
	public native V put(short key, V value) /*-{
		var previous = this[key];
		this[key] = value;
		return previous;
	}-*/;
	
	public native V put(byte key, V value) /*-{
		var previous = this[key];
		this[key] = value;
		return previous;
	}-*/;
	
	public native V put(double key, V value) /*-{
		var previous = this[key];
		this[key] = value;
		return previous;
	}-*/;
	
	public native V put(float key, V value) /*-{
		var previous = this[key];
		this[key] = value;
		return previous;
	}-*/;

	public native V put(char key, V value) /*-{
		var previous = this[key];
		this[key] = value;
		return previous;
	}-*/;
	
	public native V put(boolean key, V value) /*-{
		var previous = this[key];
		this[key] = value;
		return previous;
	}-*/;
	
	public native V remove(K key) /*-{
		var previous = this[key];
		delete this[key];
		return previous;
	}-*/;
	
	public native V remove(int key) /*-{
		var previous = this[key];
		delete this[key];
		return previous;
	}-*/;
	
	public native V remove(short key) /*-{
		var previous = this[key];
		delete this[key];
		return previous;
	}-*/;
	
	public native V remove(byte key) /*-{
		var previous = this[key];
		delete this[key];
		return previous;
	}-*/;
	
	public native V remove(double key) /*-{
		var previous = this[key];
		delete this[key];
		return previous;
	}-*/;
	
	public native V remove(float key) /*-{
		var previous = this[key];
		delete this[key];
		return previous;
	}-*/;
	
	public native V remove(char key) /*-{
		var previous = this[key];
		delete this[key];
		return previous;
	}-*/;
	
	public native V remove(boolean key) /*-{
		var previous = this[key];
		delete this[key];
		return previous;
	}-*/;

	public void putAll(Map<K,V> m) {
		for(Map.Entry<K,V> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	public native JsArrayString keyArray() /*-{
		var array = [];
		for(var key in this) {
			array.push(key);
		}
		return array;
	}-*/;

	public native int size() /*-{
		var size = 0;
		for(var key in this) {
			size++;
		}
		return size;
	}-*/;
}
