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
