package org.sigmah.offline.dao;

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
        if(callback != null) {
            callback.onSuccess(result);
        }
	}
    
}
