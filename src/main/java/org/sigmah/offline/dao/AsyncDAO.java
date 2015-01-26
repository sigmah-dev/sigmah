package org.sigmah.offline.dao;

import java.util.Collection;
import java.util.Set;

import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Asynchronous DAO. Primarily created to be used with IndexedDB.
 * 
 * @param <T> DTO saved and loaded by this DAO.
 * @author Raphaël Calabro <rcalabro@ideia.fr>
 */
public interface AsyncDAO<T> {
	/**
	 * Open a new transaction and save or replace the given object in the 
	 * database.
	 * @param t Object to save.
	 */
	void saveOrUpdate(T t);
	
	/**
	 * Save or replace the given object using the given transaction.
	 * <p/>
	 * It is more efficient to save composites objects using the same transaction.
	 * Open a new transaction with {@link #saveOrUpdate(Object)} and save its children
	 * with this method.
	 * 
	 * @param t Object to be saved.
	 * @param callback Called when the object is saved or in case of failure (not always supported).
	 * @param transaction An open transaction to use.
	 */
	void saveOrUpdate(T t, AsyncCallback<T> callback, Transaction transaction);
	
	/**
	 * Open a new transaction and retrieve the object associated to the given id.
	 * 
	 * @param id Identifier of the object to retrieve.
	 * @param callback Called when the object is retrieved or in case of failure.
	 */
	void get(int id, AsyncCallback<T> callback);
	
	/**
	 * Retrieve the object associated to the given id using the given transaction.
	 * 
	 * @param id Identifier of the object to retrieve.
	 * @param callback Called when the object is retrieved or in case of failure.
	 * @param transaction An open transaction to use.
	 */
	void get(int id, AsyncCallback<T> callback, Transaction transaction);
	
	/**
	 * Retrieve the store directly required to save and load the kind of object 
	 * handled by this DAO.
	 * @return The required store.
	 */
	Store getRequiredStore();
	
	/**
	 * Retrieve the full list of stores required by this DAO and its
	 * dependencies.
	 * 
	 * @return A set of all the stores needed when opening a transaction.
	 */
	Set<Store> getRequiredStores();
	
	/**
	 * Retrieve the collection of DAO required to save or load the objects
	 * managed by this DAO.
	 * 
	 * @return All of the dependencies required by this DAO.
	 */
	Collection<BaseAsyncDAO> getDependencies();
}
