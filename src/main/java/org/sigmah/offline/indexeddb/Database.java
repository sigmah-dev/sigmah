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

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * IndexedDB database.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @param <S> Enum type defining the stores contained in the database?
 */
public class Database<S extends Enum<S> & Schema> {
	
    /**
     * Native JavaScript instance of this database.
     */
	private final IDBDatabase nativeDatabase;
	/**
	 * Enum type containing the store list of this database.
	 */
	private final Class<S> schema;

	Database(IDBDatabase database, Class<S> schema) {
		this.nativeDatabase = database;
		this.schema = schema;
	}
	
	/**
	 * Retrieves the name of this database.
	 * 
	 * @return Name of the database.
	 */
	public String getName() {
		return nativeDatabase.getName();
	}
	
	/**
	 * Retrieves the version number of this database.
	 * 
	 * @return Version number of the database.
	 */
	public int getVersion() {
		return nativeDatabase.getVersion();
	}
	
	/**
	 * Retrieves the schema of this database.
	 * 
	 * @return Schema of the database.
	 */
	public Class<S> getSchema() {
		return schema;
	}
	
	/**
	 * Closes the database.
	 */
	public void close() {
		nativeDatabase.close();
	}
	
	/**
	 * Retrieves the name of every object store in this database.
	 * 
	 * @return A set of the object stores names.
	 */
	public Set<String> getObjectStoreNames() {
		final JsArrayString names = nativeDatabase.getObjectStoreNames();
		final HashSet<String> result = new HashSet<String>();
		
		for(int index = 0; index < names.length(); index++) {
			result.add(names.get(index));
		}
		
		return result;
	}
	
	/**
	 * Retrieves every object store in this database.
	 * 
	 * @return A set of the object stores.
	 */
	public Set<S> getObjectStores() {
		final JsArrayString names = nativeDatabase.getObjectStoreNames();
		final EnumSet<S> result = EnumSet.noneOf(schema);
		
		for(int index = 0; index < names.length(); index++) {
			try {
				final S store = Enum.valueOf(schema, names.get(index));
				result.add(store);
			} catch (IllegalArgumentException e) {
				// Can happen if a store has been renamed or removed.
			}
		}
		
		return result;
	}
	
	/**
	 * Creates a new object store with the given name.
	 * 
	 * @param name Name of the store to create.
	 * @return A new object store.
	 */
	private ObjectStore createObjectStore(String name) {
		return new ObjectStore(nativeDatabase.createObjectStore(name));
	}
	
	/**
	 * Creates a new object store.
	 * 
	 * @param store Store to create.
	 * @return A new object store.
	 */
	public ObjectStore createObjectStore(S store) {
		return createObjectStore(store.name());
	}
	
	/**
	 * Creates a new object store with the given name.
	 * 
	 * @param name Name of the store to create.
	 * @param keyPath Path to the variable used as the key for the content of the new store.
	 * @param autoIncrement <code>true</code> to fill the key value with a sequence.
	 * @return A new object store.
	 */
	private ObjectStore createObjectStore(String name, String keyPath, boolean autoIncrement) {
		return new ObjectStore(nativeDatabase.createObjectStore(name, keyPath, autoIncrement));
	}
	
	/**
	 * Creates a new object store.
	 * 
	 * @param store Store to create.
	 * @param keyPath Path to the variable used as the key for the content of the new store.
	 * @param autoIncrement <code>true</code> to fill the key value with a sequence.
	 * @return A new object store.
	 */
	public ObjectStore createObjectStore(S store, String keyPath, boolean autoIncrement) {
		return createObjectStore(store.name(), keyPath, autoIncrement);
	}
	
	/**
	 * Remove an object store by its name. Useful to remove an object store
	 * that was created in a previous version.
	 * 
	 * @param name Name of the object store to remove.
	 */
	public void deleteObjectStore(String name) {
		nativeDatabase.deleteObjectStore(name);
	}
	
	/**
	 * Remove the given object store.
	 * 
	 * @param store Store to remove.
	 */
	public void deleteObjectStore(S store) {
		deleteObjectStore(store.name());
	}
	
	/**
	 * Open a new transaction in the given mode.
	 * 
	 * @param mode Write mode.
	 * @param stores Stores to access during the transaction.
	 * @return A new transaction.
	 * @throws IndexedDBException If an error occurs when opening the transaction.
	 */
	public Transaction<S> getTransaction(Transaction.Mode mode, Collection<S> stores) throws IndexedDBException {
		final JsArrayString array = (JsArrayString) JavaScriptObject.createArray();
		for(final S store : stores) {
			array.push(store.name());
		}
		try {
			return new Transaction<S>(nativeDatabase.getTransaction(array, mode.getArgument()), mode, stores);
		} catch(JavaScriptException e) {
			throw new IndexedDBException(e);
		}
	}
	
}
