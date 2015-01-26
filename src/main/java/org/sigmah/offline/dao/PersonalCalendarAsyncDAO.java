package org.sigmah.offline.dao;

import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.PersonalCalendarJS;
import org.sigmah.shared.command.result.Calendar;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class PersonalCalendarAsyncDAO extends AbstractAsyncDAO<Calendar> {

	@Override
	public void saveOrUpdate(final Calendar t, final AsyncCallback<Calendar> callback, Transaction transaction) {
		final ObjectStore store = transaction.getObjectStore(getRequiredStore());
		
		final PersonalCalendarJS personalCalendarJS = PersonalCalendarJS.toJavaScript(t);
		store.put(personalCalendarJS).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
				if(callback != null) {
					callback.onFailure(caught);
				}
            }

            @Override
            public void onSuccess(Request result) {
				if(callback != null) {
					callback.onSuccess(t);
				}
            }
        });
	}

	@Override
	public void get(int id, final AsyncCallback<Calendar> callback, Transaction transaction) {
		if(transaction.useObjectFromCache(Calendar.class, id, callback)) {
			return;
		}
		
		final ObjectStore store = transaction.getObjectStore(getRequiredStore());
		store.get(id).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Request request) {
                final PersonalCalendarJS personalCalendarJS = (PersonalCalendarJS)request.getResult();
				final Calendar calendar = personalCalendarJS != null ? personalCalendarJS.toCalendar() : null;
				
				callback.onSuccess(calendar);
            }
        });
	}

	@Override
	public Store getRequiredStore() {
		return Store.PERSONAL_CALENDAR;
	}
}
