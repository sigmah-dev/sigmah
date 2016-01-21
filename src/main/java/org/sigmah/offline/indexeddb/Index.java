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

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Index {
	private final IDBIndex nativeIndex;
	
	Index(IDBIndex index) {
		this.nativeIndex = index;
	}
	
	public Request get(Object key) {
		return new Request(nativeIndex.get(key));
	}
	
	public Request get(int key) {
		return new Request(nativeIndex.get(key));
	}
	
	public Request get(float key) {
		return new Request(nativeIndex.get(key));
	}
	
	public Request get(double key) {
		return new Request(nativeIndex.get(key));
	}
	
	public Request get(char key) {
		return new Request(nativeIndex.get(key));
	}
	
	public CountRequest count() {
		return new CountRequest(nativeIndex.count());
	}
	
	public CountRequest count(Object value) {
		return new CountRequest(nativeIndex.count(value));
	}
	
	public CountRequest count(double value) {
		return new CountRequest(nativeIndex.count(value));
	}
	
	public CountRequest count(float value) {
		return new CountRequest(nativeIndex.count(value));
	}
	
	public CountRequest count(char value) {
		return new CountRequest(nativeIndex.count(value));
	}
	
	public CountRequest count(boolean value) {
		return new CountRequest(nativeIndex.count(value));
	}
	
	public CountRequest count(IDBKeyRange range) {
		return new CountRequest(nativeIndex.count(range));
	}
	
	public OpenCursorRequest openCursor() {
		return new OpenCursorRequest(nativeIndex.openCursor());
	}
	
	public OpenCursorRequest openCursor(IDBKeyRange range) {
		return new OpenCursorRequest(nativeIndex.openCursor(range));
	}
	
	public OpenCursorRequest openCursor(IDBKeyRange range, Order order) {
		return new OpenCursorRequest(nativeIndex.openCursor(range, order.toString()));
	}
	
	public OpenCursorRequest openKeyCursor() {
		return new OpenCursorRequest(nativeIndex.openKeyCursor());
	}
	
	public OpenCursorRequest openKeyCursor(IDBKeyRange range) {
		return new OpenCursorRequest(nativeIndex.openKeyCursor(range));
	}
	
	public OpenCursorRequest openKeyCursor(IDBKeyRange range, Order order) {
		return new OpenCursorRequest(nativeIndex.openKeyCursor(range, order.toString()));
	}
}
