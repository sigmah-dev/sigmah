package org.sigmah.offline.indexeddb;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * IndexedDB Transaction.
 * <p/>
 * Active only while operations are done on the transaction. Closed
 * automatically when unused.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Transaction {
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
	
	private final Set<Store> stores;
	private final Mode mode;
	
	Transaction(IDBTransaction transaction, Mode mode, Collection<Store> stores) {
		this.nativeTransaction = transaction;
		this.objectCache = new ObjectCache();
		this.stores = EnumSet.copyOf(stores);
		this.mode = mode;
	}
	
	private ObjectStore getObjectStore(String name) {
		return new ObjectStore(nativeTransaction.getObjectStore(name));
	}
	
	public ObjectStore getObjectStore(Store store) {
		return getObjectStore(store.name());
	}

	public ObjectCache getObjectCache() {
		return objectCache;
	}

	public Mode getMode() {
		return mode;
	}

	public Set<Store> getStores() {
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
		final Store randomStore = stores.iterator().next();
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
