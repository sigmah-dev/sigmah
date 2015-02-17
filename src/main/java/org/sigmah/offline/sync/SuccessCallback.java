package org.sigmah.offline.sync;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public abstract class SuccessCallback<T> implements AsyncCallback<T> {

	@Override
	public void onFailure(Throwable caught) {
	}
	
}
