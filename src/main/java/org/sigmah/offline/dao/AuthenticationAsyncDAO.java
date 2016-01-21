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
