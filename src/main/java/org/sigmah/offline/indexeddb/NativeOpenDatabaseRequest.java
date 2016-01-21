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

import java.util.ArrayList;
import java.util.List;

import org.sigmah.offline.event.JavaScriptEvent;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Request to open an IndexedDB database.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class NativeOpenDatabaseRequest<S extends Enum<S> & Schema> extends Request implements OpenDatabaseRequest<S> {
	
	private final List<JavaScriptEvent> upgradeNeededHandlers = new ArrayList<JavaScriptEvent>();
	private final List<JavaScriptEvent> blockedHandlers = new ArrayList<JavaScriptEvent>();
	
	private final Class<S> stores;
	
	private boolean openFailed;

	NativeOpenDatabaseRequest(IDBOpenDBRequest request, Class<S> stores) {
		super(request);
		this.stores = stores;
		registerEvents(request);
	}
	
	private native void registerEvents(IDBRequest request) /*-{
		if(typeof $wnd.Object.getPrototypeOf != 'undefined') {
			$wnd.Object.getPrototypeOf(this).handleEvent = function(event) {
				switch(event.type) {
					case 'success':
						this.@org.sigmah.offline.indexeddb.NativeOpenDatabaseRequest::fireSuccess(Lcom/google/gwt/core/client/JavaScriptObject;)(event);
						break;
					case 'error':
						this.@org.sigmah.offline.indexeddb.NativeOpenDatabaseRequest::fireError(Lcom/google/gwt/core/client/JavaScriptObject;)(event);
						break;
					case 'upgradeneeded':
						this.@org.sigmah.offline.indexeddb.NativeOpenDatabaseRequest::fireUpgradeNeeded(Lcom/google/gwt/core/client/JavaScriptObject;)(event);
						break;
					case 'blocked':
						this.@org.sigmah.offline.indexeddb.NativeOpenDatabaseRequest::fireBlocked(Lcom/google/gwt/core/client/JavaScriptObject;)(event);
						break;
					default:
						break;
				}
			};
			request.addEventListener('upgradeneeded', this);
			request.addEventListener('blocked', this);
		}
	}-*/;

	@Override
	public Database<S> getResult() {
		if(!openFailed) {
			return new Database<S>((IDBDatabase) super.getResult(), stores);
		} else {
			return null;
		}
	}
    
    @Override
    public void addSuccessHandler(final JavaScriptEvent successHandler) {
        addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error while opening an IndexedDB database.", caught);
				
				openFailed = true;
				successHandler.onEvent(null);
            }

            @Override
            public void onSuccess(Request result) {
                successHandler.onEvent(null);
            }
        });
    }
	
	public void addUpgradeNeededHandler(JavaScriptEvent<IDBVersionChangeEvent> handler) {
		upgradeNeededHandlers.add(handler);
	}
	
	protected void fireUpgradeNeeded(JavaScriptObject event) {
		for(int index = upgradeNeededHandlers.size() - 1; index >= 0; index--) {
			upgradeNeededHandlers.get(index).onEvent(event);
		}
	}
	
	public void addBlockedHandler(JavaScriptEvent handler) {
		blockedHandlers.add(handler);
	}
	
	protected void fireBlocked(JavaScriptObject event) {
		for(int index = blockedHandlers.size() - 1; index >= 0; index--) {
			blockedHandlers.get(index).onEvent(event);
		}
	}
}
