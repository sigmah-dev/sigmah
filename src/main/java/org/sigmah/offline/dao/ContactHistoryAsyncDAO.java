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

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.offline.indexeddb.Cursor;
import org.sigmah.offline.indexeddb.IDBKeyRange;
import org.sigmah.offline.indexeddb.OpenCursorRequest;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.ContactHistoryJS;
import org.sigmah.shared.command.result.ContactHistory;
import org.sigmah.shared.command.result.ListResult;

public class ContactHistoryAsyncDAO extends AbstractUserDatabaseAsyncDAO<ContactHistory, ContactHistoryJS> {
  @Override
  public ContactHistoryJS toJavaScriptObject(ContactHistory contactHistory) {
    return ContactHistoryJS.toJavaScript(contactHistory);
  }

  @Override
  public ContactHistory toJavaObject(ContactHistoryJS contactHistoryJS) {
    return contactHistoryJS.toDTO();
  }

  @Override
  public Store getRequiredStore() {
    return Store.CONTACT;
  }

  public void findByContactId(final Integer contactId, final AsyncCallback<ListResult<ContactHistory>> callback) {
    openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {
      @Override
      public void onTransaction(Transaction<Store> transaction) {
        findByContactId(contactId, callback, transaction);
      }
    });
  }

  private void findByContactId(Integer contactId, final AsyncCallback<ListResult<ContactHistory>> callback, Transaction<Store> transaction) {
    final List<ContactHistory> contactHistories = new ArrayList<ContactHistory>();
    final OpenCursorRequest openCursorRequest = transaction.getObjectStore(Store.CONTACT_HISTORY)
        .index("contactId")
        .openCursor(IDBKeyRange.only(contactId));
    openCursorRequest.addCallback(new AsyncCallback<Request>() {
      @Override
      public void onFailure(Throwable caught) {
        callback.onFailure(caught);
      }

      @Override
      public void onSuccess(Request result) {
        final Cursor cursor = openCursorRequest.getResult();
        if (cursor != null) {
          final ContactHistoryJS contactHistoryJS = cursor.getValue();
          contactHistories.add(contactHistoryJS.toDTO());
          cursor.next();
        } else {
          callback.onSuccess(new ListResult<ContactHistory>(contactHistories));
        }
      }
    });
  }
}
