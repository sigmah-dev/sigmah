package org.sigmah.offline.appcache;

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

import org.sigmah.offline.event.JavaScriptEvent;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Wrapper around the JavaScript API of ApplicationCache.
 *
 * ApplicationCache is an HTML5 API. It is the basis of the offline mode.
 *
 * ApplicationCache purpose is to cache every file required by the application.
 * They will then be accessible even when the user is offline.
 *
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
		this.onchecking = $entry(function(e) {
			handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
		});
	}-*/;
	
	public native void onError(JavaScriptEvent handler) /*-{
		this.onerror = $entry(function(e) {
			handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
		});
	}-*/;
	
	public native void onNoUpdate(JavaScriptEvent handler) /*-{
		this.onnoupdate = $entry(function(e) {
			handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
		});
	}-*/;
	
	public native void onDownloading(JavaScriptEvent handler) /*-{
		this.ondownloading = $entry(function(e) {
			handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
		});
	}-*/;
	
	public native void onProgress(JavaScriptEvent handler) /*-{
		this.onprogress = $entry(function(e) {
			handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
		});
	}-*/;
	
	public native void onUpdateReady(JavaScriptEvent handler) /*-{
		this.onupdateready = $entry(function(e) {
			handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
		});
	}-*/;
	
	public native void onCached(JavaScriptEvent handler) /*-{
		this.oncached = $entry(function(e) {
			handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
		});
	}-*/;
	
	public native void onObsolete(JavaScriptEvent handler) /*-{
		this.onObsolete = $entry(function(e) {
			handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
		});
	}-*/;
}
