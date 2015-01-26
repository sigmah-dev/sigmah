package org.sigmah.offline.appcache;

import org.sigmah.offline.event.JavaScriptEvent;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Wrapper around the JavaScript API of ApplicationCache.
 * <p/>
 * ApplicationCache is an HTML5 API. It is the basis of the offline mode.
 * <p/>
 * ApplicationCache purpose is to cache every file required by the application.
 * They will then be accessible even when the user is offline.
 * <p/>
 * The file list is specified by a Manifest.
 * 
 * @see org.sigmah.linker.ManifestGenerationLinker
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class ApplicationCache extends JavaScriptObject {
	
	/**
	 * List of possible status of ApplicationCache.
	 */
	public enum Status {
		UNCACHED, IDLE, CHECKING, DOWNLOADING, UPDATEREADY, OBSOLETE, ERROR;
	}
	
	protected ApplicationCache() {
	}
	
	/**
	 * Verify if ApplicationCache is supported by the current browser.
	 * @return <code>true</code> if supported, <code>false</code> otherwise.
	 */
	public static boolean isSupported() {
		return getApplicationCache() != null;
	}
	
	public static native ApplicationCache getApplicationCache() /*-{
		return $wnd.applicationCache;
	}-*/;
	
	private native int getStatusNumber() /*-{
		return this.status;
	}-*/;
	
	public Status getStatus() {
		return Status.values()[getStatusNumber()];
	}
	
	public native void update() /*-{
		this.update();
	}-*/;
	
	public native void abort() /*-{
		this.abort();
	}-*/;
	
	public native void swapCache() /*-{
		this.swapCache();
	}-*/;
	
	public native void onChecking(JavaScriptEvent handler) /*-{
		this.onchecking = handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;);
	}-*/;
	
	public native void onError(JavaScriptEvent handler) /*-{
		this.onerror = handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;);
	}-*/;
	
	public native void onNoUpdate(JavaScriptEvent handler) /*-{
		this.onnoupdate = handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;);
	}-*/;
	
	public native void onDownloading(JavaScriptEvent handler) /*-{
		this.ondownloading = handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;);
	}-*/;
	
	public native void onProgress(JavaScriptEvent handler) /*-{
		this.onprogress = handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;);
	}-*/;
	
	public native void onUpdateReady(JavaScriptEvent handler) /*-{
		this.onupdateready = handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;);
	}-*/;
	
	public native void onCached(JavaScriptEvent handler) /*-{
		this.oncached = handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;);
	}-*/;
	
	public native void onObsolete(JavaScriptEvent handler) /*-{
		this.onObsolete = handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;);
	}-*/;
}
