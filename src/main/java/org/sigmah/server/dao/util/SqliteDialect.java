package org.sigmah.server.dao.util;

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

public class SqliteDialect implements SQLDialect {

	// TODO: implement date/time functions for pivot tables...
	// http://www.sqlite.org/cvstrac/wiki?p=DateAndTimeFunctions

	@Override
	public String yearFunction(String expression) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String monthFunction(String month) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String quarterFunction(String column) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isPossibleToDisableReferentialIntegrity() {
		return false;
	}

	@Override
	public String disableReferentialIntegrityStatement(boolean disabled) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String limitClause(int offset, int limit) {
		return "LIMIT " + limit + " OFFSET " + offset;
	}

}
