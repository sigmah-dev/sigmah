package org.sigmah.offline.dao;

import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.ValueJS;
import org.sigmah.offline.js.ValueJSIdentifierFactory;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.UpdateProject;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.element.event.ValueEventWrapper;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.offline.js.FileDataJS;
import org.sigmah.offline.js.FileJS;
import org.sigmah.offline.js.FileVersionJS;
import org.sigmah.offline.js.ListableValueJS;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class ValueAsyncDAO extends BaseAsyncDAO {
	
	@Inject
	private FileDataAsyncDAO fileDataAsyncDAO;
	
	public void saveOrUpdate(final GetValue getValue, final ValueResult valueResult) {
		saveOrUpdate(getValue, valueResult, null);
	}
	
	public void saveOrUpdate(final GetValue getValue, final ValueResult valueResult, final AsyncCallback<VoidResult> callback) {
		openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler() {

			@Override
			public void onTransaction(Transaction transaction) {
				saveOrUpdate(getValue, valueResult, callback, transaction);
			}
		});
	}
	
	public void saveOrUpdate(GetValue getValue, ValueResult valueResult, final AsyncCallback<VoidResult> callback, Transaction transaction) {
		final ObjectStore valueObjectStore = transaction.getObjectStore(getRequiredStore());
		
		final ValueJS valueJS = ValueJS.toJavaScript(getValue, valueResult);
		valueObjectStore.put(valueJS).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error while saving value " + valueJS.getId() + ".", caught);
				if(callback != null) {
					callback.onFailure(caught);
				}
            }

            @Override
            public void onSuccess(Request result) {
                Log.trace("Value " + valueJS.getId() + " has been successfully saved.");
				if(callback != null) {
					callback.onSuccess(null);
				}
            }
        });
	}

	public void saveOrUpdate(final UpdateProject updateProject, final ValueEventWrapper valueEventWrapper, final ValueResult originalValue, final AsyncCallback<VoidResult> callback) {
		openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler() {

			@Override
			public void onTransaction(Transaction transaction) {
				saveOrUpdate(updateProject, valueEventWrapper, originalValue, callback, transaction);
			}
		});
	}
	
	public void saveOrUpdate(final UpdateProject updateProject, final ValueEventWrapper valueEventWrapper, final ValueResult originalValue, final AsyncCallback<VoidResult> callback, Transaction transaction) {
		final ObjectStore valueObjectStore = transaction.getObjectStore(getRequiredStore());
		
		final ValueJS valueJS = ValueJS.toJavaScript(updateProject, valueEventWrapper, originalValue);
		valueObjectStore.put(valueJS).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error while saving value " + valueJS.getId() + ".");
				callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Request result) {
                Log.trace("Value " + valueJS.getId() + " has been successfully saved.");
				callback.onSuccess(null);
            }
        });
	}
	
	public void get(final GetValue getValue, final AsyncCallback<ValueResult> callback) {
		get(ValueJSIdentifierFactory.toIdentifier(getValue), callback);
	}
	
	public void get(final String id, final AsyncCallback<ValueResult> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler() {

			@Override
			public void onTransaction(Transaction transaction) {
				get(id, callback, transaction);
			}
		});
	}
	
	public void get(final String id, final AsyncCallback<ValueResult> callback, Transaction transaction) {
		final ObjectStore valueObjectStore = transaction.getObjectStore(getRequiredStore());
		
		valueObjectStore.get(id).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Request request) {
                final ValueJS valueJS = (ValueJS) request.getResult();
				if(valueJS != null) {
					verifyIfFileVersionsAreAvailable(valueJS);
					callback.onSuccess(valueJS.toValueResult());
				} else {
					// No value has been saved for the requested element
					Log.warn("No value saved locally for id " + id);
					callback.onSuccess(new ValueResult());
				}
            }
        });
	}
	
	/**
	 * Sets the <code>available</code> flag on {@link FileVersionJS} instances
	 * by searching in the FileData table.
	 * 
	 * @param valueJS Value to verifiy.
	 */
	private void verifyIfFileVersionsAreAvailable(ValueJS valueJS) {
		final JsArray<ListableValueJS> values = valueJS.getValues();
		if(values != null) {
			
			for(int parent = 0; parent < values.length(); parent++) {
				final ListableValueJS listableValue = values.get(parent);
				
				if(listableValue.getListableValueTypeEnum() == ListableValueJS.Type.FILE) {
					final FileJS file = (FileJS)listableValue;
					final JsArray<FileVersionJS> versions = file.getVersions();
					
					for(int child = 0; child < versions.length(); child++) {
						final FileVersionJS version = versions.get(child);
						
						fileDataAsyncDAO.getByFileVersionId(version.getId(), new AsyncCallback<FileDataJS>() {

							@Override
							public void onFailure(Throwable caught) {
								// Ignored.
							}

							@Override
							public void onSuccess(FileDataJS result) {
								version.setAvailable(result != null);
							}
						});
					}
				}
			}
		}
	}

	@Override
	public Store getRequiredStore() {
		return Store.VALUE;
	}
	
}
