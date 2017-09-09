package org.sigmah.offline.dao;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.ValueJS;
import org.sigmah.offline.js.ValueJSIdentifierFactory;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.UpdateContact;
import org.sigmah.shared.command.UpdateProject;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.element.event.ValueEventWrapper;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.offline.indexeddb.IndexedDB;
import org.sigmah.offline.indexeddb.OpenDatabaseRequest;
import org.sigmah.offline.js.FileDataJS;
import org.sigmah.offline.js.FileJS;
import org.sigmah.offline.js.FileVersionJS;
import org.sigmah.offline.js.ListableValueJS;
import org.sigmah.offline.sync.AsyncAdapter;
import org.sigmah.offline.sync.SuccessCallback;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

/**
 * Asynchronous DAO for saving and loading the values of the flexible elements.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class ValueAsyncDAO extends BaseAsyncDAO<Store> {
	
	@Inject
	private FileDataAsyncDAO fileDataAsyncDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OpenDatabaseRequest<Store> openDatabase() {
		return IndexedDB.openUserDatabase(getAuthentication());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<Store> getSchema() {
		return Store.class;
	}
	
	public void saveOrUpdate(final GetValue getValue, final ValueResult valueResult) {
		saveOrUpdate(getValue, valueResult, null);
	}
	
	public void saveOrUpdate(final GetValue getValue, final ValueResult valueResult, final AsyncCallback<VoidResult> callback) {
		openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler<Store>() {

			@Override
			public void onTransaction(final Transaction<Store> transaction) {
				saveOrUpdate(getValue, valueResult, callback, transaction);
			}
		});
	}
	
	public void saveOrUpdate(final GetValue getValue, final ValueResult valueResult, final AsyncCallback<VoidResult> callback, final Transaction<Store> transaction) {
		final ObjectStore valueObjectStore = transaction.getObjectStore(getRequiredStore());
		
		final ValueJS valueJS = ValueJS.toJavaScript(getValue, valueResult);
		valueObjectStore.put(valueJS).addCallback(new AsyncAdapter<Request, VoidResult>(callback));
	}

	public void saveOrUpdate(final UpdateProject updateProject, final ValueEventWrapper valueEventWrapper, final ValueResult originalValue, final AsyncCallback<VoidResult> callback) {
		openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler<Store>() {

			@Override
			public void onTransaction(final Transaction<Store> transaction) {
				saveOrUpdate(updateProject, valueEventWrapper, originalValue, callback, transaction);
			}
		});
	}
	
	public void saveOrUpdate(final UpdateProject updateProject, final ValueEventWrapper valueEventWrapper, final ValueResult originalValue, final AsyncCallback<VoidResult> callback, final Transaction<Store> transaction) {
		final ObjectStore valueObjectStore = transaction.getObjectStore(getRequiredStore());
		final ValueJS valueJS = ValueJS.toJavaScript(updateProject, valueEventWrapper, originalValue);
		valueObjectStore.put(valueJS).addCallback(new AsyncAdapter<Request, VoidResult>(callback));
	}

public void saveOrUpdate(final UpdateContact updateContact, final ValueEventWrapper valueEventWrapper, final ValueResult originalValue, final AsyncCallback<VoidResult> callback) {
		openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler<Store>() {

			@Override
			public void onTransaction(Transaction<Store> transaction) {
				saveOrUpdate(updateContact, valueEventWrapper, originalValue, callback, transaction);
			}
		});
	}

	public void saveOrUpdate(final UpdateContact updateContact, final ValueEventWrapper valueEventWrapper, final ValueResult originalValue, final AsyncCallback<VoidResult> callback, Transaction transaction) {
		final ObjectStore valueObjectStore = transaction.getObjectStore(getRequiredStore());

		final ValueJS valueJS = ValueJS.toJavaScript(updateContact, valueEventWrapper, originalValue);
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
	
	public void saveOrUpdate(final String value, final FlexibleElementDTO flexibleElement, final int projectId, final AsyncCallback<VoidResult> callback) {
		openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler<Store>() {

			@Override
			public void onTransaction(final Transaction<Store> transaction) {
				saveOrUpdate(value, flexibleElement, projectId, callback, transaction);
			}
		});
	}
	
	public void saveOrUpdate(final String value, final FlexibleElementDTO flexibleElement, final int projectId, final AsyncCallback<VoidResult> callback, final Transaction<Store> transaction) {
		final ObjectStore valueObjectStore = transaction.getObjectStore(getRequiredStore());
		final ValueJS valueJS = ValueJS.toJavaScript(value, flexibleElement, projectId);
		valueObjectStore.put(valueJS).addCallback(new AsyncAdapter<Request, VoidResult>(callback));
	}
	
	public void get(final GetValue getValue, final AsyncCallback<ValueResult> callback) {
		get(ValueJSIdentifierFactory.toIdentifier(getValue), callback);
	}
	
	public void get(final String id, final AsyncCallback<ValueResult> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {

			@Override
			public void onTransaction(Transaction<Store> transaction) {
				get(id, callback, transaction);
			}
		});
	}
	
	public void get(final String id, final AsyncCallback<ValueResult> callback, Transaction<Store> transaction) {
		final ObjectStore valueObjectStore = transaction.getObjectStore(getRequiredStore());
		
		valueObjectStore.get(id).addCallback(new SuccessCallback<Request>(callback) {

            @Override
            public void onSuccess(Request request) {
                final ValueJS valueJS = (ValueJS) request.getResult();
				if (valueJS != null) {
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
		if (values != null) {
			
			for (int parent = 0; parent < values.length(); parent++) {
				final ListableValueJS listableValue = values.get(parent);
				
				if (listableValue.getListableValueTypeEnum() == ListableValueJS.Type.FILE) {
					final FileJS file = (FileJS)listableValue;
					final JsArray<FileVersionJS> versions = file.getVersions();
					
					for (int child = 0; child < versions.length(); child++) {
						final FileVersionJS version = versions.get(child);
						
						fileDataAsyncDAO.getByFileVersionId(version.getId(), new SuccessCallback<FileDataJS>() {

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Store getRequiredStore() {
		return Store.VALUE;
	}
	
}
