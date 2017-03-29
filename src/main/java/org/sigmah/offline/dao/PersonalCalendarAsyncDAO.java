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

import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.js.PersonalCalendarJS;
import org.sigmah.shared.command.result.Calendar;

import com.google.inject.Singleton;

/**
 * Asynchronous DAO for saving and loading <code>Calendar</code> objects.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
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
