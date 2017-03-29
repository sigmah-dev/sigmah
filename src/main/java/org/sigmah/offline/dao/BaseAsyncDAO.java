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

import com.google.gwt.storage.client.Storage;
import com.google.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import org.sigmah.client.security.AuthenticationProvider;
import org.sigmah.offline.dispatch.LocalDispatchServiceAsync;
import org.sigmah.offline.indexeddb.OpenDatabaseRequest;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.offline.indexeddb.Schema;

/**
 * Base class for every asynchronous DAO.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @param <S> Type of the schema containing the stores used by this DAO.
 */
public abstract class BaseAsyncDAO<S extends Enum<S> & Schema> {
	
	
	private AuthenticationProvider authenticationProvider;
	
	/**
	 * Open the required IndexedDB database.
	 * 
	 * @return An open database request.
	 */
	public abstract OpenDatabaseRequest<S> openDatabase();
	
	/**
	 * Get the store required by this DAO.
	 * 
	 * @return Required store.
	 */
	public abstract S getRequiredStore();
	
	/**
	 * Get the schema class required by this DAO.
	 * 
	 * @return Schema class.
	 */
	public abstract Class<S> getSchema();
	
	/**
	 * Retrieves the current authentication.
	 * 
	 * @return The current authentication.
	 */
	public Authentication getAuthentication() {
		final Authentication authentication = authenticationProvider.get();
		
		if(authentication.getUserEmail() == null) {
			// Search the last logged user in the users database
			final Storage storage = Storage.getLocalStorageIfSupported();
			final String email = storage.getItem(LocalDispatchServiceAsync.LAST_USER_ITEM);

			authentication.setUserEmail(email);
		}
		
		return authentication;
	}
	
	/**
	 * Returns <code>true</code> if the current user is anonymous.
	 * 
	 * @return <code>true</code> if the current user is anonymous, 
	 * <code>false</code> otherwise.
	 */
	public boolean isAnonymous() {
        return authenticationProvider.isAnonymous();
    }

	/**
	 * Defines the authentication provider used by this DAO.
	 * 
	 * @param authenticationProvider 
	 *			Authentication provider to use.
	 */
	public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
		this.authenticationProvider = authenticationProvider;
	}
	
	/**
	 * Execute the given handler in a new transaction opened by locking the 
	 * stores required by this DAO.
	 * 
	 * @param mode
	 *			Opening mode (read-only or read-write).
	 * @param handler 
	 *			Handler executed in a new transaction.
	 */
	public void openTransaction(final Transaction.Mode mode, final OpenTransactionHandler<S> handler) {
        final OpenDatabaseRequest<S> openDatabaseRequest = openDatabase();
        
        handler.setMode(mode);
        handler.setOpenDatabaseRequest(openDatabaseRequest);
        handler.setStores(getRequiredStores());
        
		openDatabaseRequest.addSuccessHandler(handler);
    }
	
	/**
	 * Returns all required stores for this DAO by computing dependencies of
	 * every dependencies.
	 * 
	 * @return A set of every required stores to create a new transaction.
	 */
	public Set<S> getRequiredStores() {
		final HashSet<BaseAsyncDAO<S>> dependencies = new HashSet<BaseAsyncDAO<S>>();
		dependencies.add(this);
		
		int previousSize = 0;
		int size = dependencies.size();
		
        // Expands dependencies by adding their dependencies until no new dependency is added.
		while (previousSize != size) {
			previousSize = size;
			
            final HashSet<BaseAsyncDAO<S>> entries = new HashSet<BaseAsyncDAO<S>>(dependencies);
			for(final BaseAsyncDAO<S> entry : entries) {
                dependencies.addAll(entry.getDependencies());
			}
			
			size = dependencies.size();
		}
		
		final EnumSet<S> requiredStores = EnumSet.noneOf(getSchema());
		for(final BaseAsyncDAO<S> dependency : dependencies) {
			requiredStores.add(dependency.getRequiredStore());
		}
		return requiredStores;
	}

	/**
	 * Returns a collection of the DAO required to save or read an object with
	 * this DAO.
	 * 
	 * @return A collection of the required DAO.
	 */
	public Collection<BaseAsyncDAO<S>> getDependencies() {
		return Collections.<BaseAsyncDAO<S>>emptyList();
	}
	
}
