package org.sigmah.offline.indexeddb;

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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import org.sigmah.offline.event.JavaScriptEvent;
import org.sigmah.offline.sync.UpdateDates;
import org.sigmah.shared.command.result.Authentication;

/**
 * Main class for creating and opening IndexedDB databases.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class IndexedDB {
	
    private static final LinkedList<AlreadyOpenedDatabaseRequest> LISTENER_QUEUE = new LinkedList<AlreadyOpenedDatabaseRequest>();
	
    private static State state = State.CLOSED;
    private static Database<Store> userDatabase;
	
	/**
	 * Verify if IndexedDB is supported by the current browser.
	 * 
	 * @return <code>true</code> if supported, <code>false</code> otherwise.
	 */
	public static native boolean isSupported() /*-{
		return typeof $wnd.indexedDB != 'undefined';
	}-*/;
	
	/**
	 * Open or create a database named with the e-mail address of the given user.
	 * 
	 * @param authentication Information about the current user.
	 * @return A request to open the database.
	 */
	public static OpenDatabaseRequest<Store> openUserDatabase(Authentication authentication) {
		return openUserDatabase(authentication.getUserEmail());
	}
    
	/**
	 * Removes the database named with the e-mail address of the given user.
	 * 
	 * @param authentication Information about the current user.
	 * @return A request to delete the database.
	 */
    public static OpenDatabaseRequest deleteUserDatabase(Authentication authentication) {
		closeDatabase();
        return deleteUserDatabase(authentication.getUserEmail());
    }
	
	/**
	 * Open or create a database with the given name.
	 * 
	 * @param email Name of the database to open or create.
	 * @return A request to open the database.
	 */
	private static OpenDatabaseRequest<Store> openUserDatabase(final String email) {
        if (userDatabase != null) {
			if (userDatabase.getName().equals(email)) {
				return new AlreadyOpenedDatabaseRequest<Store>(userDatabase);
			} else {
				// Switching database.
				state = State.CLOSED;
				userDatabase.close();
				userDatabase = null;
			}
        }
        
		if(email == null) {
			return new NoopDatabaseRequest<Store>();
		}
		if(!isSupported()) {
			Log.warn("IndexedDB is not supported by this web browser.");
			return new NoopDatabaseRequest<Store>();
		}
		if(!GWT.isProdMode()) {
			Log.info("IndexedDB is unavailable in Hosted Mode.");
			return new NoopDatabaseRequest<Store>();
		}
        
        switch(state) {
            case CLOSED:
                state = State.OPENING;
                
                final IndexedDB indexedDB = new IndexedDB();
                final NativeOpenDatabaseRequest<Store> openDatabaseRequest = indexedDB.open(email, Store.class);

                openDatabaseRequest.addSuccessHandler(new JavaScriptEvent() {
                    @Override
                    public void onEvent(JavaScriptObject event) {
                        userDatabase = openDatabaseRequest.getResult();
						
						if (userDatabase != null) {
							state = State.OPENED;
						} else {
							state = State.ERROR;
						}
						
						for(final AlreadyOpenedDatabaseRequest<Store> listener : LISTENER_QUEUE) {
							listener.setResult(userDatabase);
						}
                    }
                });
                
                return openDatabaseRequest;
                
            case OPENING:
                final AlreadyOpenedDatabaseRequest<Store> listener = new AlreadyOpenedDatabaseRequest<Store>();
                LISTENER_QUEUE.add(listener);
                return listener;
                
            case OPENED:
                return new AlreadyOpenedDatabaseRequest<Store>(userDatabase);
                
            default:
                return new NoopDatabaseRequest<Store>();
        }
	}
	
	/**
	 * Upgrade the given database.
	 * 
	 * @param <S> Schema type.
	 * @param database Database to upgrade.
	 * @param event Version change event.
	 * @param name Name of the database.
	 */
	private static <S extends Enum<S> & Schema> void upgradeDatabase(final Database<S> database, final IDBVersionChangeEvent event, final String name) {
		Log.info("Local IndexedDB database is being updated from version " + event.getOldVersion() + " to version " + Stores.getVersion(database.getSchema()) + '.');
		UpdateDates.setDatabaseUpdateDate(name, null);
		
		for(final String store : database.getObjectStoreNames()) {
			database.deleteObjectStore(store);
		}

		final Set<S> stores = database.getObjectStores();

		for(final S store : database.getSchema().getEnumConstants()) {
			if (!stores.contains(store) && store.isEnabled()) {
				final ObjectStore objectStore = database.createObjectStore(store, "id", store.isAutoIncrement());
				for (final Map.Entry<String, String> index : store.getIndexes().entrySet()) {
					objectStore.createIndex(index.getKey(), index.getValue());
				}
			}
		}
	}
    
	/**
	 * Removes the database with the given name.
	 * 
	 * @param email Name of the database to remove.
	 * @return A request to delete the database.
	 */
    private static OpenDatabaseRequest<Store> deleteUserDatabase(String email) {
        if(email == null) {
			return new NoopDatabaseRequest<Store>();
		}
		if(!isSupported()) {
			Log.warn("IndexedDB is not supported by this web browser.");
			return new NoopDatabaseRequest<Store>();
		}
        
        final IndexedDB indexedDB = new IndexedDB();
        return indexedDB.deleteDatabase(email, Store.class);
    }
	
	/**
	 * Close and release the currently opened user database.
	 */
	public static void closeDatabase() {
		if(userDatabase != null) {
			userDatabase.close();
			state = State.CLOSED;
		}
	}
	
	/**
	 * Native instance of IndexedDB.
	 */
	private final NativeIndexedDB nativeIndexedDB;
	
	/**
	 * Creates a new wrapper around IndexedDB.
	 * 
	 * @throws UnsupportedOperationException If IndexedDB is not supported by the web browser.
	 */
	public IndexedDB() throws UnsupportedOperationException {
		if(!isSupported()) {
			throw new UnsupportedOperationException("IndexedDB is not supported by this web browser.");
		}
		this.nativeIndexedDB = NativeIndexedDB.getIndexedDB();
	}

	/**
	 * Create a native request to open a designated IndexedDB database.
	 * 
	 * @param <S> Schema type.
	 * @param name Name of the database to open.
	 * @param stores Schema type class.
	 * @return An open database request.
	 */
	public <S extends Enum<S> & Schema> NativeOpenDatabaseRequest<S> open(final String name, final Class<S> stores) {
		final NativeOpenDatabaseRequest<S> request = new NativeOpenDatabaseRequest<S>(nativeIndexedDB.open(name, Stores.getVersion(stores)), stores);
		
		request.addUpgradeNeededHandler(new JavaScriptEvent<IDBVersionChangeEvent>() {

			@Override
			public void onEvent(IDBVersionChangeEvent event) {
				upgradeDatabase(request.getResult(), event, name);
			}
		});
		
		return request;
	}
    
	/**
	 * Create a native request to delete the given database.
	 * 
	 * @param name Name of the database to delete.
	 * @return An open database request.
	 */
    public <S extends Enum<S> & Schema> NativeOpenDatabaseRequest<S> deleteDatabase(String name, final Class<S> stores) {
        return new NativeOpenDatabaseRequest<S>(nativeIndexedDB.deleteDatabase(name), stores);
    }
    
	/**
	 * Database states.
	 */
    public static enum State {
		
        CLOSED, OPENING, OPENED, ERROR;
		
    }
}
