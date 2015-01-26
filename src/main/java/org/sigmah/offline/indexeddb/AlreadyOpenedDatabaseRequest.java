package org.sigmah.offline.indexeddb;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import java.util.List;
import org.sigmah.offline.event.JavaScriptEvent;

/**
 * Open request done on an already opened database.
 * <p/>
 * Reuse the given database instance instead of creating a new connection.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class AlreadyOpenedDatabaseRequest implements OpenDatabaseRequest {
    
    private final List<JavaScriptEvent> handlers = new ArrayList<JavaScriptEvent>();
    private Database result;
    
	/**
	 * Creates an empty request. This is useful if an open request is pending.
	 */
    public AlreadyOpenedDatabaseRequest() {
    }

	/**
	 * Creates a request that will immediatly returns the given database.
	 * 
	 * @param database An already opened database.
	 */
    public AlreadyOpenedDatabaseRequest(Database database) {
        this.result = database;
    }
    
    @Override
    public Database getResult() {
        return result;
    }

	/**
	 * Defines the database to use. 
	 * This will trigger the success handler.
	 * 
	 * @param result An already opened database.
	 */
    public void setResult(Database result) {
        this.result = result;
        for(final JavaScriptEvent handler : handlers) {
            handler.onEvent(null);
        }
    }
    
    @Override
    public void addSuccessHandler(JavaScriptEvent handler) {
        if(result != null) {
            handler.onEvent(null);
        } else {
            handlers.add(handler);
        }
    }

    @Override
    public void addCallback(AsyncCallback<Request> callback) {
        throw new UnsupportedOperationException("Not supported.");
    }
    
}
