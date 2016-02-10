package org.sigmah.offline.dao;

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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Timer;
import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Handle simultaneous requests and call a given callback when every request
 * is done.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @param <T> Result type
 */
public class RequestManager<T> {
    
	private final ArrayList<Boolean> status = new ArrayList<Boolean>();
	private Throwable caught;
	private boolean preparing;
	private boolean errorSent;
	
	private final T result;
	private final AsyncCallback<T> callback;
	
	private Timer timer;
	
	public RequestManager(T result, AsyncCallback<T> callback) {
		this.result = result;
		this.callback = callback;
		this.preparing = true;
	}

	public void setPreparing(boolean preparing) {
		this.preparing = preparing;
		
		if(!preparing) {
			callSuccessIfAllRequestsAreSuccessful();
		}
	}
	
	public void ready() {
		timer = new Timer() {

			@Override
			public void run() {
				final StringBuilder errorBuilder = new StringBuilder();
				
				int index = 0;
				for(final Boolean entry : status) {
					if(entry == null || entry.equals(Boolean.FALSE)) {
						errorBuilder.append('#').append(index).append(" : ").append(entry).append("; ");
					}
					index++;
				}
				
				if(errorBuilder.length() == 0) {
					Log.warn("RequestManager : callSuccess required ?");
				} else {
					Log.warn("RequestManager Timeout : " + errorBuilder.toString());
				}
			}
		};
		timer.schedule(3000);
		
		setPreparing(false);
	}
	
	public int prepareRequest() {
		final int id = status.size();
		status.add(null);
		
		return id;
	}
	
	public void setRequestSuccess(int request) {
		status.set(request, Boolean.TRUE);
		callSuccessIfAllRequestsAreSuccessful();
	}
	
	public void setRequestFailure(int request, Throwable caught) {
		this.caught = caught;
		status.set(request, Boolean.FALSE);
		
		errorSent = true;
        if(callback != null) {
            callback.onFailure(caught);
        }
	}
	
	private void callSuccessIfAllRequestsAreSuccessful() {
		if(preparing || callback == null || errorSent) {
			return;
		}
		
		for(Boolean entry : status) {
			if(entry == null) {
				return;
				
			} else if(!entry) {
				errorSent = true;
				callback.onFailure(caught);
				return;
			}
		}
		if(timer != null) {
			timer.cancel();
			timer = null;
		}
        if(callback != null) {
            callback.onSuccess(result);
        }
	}
    
}
