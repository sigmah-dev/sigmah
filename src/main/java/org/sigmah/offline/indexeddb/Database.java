package org.sigmah.offline.indexeddb;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * IndexedDB database.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Database {
    /**
     * Native JavaScript instance of this database.
     */
	private final IDBDatabase nativeDatabase;

	Database(IDBDatabase database) {
		this.nativeDatabase = database;
	}
	
	public String getName() {
		return nativeDatabase.getName();
	}
	
	public int getVersion() {
		return nativeDatabase.getVersion();
	}
	
	public void close() {
		nativeDatabase.close();
	}
	
	public Set<String> getObjectStoreNames() {
		final JsArrayString names = nativeDatabase.getObjectStoreNames();
		final HashSet<String> result = new HashSet<String>();
		
		for(int index = 0; index < names.length(); index++) {
			result.add(names.get(index));
		}
		
		return result;
	}
	
	public Set<Store> getObjectStores() {
		final JsArrayString names = nativeDatabase.getObjectStoreNames();
		final EnumSet<Store> result = EnumSet.noneOf(Store.class);
		
		for(int index = 0; index < names.length(); index++) {
			result.add(Store.valueOf(names.get(index)));
		}
		
		return result;
	}
	
	private ObjectStore createObjectStore(String name) {
		return new ObjectStore(nativeDatabase.createObjectStore(name));
	}
	
	public ObjectStore createObjectStore(Store store) {
		return createObjectStore(store.name());
	}
	
	private ObjectStore createObjectStore(String name, String keyPath, boolean autoIncrement) {
		return new ObjectStore(nativeDatabase.createObjectStore(name, keyPath, autoIncrement));
	}
	
	public ObjectStore createObjectStore(Store store, String keyPath, boolean autoIncrement) {
		return createObjectStore(store.name(), keyPath, autoIncrement);
	}
	
	private void deleteObjectStore(String name) {
		nativeDatabase.deleteObjectStore(name);
	}
	
	public void deleteObjectStore(Store store) {
		deleteObjectStore(store.name());
	}
	
	public Transaction getTransaction(Transaction.Mode mode, Collection<Store> stores) {
		final JsArrayString array = (JsArrayString) JavaScriptObject.createArray();
		for(final Store store : stores) {
			array.push(store.name());
		}
		return new Transaction(nativeDatabase.getTransaction(array, mode.getArgument()), mode, stores);
	}
}
