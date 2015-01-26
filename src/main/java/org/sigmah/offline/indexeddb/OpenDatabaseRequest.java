package org.sigmah.offline.indexeddb;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.offline.event.JavaScriptEvent;

/**
 * Define a request to open an IndexedDB database.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface OpenDatabaseRequest {
	/**
	 * Retrieve the opened database instance.
	 * <p/>
	 * Returned value is null during the opening.
	 * 
	 * @return Instance of the opened database or <code>null</code> while loading.
	 */
    Database getResult();
	
	/**
	 * Adds an handler that will be called when the database is opened.
	 * 
	 * @param handler The handler to add.
	 */
    void addSuccessHandler(JavaScriptEvent handler);
	
    void addCallback(AsyncCallback<Request> callback);
}
