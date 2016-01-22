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

import java.util.ArrayList;
import java.util.List;

import org.sigmah.offline.event.ProgressEvent;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Javascript equivalent to FileInputStream. Can read {@link Blob} objects as
 * strings, data URIs or ArrayBuffers.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class FileReader {
	/**
	 * Native implementation of FileReader.
	 */
	private static final class NativeFileReader extends JavaScriptObject {
		protected NativeFileReader() {
		}
		
		public native void readAsArrayBuffer(Blob blob) /*-{
			this.readAsArrayBuffer(blob);
		}-*/;
		
		public native void readAsText(Blob blob, String encoding) /*-{
			this.readAsText(blob, encoding);
		}-*/;
		
		public native void readAsDataURL(Blob blob) /*-{
			this.readAsDataURL(blob);
		}-*/;
		
		public native void abort() /*-{
			this.abort();
		}-*/;
		
		public native JavaScriptObject getResult() /*-{
			return this.result;
		}-*/;
		
		public native ArrayBuffer getResultAsArrayBuffer() /*-{
			return this.result;
		}-*/;
		
		public native String getResultAsString() /*-{
			return this.result;
		}-*/;
		
		public native JavaScriptObject getError() /*-{
			return this.getError();
		}-*/;
	}

	/**
	 * Verify if FileReader is supported by the current browser.
	 * @return <code>true</code> if supported, <code>false</code> otherwise.
	 */
	public native static boolean isSupported() /*-{
		return typeof FileReader != 'undefined';
	}-*/;
	
	private final NativeFileReader nativeFileReader;
	private final List<LoadFileListener> eventHandlers = new ArrayList<LoadFileListener>();

	public FileReader() {
		this.nativeFileReader = createNativeFileReader();
		registerEvents(nativeFileReader);
	}
	
	private native void registerEvents(NativeFileReader fileReader) /*-{
		if(typeof $wnd.Object.getPrototypeOf != 'undefined') {
			$wnd.Object.getPrototypeOf(this).handleEvent = function(event) {
				switch(event.type) {
					case 'load':
						this.@org.sigmah.offline.fileapi.FileReader::fireLoad()();
						break;
					case 'loadstart':
						break;
					case 'loadend':
						break;
					case 'progress':
						this.@org.sigmah.offline.fileapi.FileReader::fireProgress(Lorg/sigmah/offline/event/ProgressEvent;)(event);
						break;
					case 'abort':
						break;
					case 'error':
						this.@org.sigmah.offline.fileapi.FileReader::fireError()();
						break;
					default:
						break;
				}
			};
			fileReader.addEventListener('load', this);
			fileReader.addEventListener('loadstart', this);
			fileReader.addEventListener('loadend', this);
			fileReader.addEventListener('progress', this);
			fileReader.addEventListener('abort', this);
			fileReader.addEventListener('error', this);
		}
	}-*/;
	
	private native NativeFileReader createNativeFileReader() /*-{
		return new FileReader();
	}-*/;
	
	public void readAsText(Blob blob, String encoding) {
		nativeFileReader.readAsText(blob, encoding);
	}
	
	public void readAsArrayBuffer(Blob blob) {
		nativeFileReader.readAsArrayBuffer(blob);
	}
	
	public void readAsDataURL(Blob blob) {
		nativeFileReader.readAsDataURL(blob);
	}
	
	public String getResultAsString() {
		return nativeFileReader.getResultAsString();
	}
	
	public ArrayBuffer getResultAsArrayBuffer() {
		return nativeFileReader.getResultAsArrayBuffer();
	}
	
	public void addLoadFileListener(LoadFileListener listener) {
		eventHandlers.add(listener);
	}
	
	protected void fireLoad() {
		for(int index = eventHandlers.size() - 1; index >= 0; index--) {
			eventHandlers.get(index).onLoad();
		}
	}
	
	protected void fireProgress(ProgressEvent event) {
		for(int index = eventHandlers.size() - 1; index >= 0; index--) {
			eventHandlers.get(index).onProgress(event);
		}
	}
	
	protected void fireError() {
		for(int index = eventHandlers.size() - 1; index >= 0; index--) {
			eventHandlers.get(index).onError();
		}
	}
}
