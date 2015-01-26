package org.sigmah.offline.dao;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;
import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.AuthenticationJS;
import org.sigmah.shared.command.result.Authentication;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class AuthenticationAsyncDAO extends AbstractAsyncDAO<Authentication> {
	
	public static final int DEFAULT_ID = 1;

	@Override
	public void saveOrUpdate(final Authentication t, final AsyncCallback<Authentication> callback, final Transaction transaction) {
		final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());
		
		final AuthenticationJS authenticationJS = AuthenticationJS.toJavaScript(t);
		authenticationJS.setId(DEFAULT_ID);
		
		final Request request = objectStore.put(authenticationJS);
        request.addCallback(new AsyncCallback<Request>() {
            @Override
            public void onFailure(Throwable caught) {
                
                Log.error("Error while saving authentication.");
				if(callback != null) {
					callback.onFailure(caught);
				}
            }

            @Override
            public void onSuccess(Request result) {
                Log.trace("Authentication has been successfully saved.");
				
				if(callback != null) {
					callback.onSuccess(t);
				}
            }
        });
	}
	
	public void get(AsyncCallback<Authentication> callback) {
		get(DEFAULT_ID, callback);
	}

	@Override
	public void get(int id, final AsyncCallback<Authentication> callback, Transaction transaction) {
		final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());
		
		objectStore.get(id).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Request request) {
                final AuthenticationJS authenticationJS = request.getResult();
				final Authentication authentication;
				
				if(authenticationJS != null) {
					authentication = authenticationJS.toAuthentication();
				} else {
					authentication = null;
				}
				
				callback.onSuccess(authentication);
            }
        });
	}

	@Override
	public Store getRequiredStore() {
		return Store.AUTHENTICATION;
	}
	
}
