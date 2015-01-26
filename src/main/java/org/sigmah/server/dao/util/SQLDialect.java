package org.sigmah.server.dao.util;

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
