package org.sigmah.offline.fileapi;

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
import com.google.gwt.core.client.JsArray;

/**
 * Int8Array is a <a href="https://www.khronos.org/registry/typedarray/specs/latest/">typed array</a>
 * of bytes.
 * This class is usable client-side only.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class Int8Array extends JavaScriptObject {
	protected Int8Array() {
	}
	
	public static native Int8Array createInt8Array(ArrayBuffer arrayBuffer) /*-{
		return new Int8Array(arrayBuffer);
	}-*/;
	
	public static native Int8Array createInt8Array(int length) /*-{
		return new Int8Array(length);
	}-*/;
	
	public static Int8Array createInt8Array(JsArray<Int8Array> arrays) {
		final Int8Array result;
		
		int totalLength = 0;
		for(int index = 0; index < arrays.length(); index++) {
			totalLength += arrays.get(index).length();
		}
		
		result = createInt8Array(totalLength);
		int offset = 0;
		for(int index = 0; index < arrays.length(); index++) {
			final Int8Array array = arrays.get(index);
			result.set(array, offset);
			offset += array.length();
		}
		
		return result;
	}
	
	public static Int8Array toInt8Array(byte[] array) {
		final Int8Array result = createInt8Array(array.length);
		for(int index = 0; index < array.length; index++) {
			result.set(index, array[index]);
		}
		return result;
	}
	
	public byte[] toByteArray() {
		final int length = length();
		final byte[] bytes = new byte[length];
		for(int index = 0; index < length; index++) {
			bytes[index] = get(index);
		}
		return bytes;
	}
	
	public native byte get(int index) /*-{
		return this[index];
	}-*/;
	
	public native void set(Int8Array value, int offset) /*-{
		this.set(value, offset);
	}-*/;
	
	public native void set(int index, byte value) /*-{
		this[index] = value;
	}-*/;
	
	public native int length() /*-{
		return this.length;
	}-*/;
}
