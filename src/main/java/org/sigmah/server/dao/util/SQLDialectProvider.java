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

import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManager;

import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.jdbc.Work;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class SQLDialectProvider implements Provider<SQLDialect> {

	private SQLDialect dialect;

	@Inject
	public SQLDialectProvider(final Provider<EntityManager> emProvider) {
		final HibernateEntityManager hem = (HibernateEntityManager) emProvider.get();
		init(hem);
	}

	private SQLDialectProvider() {
	}

	public static SQLDialect from(EntityManager entityManager) {
		SQLDialectProvider provider = new SQLDialectProvider();
		provider.init((HibernateEntityManager) entityManager);
		return provider.get();
	}

	private void init(HibernateEntityManager hem) {
		hem.getSession().doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				String dbName = connection.getMetaData().getDatabaseProductName();
				if (dbName.equals(MSSQLDialect.PRODUCT_NAME)) {
					dialect = new MSSQLDialect();
				} else if (dbName.equals(PostgresDialect.PRODUCT_NAME)) {
					dialect = new PostgresDialect();
				} else if (dbName.equals(H2Dialect.PRODUCT_NAME)) {
					dialect = new H2Dialect();
				} else if (dbName.equals(MySQLDialect.PRODUCT_NAME)) {
					dialect = new MySQLDialect();
				} else {
					dialect = new DefaultDialect();
				}
			}
		});
	}

	@Override
	public SQLDialect get() {
		return dialect;
	}

	private static class DefaultDialect implements SQLDialect {

		@Override
		public String yearFunction(String column) {
			return "YEAR(" + column + ")";
		}

		@Override
		public String monthFunction(String column) {
			return "MONTH(" + column + ")";
		}

		@Override
		public String quarterFunction(String column) {
			return "QUARTER(" + column + ")";
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
			throw new UnsupportedOperationException();
		}
	}

	private static class MSSQLDialect extends DefaultDialect {

		public static final String PRODUCT_NAME = "Microsoft SQL Server";

		@Override
		public String quarterFunction(String column) {
			return "FLOOR((MONTH(" + column + ")-1)/3)+1";
		}
	}

	private static class MySQLDialect extends DefaultDialect {

		public static final String PRODUCT_NAME = "MySQL";

		@Override
		public boolean isPossibleToDisableReferentialIntegrity() {
			return true;
		}

		@Override
		public String disableReferentialIntegrityStatement(boolean disabled) {
			return "SET foreign_key_checks = " + (disabled ? "0" : "1");
		}

		@Override
		public String limitClause(int offset, int limit) {
			return new StringBuilder("LIMIT ").append(offset).append(',').append(limit == 0 ? Integer.MAX_VALUE : limit).toString();
		}
	}

	private static class PostgresDialect extends DefaultDialect {

		public static final String PRODUCT_NAME = "PostgreSQL";

		@Override
		public String yearFunction(String column) {
			return "EXTRACT(year FROM (" + column + "))";
		}

		@Override
		public String monthFunction(String column) {
			return "EXTRACT(month FROM (" + column + "))";
		}

		@Override
		public String quarterFunction(String column) {
			return "EXTRACT(quarter FROM (" + column + "))";
		}

		@Override
		public String limitClause(int offset, int limit) {
			return postgresStyleLimitClause(offset, limit);
		}
	}

	private static class H2Dialect extends DefaultDialect {

		public static final String PRODUCT_NAME = "H2";

		@Override
		public boolean isPossibleToDisableReferentialIntegrity() {
			return true;
		}

		@Override
		public String disableReferentialIntegrityStatement(boolean disabled) {
			return "SET REFERENTIAL_INTEGRITY " + (disabled ? "FALSE" : "TRUE");
		}

		@Override
		public String limitClause(int offset, int limit) {
			return postgresStyleLimitClause(offset, limit);
		}
	}

	private static String postgresStyleLimitClause(int offset, int limit) {
		return new StringBuilder("LIMIT ").append(limit == 0 ? "ALL" : limit).append(" OFFSET ").append(offset).toString();
	}

}
