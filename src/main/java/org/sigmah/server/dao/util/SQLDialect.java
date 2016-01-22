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

/**
 * Utility class that detects and caches required properties of the SQL dialect in use. Note: Obviously hibernate
 * manages SQLDialects as well, but there are some additional database-specific parameters that we need, and this will
 * ultimately be used on the client as well.
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface SQLDialect {

	/**
	 * @param expression
	 *          A valid SQL expression.
	 * @return Returns the SQL expression that evaluates to the year of the given date {@code expression}
	 */
	String yearFunction(String expression);

	/**
	 * @param month
	 *          A valid SQL month value.
	 * @return Returns the SQL expression that evaluates to the month of the given date {@code expression}
	 */
	String monthFunction(String month);

	/**
	 * @param column
	 *          A valid SQL column name.
	 * @return Returns the SQL expression that evaluates to the quarter of the given date {@code expression}
	 */
	String quarterFunction(String column);

	/**
	 * @return true if it possible to disable referential integrity for this database
	 */
	boolean isPossibleToDisableReferentialIntegrity();

	/**
	 * @param disabled
	 *          true if the referential integrity checking should be disabled
	 * @return the statement which will disable or renable referential integrity checking
	 */
	String disableReferentialIntegrityStatement(boolean disabled);

	/**
	 * Returns the database-specific clause for limiting the size of the result list
	 * 
	 * @param offset
	 *          zero-based index of rows to start
	 * @param limit
	 *          maximum number of rows to return, or zero for no limit
	 * @return the database-specific {@code LIMIT} clause for the given arguments.
	 */
	String limitClause(int offset, int limit);

}
