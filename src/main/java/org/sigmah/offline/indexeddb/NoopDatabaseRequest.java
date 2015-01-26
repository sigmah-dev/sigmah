package org.sigmah.offline.indexeddb;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.offline.event.JavaScriptEvent;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class NoopDatabaseRequest implements OpenDatabaseRequest {

    @Override
    public Database getResult() {
        return null;
    }

    @Override
    public void addSuccessHandler(JavaScriptEvent handler) {
    }

    @Override
    public void addCallback(AsyncCallback<Request> callback) {
    }
    
}
