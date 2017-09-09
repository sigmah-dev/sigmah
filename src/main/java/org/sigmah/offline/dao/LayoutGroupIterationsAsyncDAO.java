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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;

import org.sigmah.offline.indexeddb.Cursor;
import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.LayoutGroupIterationJS;
import org.sigmah.offline.js.UpdateLayoutGroupIterationsJS;
import org.sigmah.offline.sync.SuccessCallback;
import org.sigmah.shared.command.UpdateLayoutGroupIterations;
import org.sigmah.shared.command.UpdateLayoutGroupIterations.IterationChange;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.layout.LayoutGroupIterationDTO;

public class LayoutGroupIterationsAsyncDAO extends AbstractUserDatabaseAsyncDAO<LayoutGroupIterationDTO, LayoutGroupIterationJS> {
  @Override
  public LayoutGroupIterationJS toJavaScriptObject(LayoutGroupIterationDTO layoutGroupIterationDTO) {
    return LayoutGroupIterationJS.toJavaScript(layoutGroupIterationDTO);
  }

  @Override
  public LayoutGroupIterationDTO toJavaObject(LayoutGroupIterationJS layoutGroupIterationJS) {
    return layoutGroupIterationJS.toDTO();
  }

  public void saveOrUpdate(final UpdateLayoutGroupIterations updateLayoutGroupIterations, final AsyncCallback<ListResult<IterationChange>> callback) {
    openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler<Store>() {

      @Override
      public void onTransaction(Transaction<Store> transaction) {
        saveOrUpdate(updateLayoutGroupIterations, callback, transaction);
      }
    });
  }
  
  public void getListResult(final int containerId, final int layoutGroupId, final int amendmentId,
			final AsyncCallback<ListResult<LayoutGroupIterationDTO>> callback) {
	  
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {

			@Override
			public void onTransaction(Transaction<Store> transaction) {
				final ArrayList<LayoutGroupIterationDTO> ts = new ArrayList<LayoutGroupIterationDTO>();
				
				final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());
				
				objectStore.openCursor().addCallback(new SuccessCallback<Request>(callback) {

					@Override
					public void onSuccess(Request result) {
						final Cursor cursor = result.getResult();
						if (cursor != null) {
							final LayoutGroupIterationJS js = cursor.getValue();
							if(js != null && amendmentId == -1 && containerId == js.getContainerId() && layoutGroupId == js.getLayoutGroup().getId()) {
							  ts.add(js.toDTO());
							}
							cursor.next();
						} else {
							callback.onSuccess(new ListResult<LayoutGroupIterationDTO>(ts));
						}
					}
					
				});
			}
		});
		
	}
    

  public void saveOrUpdate(final UpdateLayoutGroupIterations updateLayoutGroupIterations, final AsyncCallback<ListResult<IterationChange>> callback, Transaction transaction) {
    final ObjectStore layoutGroupIterationObjectStore = transaction.getObjectStore(getRequiredStore());

    final UpdateLayoutGroupIterationsJS updateLayoutGroupIterationJS = UpdateLayoutGroupIterationsJS.toJavaScript(updateLayoutGroupIterations);
    layoutGroupIterationObjectStore.put(updateLayoutGroupIterationJS).addCallback(new AsyncCallback<Request>() {

      @Override
      public void onFailure(Throwable caught) {
        Log.error("Error while saving value " + updateLayoutGroupIterationJS.getId() + ".");
        callback.onFailure(caught);
      }

      @Override
      public void onSuccess(Request result) {
        Log.trace("Value " + updateLayoutGroupIterationJS.getId() + " has been successfully saved.");
        callback.onSuccess(null);
      }
    });
  }

  @Override
  public Store getRequiredStore() {
    return Store.LAYOUT_GROUP_ITERATION;
  }

}
