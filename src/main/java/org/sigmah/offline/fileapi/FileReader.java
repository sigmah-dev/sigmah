package org.sigmah.offline.fileapi;

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
