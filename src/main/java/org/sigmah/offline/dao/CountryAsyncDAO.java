package org.sigmah.offline.dao;

import java.util.ArrayList;

import org.sigmah.offline.indexeddb.Cursor;
import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.OpenCursorRequest;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.CountryJS;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.country.CountryDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class CountryAsyncDAO extends AbstractAsyncDAO<CountryDTO> {
	
	public void saveOrUpdate(final ListResult<CountryDTO> countryResult) {
		if(countryResult != null && countryResult.getData() != null) {
            openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler() {

                @Override
                public void onTransaction(Transaction transaction) {
                    for(final CountryDTO countryDTO : countryResult.getData()) {
						saveOrUpdate(countryDTO, null, transaction);
					}
                }
            });
		}
	}

	@Override
	public void saveOrUpdate(final CountryDTO t, AsyncCallback<CountryDTO> callback, Transaction transaction) {
		final ObjectStore countryStore = transaction.getObjectStore(Store.COUNTRY);
		
		final CountryJS countryJS = CountryJS.toJavaScript(t);
		countryStore.put(countryJS).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error while saving country " + t.getId() + ".", caught);
            }

            @Override
            public void onSuccess(Request result) {
                Log.trace("Country " + t.getId() + " has been successfully saved.");
            }
        });
        
        // TODO: Save the children objects
	}

	@Override
	public void get(int id, final AsyncCallback<CountryDTO> callback, Transaction transaction) {
		final ObjectStore countryStore = transaction.getObjectStore(Store.COUNTRY);

		countryStore.get(id).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Request request) {
                final CountryJS countryJS = request.getResult();
				final CountryDTO countryDTO = countryJS != null ? countryJS.toDTO() : null;
				callback.onSuccess(countryDTO);
            }
        });
	}
	
	public void getAll(final AsyncCallback<ListResult<CountryDTO>> callback) {
        openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler() {

            @Override
            public void onTransaction(Transaction transaction) {
                final ArrayList<CountryDTO> countries = new ArrayList<CountryDTO>();
				
				final ObjectStore countryObjectStore = transaction.getObjectStore(Store.COUNTRY);
				final OpenCursorRequest cursorRequest = countryObjectStore.openCursor();
                
                cursorRequest.addCallback(new AsyncCallback<Request>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(Request result) {
                        final Cursor cursor = cursorRequest.getResult();
						if(cursor != null) {
							final CountryJS countryJS = (CountryJS) cursor.getValue();
							countries.add(countryJS.toDTO());
							cursor.next();
							
						} else {
							callback.onSuccess(new ListResult<CountryDTO>(countries));
						}
                    }
                });
            }
        });
	}

	@Override
	public Store getRequiredStore() {
		return Store.COUNTRY;
	}
}
