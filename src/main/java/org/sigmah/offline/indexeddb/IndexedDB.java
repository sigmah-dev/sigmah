package org.sigmah.offline.indexeddb;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import java.util.LinkedList;
import java.util.Set;
import org.sigmah.offline.event.JavaScriptEvent;
import org.sigmah.offline.sync.UpdateDates;
import org.sigmah.shared.command.result.Authentication;

/**
 * Main class for creating and opening IndexedDB databases.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class IndexedDB {
	private static final int REVISION = 9;
	private static final int VERSION = Store.values().length + REVISION;
    
    private static State state = State.CLOSED;
    private static Database database;
    private static final LinkedList<AlreadyOpenedDatabaseRequest> listenerQueue = new LinkedList<AlreadyOpenedDatabaseRequest>();
	
	/**
	 * Verify if IndexedDB is supported by the current browser.
	 * @return <code>true</code> if supported, <code>false</code> otherwise.
	 */
	public static native boolean isSupported() /*-{
		return typeof $wnd.indexedDB != 'undefined';
	}-*/;
	
	/**
	 * Open or create a database named with the e-mail address of the current user.
	 * @param authentication Information about the current user.
	 * @return A request to open the database.
	 */
	public static OpenDatabaseRequest openUserDatabase(Authentication authentication) {
		return openUserDatabase(authentication.getUserEmail());
	}
    
    public static OpenDatabaseRequest deleteUserDatabase(Authentication authentication) {
		closeDatabase();
        return deleteUserDatabase(authentication.getUserEmail());
    }
	
	private static OpenDatabaseRequest openUserDatabase(final String email) {
        if(database != null) {
			if(database.getName().equals(email)) {
				return new AlreadyOpenedDatabaseRequest(database);
				
			} else {
				// Switching database.
				state = State.CLOSED;
				database.close();
				database = null;
			}
        }
        
		if(email == null) {
			return new NoopDatabaseRequest();
		}
		if(!isSupported()) {
			Log.warn("IndexedDB is not supported by this web browser.");
			return new NoopDatabaseRequest();
		}
		if(!GWT.isProdMode()) {
			Log.info("IndexedDB is unavailable in Hosted Mode.");
			return new NoopDatabaseRequest();
		}
        
        switch(state) {
            case CLOSED:
                state = State.OPENING;
                
                final IndexedDB indexedDB = new IndexedDB();
                final NativeOpenDatabaseRequest openDatabaseRequest = indexedDB.open(email, VERSION);

                openDatabaseRequest.addSuccessHandler(new JavaScriptEvent() {
                    @Override
                    public void onEvent(JavaScriptObject event) {
                        database = openDatabaseRequest.getResult();
						
						if(database != null) {
							state = State.OPENED;
							for(final AlreadyOpenedDatabaseRequest listener : listenerQueue) {
								listener.setResult(database);
							}
						} else {
							state = State.ERROR;
						}
                    }
                });
                
                openDatabaseRequest.addUpgradeNeededHandler(new JavaScriptEvent<IDBVersionChangeEvent>() {
                    @Override
                    public void onEvent(IDBVersionChangeEvent event) {
                        upgradeDatabase(openDatabaseRequest.getResult(), event, email);
                    }
                });
                return openDatabaseRequest;
                
            case OPENING:
                final AlreadyOpenedDatabaseRequest listener = new AlreadyOpenedDatabaseRequest();
                listenerQueue.add(listener);
                return listener;
                
            case OPENED:
                return new AlreadyOpenedDatabaseRequest(database);
                
            default:
                return new NoopDatabaseRequest();
        }
	}
	
	private static void upgradeDatabase(final Database database, IDBVersionChangeEvent event, String email) {
		Log.info("Local IndexedDB database is being updated from version " + event.getOldVersion() + " to version " + VERSION + '.');
		UpdateDates.setDatabaseUpdateDate(email, null);
		
		for(final Store store : database.getObjectStores()) {
			database.deleteObjectStore(store);
		}

		final Set<Store> stores = database.getObjectStores();

		for(final Store store : Store.values()) {
			if(!stores.contains(store)) {
				final ObjectStore objectStore = database.createObjectStore(store, "id", store.isAutoIncrement());

				switch(store) {
					case FILE_DATA:
						objectStore.createIndex("fileVersionId", "fileVersion.id");
						break;
					case MONITORED_POINT:
						objectStore.createIndex("parentListId", "parentListId");
						break;
					case PROJECT:
						objectStore.createIndex("orgUnit", "orgUnit");
						objectStore.createIndex("remindersListId", "remindersListId");
						objectStore.createIndex("pointsListId", "pointsListId");
						break;
					case PROJECT_REPORT:
						objectStore.createIndex("versionId", "versionId");
						break;
					case REPORT_REFERENCE:
						objectStore.createIndex("parentId", "parentId");
						break;
					case REMINDER:
						objectStore.createIndex("parentListId", "parentListId");
						break;
					case TRANSFERT:
						objectStore.createIndex("type", "type");
						objectStore.createIndex("fileVersionId", "fileVersion.id");
						break;
					case USER:
						objectStore.createIndex("organization", "organization");
						break;
				}
			}
		}
	}
    
    private static OpenDatabaseRequest deleteUserDatabase(String email) {
        if(email == null) {
			return new NoopDatabaseRequest();
		}
		if(!isSupported()) {
			Log.warn("IndexedDB is not supported by this web browser.");
			return new NoopDatabaseRequest();
		}
        
        final IndexedDB indexedDB = new IndexedDB();
        return indexedDB.deleteDatabase(email);
    }
	
	/**
	 * Close and release the currently opened database.
	 */
	public static void closeDatabase() {
		if(database != null) {
			database.close();
			state = State.CLOSED;
		}
	}
	
	private final NativeIndexedDB nativeIndexedDB;
	
	public IndexedDB() throws UnsupportedOperationException {
		if(!isSupported()) {
			throw new UnsupportedOperationException("IndexedDB is not supported by this web browser.");
		}
		this.nativeIndexedDB = NativeIndexedDB.getIndexedDB();
	}

	public NativeOpenDatabaseRequest open(String name, int version) {
		return new NativeOpenDatabaseRequest(nativeIndexedDB.open(name, version));
	}
    
    private NativeOpenDatabaseRequest deleteDatabase(String name) {
        return new NativeOpenDatabaseRequest(nativeIndexedDB.deleteDatabase(name));
    }
    
    public static enum State {
        CLOSED, OPENING, OPENED, ERROR;
    }
}
