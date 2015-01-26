package org.sigmah.offline.dao;

import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.CategoryElementJS;
import org.sigmah.shared.dto.category.CategoryElementDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class CategoryElementAsyncDAO extends AbstractAsyncDAO<CategoryElementDTO> {

	@Override
	public void saveOrUpdate(CategoryElementDTO t, AsyncCallback<CategoryElementDTO> callback, Transaction transaction) {
		final ObjectStore categoryElementStore = transaction.getObjectStore(Store.CATEGORY_ELEMENT);
		
		final CategoryElementJS categoryElementJS = CategoryElementJS.toJavaScript(t);
		categoryElementStore.put(categoryElementJS).addCallback(new AsyncCallback<Request>() {
            
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error while saving category element " + categoryElementJS.getId() + '.', caught);
            }

            @Override
            public void onSuccess(Request result) {
                Log.trace("Category element " + categoryElementJS.getId() + " has been successfully saved.");
            }
        });
	}

	@Override
	public void get(final int id, final AsyncCallback<CategoryElementDTO> callback, final Transaction transaction) {
		// Searching in the transaction cache
		if(transaction.useObjectFromCache(CategoryElementDTO.class, id, callback)) {
			return;
		}
		
		// Loading from the database
		final ObjectStore categoryElementStore = transaction.getObjectStore(Store.CATEGORY_ELEMENT);

		categoryElementStore.get(id).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Request request) {
                final CategoryElementJS categoryElementJS = (CategoryElementJS) request.getResult();
				final CategoryElementDTO categoryElementDTO = categoryElementJS != null ? categoryElementJS.toDTO() : null;
				
				transaction.getObjectCache().put(id, categoryElementDTO);
				callback.onSuccess(categoryElementDTO);
            }
        });
	}

	@Override
	public Store getRequiredStore() {
		return Store.CATEGORY_ELEMENT;
	}
	
}
