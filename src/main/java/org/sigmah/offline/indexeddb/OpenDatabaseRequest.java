package org.sigmah.offline.indexeddb;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.offline.event.JavaScriptEvent;

/**
 * Define a request to open an IndexedDB database.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface OpenDatabaseRequest<S extends Enum<S> & Schema> {
	/**
	 * Retrieve the opened database instance.
	 * <p/>
	 * Returned value is null during the opening.
	 * 
	 * @return Instance of the opened database or <code>null</code> while loading.
	 */
    Database<S> getResult();
	
	/**
	 * Adds an handler that will be called when the database is opened.
	 * 
	 * @param handler The handler to add.
	 */
    void addSuccessHandler(JavaScriptEvent handler);
	
	/**
	 * Adds a callback that will be called when the database is opened or if an
	 * error occurs during the operation.
	 * 
	 * @param callback Callback to add.
	 */
    void addCallback(AsyncCallback<Request> callback);
	
}
