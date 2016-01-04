package org.sigmah.offline.dao;

import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.CategoryElementJS;
import org.sigmah.shared.dto.category.CategoryElementDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;

/**
 * Asynchronous DAO for saving and loading <code>CategoryElementDTO</code> objects.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class CategoryElementAsyncDAO extends AbstractUserDatabaseAsyncDAO<CategoryElementDTO, CategoryElementJS> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(final int id, final AsyncCallback<CategoryElementDTO> callback, final Transaction<Store> transaction) {
		// Searching in the transaction cache
		if(transaction.useObjectFromCache(CategoryElementDTO.class, id, callback)) {
			return;
		}
		
		super.get(id, callback, transaction);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Store getRequiredStore() {
		return Store.CATEGORY_ELEMENT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CategoryElementJS toJavaScriptObject(CategoryElementDTO t) {
		return CategoryElementJS.toJavaScript(t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CategoryElementDTO toJavaObject(CategoryElementJS js) {
		return js.toDTO();
	}
	
}
