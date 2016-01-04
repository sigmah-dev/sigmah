package org.sigmah.offline.dao;

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
	
	@Inject
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

	public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
		this.authenticationProvider = authenticationProvider;
	}
	
	protected void openTransaction(Transaction.Mode mode, OpenTransactionHandler<S> handler) {
        final OpenDatabaseRequest<S> openDatabaseRequest = openDatabase();
        
        handler.setMode(mode);
        handler.setOpenDatabaseRequest(openDatabaseRequest);
        handler.setStores(getRequiredStores());
        
		openDatabaseRequest.addSuccessHandler(handler);
    }
	
	public Set<S> getRequiredStores() {
		final HashSet<BaseAsyncDAO> dependencies = new HashSet<BaseAsyncDAO>();
		dependencies.add(this);
		
		int previousSize = 0;
		int size = dependencies.size();
		
        // Expands dependencies by adding their dependencies until no new dependency is added.
		while(previousSize != size) {
			previousSize = size;
			
            final HashSet<BaseAsyncDAO> entries = new HashSet<BaseAsyncDAO>(dependencies);
			for(final BaseAsyncDAO entry : entries) {
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

	public Collection<BaseAsyncDAO<S>> getDependencies() {
		return Collections.EMPTY_LIST;
	}
	
}
