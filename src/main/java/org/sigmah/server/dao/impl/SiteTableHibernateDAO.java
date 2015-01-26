package org.sigmah.server.dao.impl;

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
