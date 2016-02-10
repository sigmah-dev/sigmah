package org.sigmah.server.dao.impl;

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
import java.util.ArrayList;
import java.util.List;


import org.hibernate.jdbc.Work;
import org.sigmah.server.dao.SiteTableDAO;
import org.sigmah.server.dao.SqlSiteTableDAO;
import org.sigmah.server.dao.util.SQLDialect;
import org.sigmah.server.dao.util.SiteOrder;
import org.sigmah.server.dao.util.SiteProjectionBinder;
import org.sigmah.server.domain.User;

import com.google.inject.Inject;
import org.hibernate.Session;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.dao.base.EntityManagerProvider;
import org.sigmah.shared.util.Filter;

/**
 * Hibernate-friendly wrapper for the {@link org.sigmah.server.dao.SqlSiteTableDAO} that assures that connections are
 * used and cleaned up appropriately.
 * 
 * @author Alexander Bertram (akbertram@gmail.com)
 */
public class SiteTableHibernateDAO extends EntityManagerProvider implements SiteTableDAO {

	@Inject
	private SQLDialect dialect;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <RowT> List<RowT> query(final User user, final Filter filter, final List<SiteOrder> orderings, final SiteProjectionBinder<RowT> binder,
			final int retrieve, final int offset, final int limit) {
		final List<RowT> list = new ArrayList<RowT>();
		final Session session = AbstractDAO.getSession(em());
		session.doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				SqlSiteTableDAO dao = new SqlSiteTableDAO(connection, dialect);
				list.addAll(dao.query(user, filter, orderings, binder, retrieve, offset, limit));
			}
		});
		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int queryCount(final User user, final Filter filter) {
		final int result[] = new int[1];
		final Session session = AbstractDAO.getSession(em());
		session.doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				SqlSiteTableDAO dao = new SqlSiteTableDAO(connection, dialect);
				result[0] = dao.queryCount(user, filter);
			}
		});
		return result[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int queryPageNumber(final User user, final Filter filter, final List<SiteOrder> orderings, final int pageSize, final int siteId) {
		final int result[] = new int[1];
		final Session session = AbstractDAO.getSession(em());
		session.doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				SqlSiteTableDAO dao = new SqlSiteTableDAO(connection, dialect);
				result[0] = dao.queryPageNumber(user, filter, orderings, pageSize, siteId);
			}
		});
		return result[0];
	}
}
