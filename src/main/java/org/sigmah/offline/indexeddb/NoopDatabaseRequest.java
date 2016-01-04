package org.sigmah.offline.indexeddb;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.offline.event.JavaScriptEvent;

/**
 * Empty request. No operation will be done.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class NoopDatabaseRequest<S extends Enum<S> & Schema> implements OpenDatabaseRequest<S> {

	/**
	 * {@inheritDoc}
	 */
    @Override
    public Database<S> getResult() {
        return null;
    }

	/**
	 * {@inheritDoc}
	 */
    @Override
    public void addSuccessHandler(JavaScriptEvent handler) {
    }

	/**
	 * {@inheritDoc}
	 */
    @Override
    public void addCallback(AsyncCallback<Request> callback) {
    }
    
}
