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

import com.google.inject.Singleton;

import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.js.UserUnitsResultJS;
import org.sigmah.shared.command.result.UserUnitsResult;

@Singleton
public class UserUnitAsyncDAO extends AbstractUserDatabaseAsyncDAO<UserUnitsResult, UserUnitsResultJS> {

	@Override
	public Store getRequiredStore() {
		return Store.USER_UNITS_RESULT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserUnitsResultJS toJavaScriptObject(UserUnitsResult t) {
		return UserUnitsResultJS.toJavaScript(t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserUnitsResult toJavaObject(UserUnitsResultJS js) {
		return js.toDTO();
	}
}
