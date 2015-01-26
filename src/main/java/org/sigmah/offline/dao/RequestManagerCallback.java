package org.sigmah.offline.dao;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @param <M> Object handled by the request manager
 * @param <R> Result from the AsyncCallback
 */
public abstract class RequestManagerCallback<M, R> implements AsyncCallback<R> {

	private final int requestId;
	private final RequestManager<M> requestManager;

	public RequestManagerCallback(RequestManager<M> requestManager) {
		this.requestId = requestManager.prepareRequest();
		this.requestManager = requestManager;
	}
    
	public RequestManagerCallback(RequestManager<M> requestManager, int requestId) {
		this.requestId = requestId;
		this.requestManager = requestManager;
	}

	@Override
	public void onFailure(Throwable caught) {
		requestManager.setRequestFailure(requestId, caught);
	}

	@Override
	public void onSuccess(R result) {
		onRequestSuccess(result);
		requestManager.setRequestSuccess(requestId);
	}

	public abstract void onRequestSuccess(R result);
}
