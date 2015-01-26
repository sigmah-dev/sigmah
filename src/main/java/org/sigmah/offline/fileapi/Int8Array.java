package org.sigmah.offline.fileapi;

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
