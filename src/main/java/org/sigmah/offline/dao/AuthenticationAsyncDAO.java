package org.sigmah.offline.dao;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.js.AuthenticationJS;
import org.sigmah.shared.command.result.Authentication;

/**
 * Asynchronous DAO for saving and loading <code>Authentication</code> objects.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class AuthenticationAsyncDAO extends AbstractUserDatabaseAsyncDAO<Authentication, AuthenticationJS> {
	
	public void get(AsyncCallback<Authentication> callback) {
		get(AuthenticationJS.DEFAULT_ID, callback);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Store getRequiredStore() {
		return Store.AUTHENTICATION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthenticationJS toJavaScriptObject(Authentication t) {
		return AuthenticationJS.toJavaScript(t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Authentication toJavaObject(AuthenticationJS js) {
		return js.toAuthentication();
	}
	
}
