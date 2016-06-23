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

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * IndexedDB Transaction.
 *
 * Active only while operations are done on the transaction. Closed
 * automatically when unused.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Transaction<S extends Enum<S> & Schema> {
    /**
     * Allowed operations on a transaction.
     */
	public static enum Mode {
		READ_ONLY("readonly"), READ_WRITE("readwrite");
        
        private final String argument;

        private Mode(String argument) {
            this.argument = argument;
        }

        public String getArgument() {
            return argument;
        }
	}

	private final IDBTransaction nativeTransaction;
	private final ObjectCache objectCache;
	
	private final Set<S> stores;
	private final Mode mode;
	
	Transaction(IDBTransaction transaction, Mode mode, Collection<S> stores) {
		this.nativeTransaction = transaction;
		this.objectCache = new ObjectCache();
		this.stores = EnumSet.copyOf(stores);
		this.mode = mode;
	}
	
	private ObjectStore getObjectStore(String name) {
		return new ObjectStore(nativeTransaction.getObjectStore(name));
	}
	
	public ObjectStore getObjectStore(S store) {
		return getObjectStore(store.name());
	}

	public ObjectCache getObjectCache() {
		return objectCache;
	}

	public Mode getMode() {
		return mode;
	}

	public Set<S> getStores() {
		return stores;
	}

	public <T> boolean useObjectFromCache(Class<T> clazz, int id, AsyncCallback<T> callback) {
		final T fromCache = objectCache.get(clazz, id);
		if(fromCache != null) {
			callback.onSuccess(fromCache);
			return true;
		}
		return false;
	}
	
	/**
	 * Run a dummy request on the current transaction to extends its validity.
	 */
	public void ping() {
		final S randomStore = stores.iterator().next();
		final ObjectStore pingObjectStore = getObjectStore(randomStore);
        
		pingObjectStore.get(0).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.trace("IndexedDB PING failed", caught);
            }

            @Override
            public void onSuccess(Request result) {
                Log.trace("IndexedDB PING successful");
            }
        });
	}
}
