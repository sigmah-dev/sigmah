package org.sigmah.offline.dao;

import java.util.ArrayList;

import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.ValueHistoryJS;
import org.sigmah.shared.command.GetHistory;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.history.HistoryTokenListDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class HistoryAsyncDAO extends BaseAsyncDAO {
	
	public void saveOrUpdate(final GetHistory getHistory, final ListResult<HistoryTokenListDTO> historyResult) {
		openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler() {

			@Override
			public void onTransaction(Transaction transaction) {
				saveOrUpdate(getHistory, historyResult, transaction);
			}
		});
	}

	public void saveOrUpdate(final GetHistory getHistory, final ListResult<HistoryTokenListDTO> historyResult, Transaction transaction) {
		final ObjectStore historyObjectStore = transaction.getObjectStore(Store.HISTORY);
		
		final ValueHistoryJS valueHistoryJS = ValueHistoryJS.toJavaScript(getHistory, historyResult);
		historyObjectStore.put(valueHistoryJS).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error while saving value history " + valueHistoryJS.getId() + ".", caught);
            }

            @Override
            public void onSuccess(Request result) {
                Log.trace("Value history " + valueHistoryJS.getId() + " has been successfully saved.");
            }
        });
	}
	
	public void get(final GetHistory getHistory, final AsyncCallback<ListResult<HistoryTokenListDTO>> callback) {
		get(ValueHistoryJS.toIdentifier(getHistory), callback);
	}
	
	public void get(final String id, final AsyncCallback<ListResult<HistoryTokenListDTO>> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler() {

			@Override
			public void onTransaction(Transaction transaction) {
				get(id, callback, transaction);
			}
		});
	}
	
	public void get(final String id, final AsyncCallback<ListResult<HistoryTokenListDTO>> callback, Transaction transaction) {
		final ObjectStore historyObjectStore = transaction.getObjectStore(Store.HISTORY);
		
		historyObjectStore.get(id).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Request request) {
                final ValueHistoryJS valueHistoryJS = (ValueHistoryJS) request.getResult();
				if(valueHistoryJS != null) {
					callback.onSuccess(valueHistoryJS.toHistoryResult());
				} else {
					// No value has been saved for the requested element
					Log.warn("No value saved locally for id " + id);
					callback.onSuccess(new ListResult<HistoryTokenListDTO>(new ArrayList<HistoryTokenListDTO>()));
				}
            }
        });
	}

	@Override
	public Store getRequiredStore() {
		return Store.HISTORY;
	}
}
