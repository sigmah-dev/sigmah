package org.sigmah.offline.indexeddb;

import org.sigmah.offline.event.JavaScriptEvent;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
final class IDBOpenDBRequest extends IDBRequest<IDBDatabase> {
	protected IDBOpenDBRequest() {
	}
	
	/**
	 * The event handler for the blocked event.
	 * This event is triggered when the upgradeneeded should be triggered 
	 * because of a version change but the database is still in use (ie not 
	 * closed) somewhere, even after the versionchange event was sent.
	 * @param handler 
	 */
	public native void setOnBlocked(JavaScriptEvent handler) /*-{
		this.onblocked = handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;);
	}-*/;
	
	/**
	 * The event handler for the upgradeneeded event. Fired when a database of a
	 * bigger version number than the existing stored database is loaded.
	 * @param handler 
	 */
	public native void setOnUpgradeNeeded(JavaScriptEvent handler) /*-{
		this.onupgradeneeded = handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;);
	}-*/;
}
