package org.sigmah.server.dao.util;

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
