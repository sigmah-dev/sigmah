package org.sigmah.offline.dao;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import java.util.Collection;
import org.sigmah.offline.event.JavaScriptEvent;
import org.sigmah.offline.indexeddb.Database;
import org.sigmah.offline.indexeddb.IndexedDBException;
import org.sigmah.offline.indexeddb.OpenDatabaseRequest;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;

/**
 * Utility class to centralize code used to open a transaction.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public abstract class OpenTransactionHandler implements JavaScriptEvent {
    
    public static void openTransaction(OpenDatabaseRequest openDatabaseRequest, Transaction.Mode mode, Collection<Store> stores, OpenTransactionHandler handler) {
        handler.stores = stores;
        handler.openDatabaseRequest = openDatabaseRequest;
        handler.mode = mode;
        openDatabaseRequest.addSuccessHandler(handler);
    }
    
    private Collection<Store> stores;
    private OpenDatabaseRequest openDatabaseRequest;
    private Transaction.Mode mode;

    @Override
    public void onEvent(JavaScriptObject event) {
        final Database database = openDatabaseRequest.getResult();
		if(database != null) {
			try {
				final Transaction transaction = database.getTransaction(mode, stores);
				onTransaction(transaction);
				
			} catch(IndexedDBException e) {
				Log.warn("An error occured while trying to open an IndexedDB transaction.", e);
			}
		}
    }
    
    public abstract void onTransaction(Transaction transaction);

    public void setStores(Collection<Store> stores) {
        this.stores = stores;
    }

    public void setMode(Transaction.Mode mode) {
        this.mode = mode;
    }

    public void setOpenDatabaseRequest(OpenDatabaseRequest openDatabaseRequest) {
        this.openDatabaseRequest = openDatabaseRequest;
    }
    
}
