package org.sigmah.offline.dao;

import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.LogFrameJS;
import org.sigmah.shared.dto.logframe.LogFrameDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class LogFrameAsyncDAO extends AbstractAsyncDAO<LogFrameDTO> {

	public LogFrameAsyncDAO() {
	}

	@Override
	public void saveOrUpdate(LogFrameDTO t, AsyncCallback<LogFrameDTO> callback, Transaction transaction) {
		final ObjectStore logFrameObjectStore = transaction.getObjectStore(getRequiredStore());
		
		final LogFrameJS logFrameJS = LogFrameJS.toJavaScript(t);
		logFrameObjectStore.put(logFrameJS).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error while saving log frame " + logFrameJS.getId() + ".", caught);
            }

            @Override
            public void onSuccess(Request request) {
                Log.trace("Log frame " + logFrameJS.getId() + " has been successfully saved.");
            }
        });
	}

	@Override
	public void get(final int id, final AsyncCallback<LogFrameDTO> callback, final Transaction transaction) {
		if(transaction.useObjectFromCache(LogFrameDTO.class, id, callback)) {
			return;
		}
		
		final ObjectStore logFrameObjectStore = transaction.getObjectStore(getRequiredStore());
		logFrameObjectStore.get(id).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Request request) {
                final LogFrameJS logFrameJS = request.getResult();
				final LogFrameDTO logFrameDTO = logFrameJS != null ? logFrameJS.toDTO() : null;
				
				callback.onSuccess(logFrameDTO);
            }
        });
	}

	@Override
	public Store getRequiredStore() {
		return Store.LOG_FRAME;
	}
	
}
