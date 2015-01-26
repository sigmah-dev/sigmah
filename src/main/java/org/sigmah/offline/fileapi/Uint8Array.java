package org.sigmah.offline.fileapi;

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
