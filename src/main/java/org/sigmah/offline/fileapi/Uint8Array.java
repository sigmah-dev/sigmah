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

/**
 * Uint8Array is a <a href="https://www.khronos.org/registry/typedarray/specs/latest/">typed array</a>
 * of bytes.
 * This class is usable client-side only.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class Uint8Array extends JavaScriptObject {
	protected Uint8Array() {
	}
	
	public static native Uint8Array createInt8Array(ArrayBuffer arrayBuffer) /*-{
		return new Uint8Array(arrayBuffer);
	}-*/;
	
	public static native Uint8Array createUint8Array(int length) /*-{
		return new Uint8Array(length);
	}-*/;
	
	public static Uint8Array toUint8Array(byte[] array) {
		final Uint8Array result = createUint8Array(array.length);
		for(int index = 0; index < array.length; index++) {
			result.set(index, array[index]);
		}
		return result;
	}
	
	public native byte get(int index) /*-{
		return this[index];
	}-*/;
	
	public native void set(int index, byte value) /*-{
		this[index] = value;
	}-*/;
}
