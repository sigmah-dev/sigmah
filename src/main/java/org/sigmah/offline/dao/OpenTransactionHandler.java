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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import java.util.Collection;
import org.sigmah.offline.event.JavaScriptEvent;
import org.sigmah.offline.indexeddb.Database;
import org.sigmah.offline.indexeddb.IndexedDBException;
import org.sigmah.offline.indexeddb.OpenDatabaseRequest;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.indexeddb.Schema;

/**
 * Utility class to centralize code used to open a transaction.
 * 
 * @param <S> Store type.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public abstract class OpenTransactionHandler<S extends Enum<S> & Schema> implements JavaScriptEvent<JavaScriptObject> {
    
	/**
	 * Open a new transaction.
	 * 
	 * @param <S> Schema type.
	 * @param openDatabaseRequest Request to open an IndexedDB database.
	 * @param mode Mode to use when opening the transaction.
	 * @param stores Stores to open and lock during the transaction.
	 * @param handler Called when the transaction is opened.
	 */
    public static <S extends Enum<S> & Schema> void openTransaction(OpenDatabaseRequest<S> openDatabaseRequest, Transaction.Mode mode, Collection<S> stores, OpenTransactionHandler<S> handler) {
        handler.stores = stores;
        handler.openDatabaseRequest = openDatabaseRequest;
        handler.mode = mode;
        openDatabaseRequest.addSuccessHandler(handler);
    }
    
    private Collection<S> stores;
    private OpenDatabaseRequest<S> openDatabaseRequest;
    private Transaction.Mode mode;

	/**
	 * {@inheritDoc}
	 */
    @Override
    public void onEvent(JavaScriptObject event) {
        final Database<S> database = openDatabaseRequest.getResult();
		if(database != null) {
			try {
				final Transaction<S> transaction = database.getTransaction(mode, stores);
				onTransaction(transaction);
				
			} catch(IndexedDBException e) {
				Log.warn("An error occured while trying to open an IndexedDB transaction.", e);
			}
		}
    }
    
	/**
	 * Called when a new transaction is opened and ready.
	 * 
	 * @param transaction A new transaction.
	 */
    public abstract void onTransaction(Transaction<S> transaction);

    public void setStores(Collection<S> stores) {
        this.stores = stores;
    }

    public void setMode(Transaction.Mode mode) {
        this.mode = mode;
    }

    public void setOpenDatabaseRequest(OpenDatabaseRequest<S> openDatabaseRequest) {
        this.openDatabaseRequest = openDatabaseRequest;
    }
    
}
