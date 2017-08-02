package org.sigmah.offline.indexeddb;

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
		this.onblocked = $entry(function(e) {
			handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
		});
	}-*/;
	
	/**
	 * The event handler for the upgradeneeded event. Fired when a database of a
	 * bigger version number than the existing stored database is loaded.
	 * @param handler 
	 */
	public native void setOnUpgradeNeeded(JavaScriptEvent handler) /*-{
		this.onupgradeneeded = $entry(function(e) {
			handler.@org.sigmah.offline.event.JavaScriptEvent::onEvent(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
		});
	}-*/;
}
