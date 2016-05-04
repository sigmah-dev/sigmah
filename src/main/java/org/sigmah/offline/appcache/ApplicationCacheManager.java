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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sigmah.offline.event.JavaScriptEvent;
import org.sigmah.offline.event.ProgressEvent;
import org.sigmah.offline.sync.UpdateDates;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Window;

/**
 * Manager of ApplicationCache.
 *
 * This class adds Java properties to ApplicationCache.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ApplicationCacheManager {
    
	/**
	 * List of handlers.
	 */
	private static List<ApplicationCacheEventHandler> eventHandlers;
	
	/**
	 * Adds an handler for ApplicationCache events.
	 * 
	 * @param handler Handler to add.
	 */
	public static void addHandler(ApplicationCacheEventHandler handler) {
		ensureHandlers();
		eventHandlers.add(handler);
		
		handler.onStatusChange(ApplicationCache.getApplicationCache().getStatus());
	}
	
	/**
	 * Swaps the current cache and displays it by reloading the web page.
	 */
	public static void swapCacheAndReload() {
		ApplicationCache.getApplicationCache().swapCache();
		Window.Location.reload();
	}
    
	/**
	 * Ensures that the manager is listening to ApplicationCache events.
	 *
	 * If the manager is already listening to ApplicationCache, this method does
	 * nothing.
	 */
    public static void ensureHandlers() {
        if(eventHandlers == null) {
            final ApplicationCache applicationCache = ApplicationCache.getApplicationCache();
            
			final JavaScriptEvent nativeHandler = new JavaScriptEvent() {
				@Override
				public void onEvent(JavaScriptObject event) {
					fireStatus();
				}
			};
			
			applicationCache.onCached(nativeHandler);
			applicationCache.onChecking(nativeHandler);
			applicationCache.onDownloading(nativeHandler);
			applicationCache.onError(new JavaScriptEvent() {
				@Override
				public void onEvent(JavaScriptObject event) {
					fireError();
				}
			});
			applicationCache.onNoUpdate(nativeHandler);
			applicationCache.onObsolete(nativeHandler);
			applicationCache.onProgress(new JavaScriptEvent() {
				@Override
				public void onEvent(JavaScriptObject event) {
					final ProgressEvent progressEvent = (ProgressEvent)event;
					
					if(progressEvent.isLengthComputable()) {
						fireProgress(progressEvent);
					} else {
						fireStatus();
					}
				}
			});
			applicationCache.onUpdateReady(new JavaScriptEvent() {
                @Override
                public void onEvent(JavaScriptObject event) {
                    setUpdateDate(new Date());
                    fireStatus();
                }
            });
			
			eventHandlers = new ArrayList<ApplicationCacheEventHandler>();
		}
    }
    
    public static Date getUpdateDate() {
        return UpdateDates.getSigmahUpdateDate();
    }
    
    private static void setUpdateDate(Date date) {
        UpdateDates.setSigmahUpdateDate(date);
    }
	
	private static void fireStatus() {
		final ApplicationCache.Status status = ApplicationCache.getApplicationCache().getStatus();
		for(int index = eventHandlers.size() - 1; index >= 0; index--) {
			eventHandlers.get(index).onStatusChange(status);
		}
	}
	
	private static void fireError() {
		for(int index = eventHandlers.size() - 1; index >= 0; index--) {
			eventHandlers.get(index).onStatusChange(ApplicationCache.Status.ERROR);
		}
	}
	
	private static void fireProgress(ProgressEvent event) {
		for(int index = eventHandlers.size() - 1; index >= 0; index--) {
			eventHandlers.get(index).onProgress(event.getLoaded(), event.getTotal());
		}
	}
}
