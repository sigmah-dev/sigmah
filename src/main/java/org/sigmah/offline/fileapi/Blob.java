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

import java.util.Date;

import org.sigmah.client.ui.widget.form.ButtonFileUploadField;
import org.sigmah.offline.js.Values;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsDate;
import com.google.gwt.dom.client.InputElement;

/**
 * Javascript Blob. Representation of a file. Can be created from scratch or
 * readed from user's hard drive by using an input of type "file".
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class Blob extends JavaScriptObject {
	
	public static Blob getBlob(ButtonFileUploadField fileUpload) {
		return getBlobFromInputFileElement(fileUpload.getFileInput());
	}
	
	public static Blob createBlob(JsArray<Int8Array> array, String contentType) {
		return createNativeBlob(array, createOptions(contentType, null));
	}
	
	protected Blob() {
	}
	
	public static native Blob getBlobFromInputFileElement(InputElement element) /*-{
		return element.files[0];
	}-*/;
	
	private static native Blob createNativeBlob(JsArray<Int8Array> bytes, JavaScriptObject options) /*-{
		return new Blob(bytes, options);
	}-*/;
	
	private static native JavaScriptObject createOptions(String contentType, String endings) /*-{
		var option = {};
		if(contentType != null) {
			option.type = contentType;
		}
		if(endings != null) {
			option.endings = endings;
		}
		return option;
	}-*/;
	
	/**
	 * Retrieves the name of this file.
	 * @return The file name.
	 */
	public native String getName() /*-{
		return this.name;
	}-*/;
	
	/**
	 * Retrieves the size of this file in bytes.
	 * @return The size of this file in bytes
	 */
	public native int getSize() /*-{
		return this.size;
	}-*/;
	
	private native JsDate getLastModifiedJsDate() /*-{
		return this.lastModifiedDate;
	}-*/;
	
	/**
	 * Retrieves the last modified date of this file.
	 * @return The last modified date.
	 */
	public Date getLastModifiedDate() {
		return Values.toDate(getLastModifiedJsDate());
	}
	
	/**
	 * Retrieves the MIME type of this file.
	 * @return The MIME type.
	 */
	public native String getType() /*-{
		return this.type;
	}-*/;
	
	/**
	 * If supported, creates a new <code>Blob</code> containing a subset of the
	 * data of this file, starting at the given offset.
	 * @param start Start offset.
	 * @return A new <code>Blob</code> or <code>null</code> if this method is unsupported.
	 */
	public native Blob slice(int start) /*-{
		if(typeof this.slice != 'undefined') {
			return this.slice(start);
		} else if(typeof this.mozSlice != 'undefined') {
			return this.mozSlice(start);
		} else if(typeof this.webkitSlice != 'undefined') {
			return this.webkitSlice(start);
		}
	}-*/;
	
	/**
	 * If supported, creates a new <code>Blob</code> containing a subset of the
	 * data of this file, starting at <code>start</code> and ending before 
	 * <code>end</code>.
	 * @param start Start offset.
	 * @param end End.
	 * @return A new <code>Blob</code> or <code>null</code> if this method is unsupported.
	 */
	public native Blob slice(int start, int end) /*-{
		if(typeof this.slice != 'undefined') {
			return this.slice(start, end);
		} else if(typeof this.mozSlice != 'undefined') {
			return this.mozSlice(start, end);
		} else if(typeof this.webkitSlice != 'undefined') {
			return this.webkitSlice(start, end);
		}
	}-*/;
	
	/**
	 * If supported, creates a new <code>Blob</code> containing a subset of the
	 * data of this file, starting at <code>start</code> and ending before 
	 * <code>end</code>.
	 * The MIME type of the new <code>Blob</code> is changed to the given 
	 * <code>contentType</code>.
	 * @param start Start offset.
	 * @param end End.
	 * @param contentType MIME type to set.
	 * @return A new <code>Blob</code> or <code>null</code> if this method is unsupported.
	 */
	public native Blob slice(int start, int end, String contentType) /*-{
		if(typeof this.slice != 'undefined') {
			return this.slice(start, end, contentType);
		} else if(typeof this.mozSlice != 'undefined') {
			return this.mozSlice(start, end, contentType);
		} else if(typeof this.webkitSlice != 'undefined') {
			return this.webkitSlice(start, end, contentType);
		}
	}-*/;
}
