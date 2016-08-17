package org.sigmah.offline.dao;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import java.util.List;
import org.sigmah.offline.indexeddb.Cursor;
import org.sigmah.offline.indexeddb.IDBKeyRange;
import org.sigmah.offline.indexeddb.Index;
import org.sigmah.offline.indexeddb.Indexes;
import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.OpenCursorRequest;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.ExtendedComputationElementJS;
import org.sigmah.offline.sync.SuccessCallback;
import org.sigmah.shared.computation.dependency.CollectionDependency;
import org.sigmah.shared.dto.element.ComputationElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

/**
 * Asynchronous DAO for saving and loading <code>ComputationJS</code> objects.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public class ComputationAsyncDAO extends AbstractUserDatabaseAsyncDAO<ComputationElementDTO, ExtendedComputationElementJS> {
	
	/**
	 * Open a new transaction and retrieve the computation elements referencing
	 * (or not) a contribution dependency.
	 * 
	 * @param contribution
	 *			<code>true</code> for the computation elements referencing a contribution,
	 *			<code>false</code> for the others.
	 * @param callback 
	 *			Called with the computation elements matching the criteria.
	 */
	public void get(final boolean contribution, final AsyncCallback<List<ComputationElementDTO>> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {
			
			@Override
			public void onTransaction(final Transaction<Store> transaction) {
				get(contribution, callback, transaction);
			}
			
		});
	}
	
	/**
	 * Retrieve the computation elements referencing (or not) a contribution 
	 * dependency.
	 * 
	 * @param contribution
	 *			<code>true</code> for the computation elements referencing a contribution,
	 *			<code>false</code> for the others.
	 * @param callback 
	 *			Called with the computation elements matching the criteria.
	 * @param transaction 
	 *			Transaction to use.
	 */
	public void get(final boolean contribution, final AsyncCallback<List<ComputationElementDTO>> callback, final Transaction<Store> transaction) {
		final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());
		final Index index = objectStore.index(Indexes.COMPUTATION_CONTRIBUTION);
		
		final OpenCursorRequest request = index.openCursor(IDBKeyRange.only(contribution));
		doGet(request, callback);
	}
	
	/**
	 * Open a new transaction and  retrieve the computation elements using the 
	 * given flexible element.
	 * 
	 * @param flexibleElement
	 *			Flexible element to search.
	 * @param callback
	 *			Called with the computation elements matching the criteria.
	 */
	public void get(final FlexibleElementDTO flexibleElement, final AsyncCallback<List<ComputationElementDTO>> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {
			
			@Override
			public void onTransaction(final Transaction<Store> transaction) {
				get(flexibleElement, callback, transaction);
			}
			
		});
	}
	
	/**
	 * Retrieve the computation elements using the given flexible element.
	 * 
	 * @param flexibleElement
	 *			Flexible element to search.
	 * @param callback
	 *			Called with the computation elements matching the criteria.
	 * @param transaction 
	 *			Transaction to use.
	 */
	public void get(final FlexibleElementDTO flexibleElement, final AsyncCallback<List<ComputationElementDTO>> callback, final Transaction<Store> transaction) {
		final CollectionDependency dependency = new CollectionDependency();
		dependency.setFlexibleElement(flexibleElement);
		
		final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());
		final Index index = objectStore.index(Indexes.COMPUTATION_DEPENDENCIES);
		
		final OpenCursorRequest request = index.openCursor(IDBKeyRange.only(dependency.flexibleElementString()));
		doGet(request, callback);
	}

	/**
	 * Iterate on the given cursor request and send back the computation elements.
	 * 
	 * @param request
	 *			Request to open a cursor.
	 * @param callback 
	 *			Callback to call with the computation elements.
	 */
	private void doGet(final OpenCursorRequest request, final AsyncCallback<List<ComputationElementDTO>> callback) {
		request.addCallback(new SuccessCallback<Request>(callback) {
			
			private final List<ComputationElementDTO> elements = new ArrayList<ComputationElementDTO>();
			
			@Override
			public void onSuccess(final Request result) {
				final Cursor cursor = request.getResult();
				if (cursor != null) {
					final ExtendedComputationElementJS elementJS = cursor.getValue();
					final ComputationElementDTO elementDTO = toJavaObject(elementJS);
					elements.add(elementDTO);
					cursor.next();
				} else {
					callback.onSuccess(elements);
				}
			}
			
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExtendedComputationElementJS toJavaScriptObject(ComputationElementDTO t) {
		return ExtendedComputationElementJS.toJavaScript(t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComputationElementDTO toJavaObject(ExtendedComputationElementJS js) {
		return js.toDTO();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Store getRequiredStore() {
		return Store.COMPUTATION;
	}
	
}
