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
import org.sigmah.offline.indexeddb.IndexedDB;
import org.sigmah.offline.indexeddb.OpenDatabaseRequest;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.shared.command.result.Authentication;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public abstract class BaseAsyncDAO {
	
	@Inject
	private AuthenticationProvider authenticationProvider;

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
	
	public boolean isAnonymous() {
        return authenticationProvider.isAnonymous();
    }

	public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
		this.authenticationProvider = authenticationProvider;
	}
	
	protected void openTransaction(Transaction.Mode mode, OpenTransactionHandler handler) {
        final OpenDatabaseRequest openDatabaseRequest = IndexedDB.openUserDatabase(getAuthentication());
        
        handler.setMode(mode);
        handler.setOpenDatabaseRequest(openDatabaseRequest);
        handler.setStores(getRequiredStores());
        
		openDatabaseRequest.addSuccessHandler(handler);
    }
	
	public Set<Store> getRequiredStores() {
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
		
		final EnumSet<Store> stores = EnumSet.noneOf(Store.class);
		for(final BaseAsyncDAO dependency : dependencies) {
			stores.add(dependency.getRequiredStore());
		}
		return stores;
	}

	public Collection<BaseAsyncDAO> getDependencies() {
		return Collections.EMPTY_LIST;
	}
	
	public abstract Store getRequiredStore();
}
