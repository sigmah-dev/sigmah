package org.sigmah.offline.dao;

import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.js.PersonalCalendarJS;
import org.sigmah.shared.command.result.Calendar;

import com.google.inject.Singleton;

/**
 * Asynchronous DAO for saving and loading <code>Calendar</code> objects.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class PersonalCalendarAsyncDAO extends AbstractUserDatabaseAsyncDAO<Calendar, PersonalCalendarJS> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Store getRequiredStore() {
		return Store.PERSONAL_CALENDAR;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PersonalCalendarJS toJavaScriptObject(Calendar t) {
		return PersonalCalendarJS.toJavaScript(t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Calendar toJavaObject(PersonalCalendarJS js) {
		return js.toCalendar();
	}
	
}
