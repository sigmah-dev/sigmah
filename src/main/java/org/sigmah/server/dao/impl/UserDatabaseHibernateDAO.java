package org.sigmah.server.dao.impl;

import java.util.List;

import org.sigmah.server.dao.UserDatabaseDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.UserDatabase;

/**
 * UserDatabaseDAO implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class UserDatabaseHibernateDAO extends AbstractDAO<UserDatabase, Integer> implements UserDatabaseDAO {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UserDatabase> queryAllUserDatabasesAlphabetically() {
		return em().createQuery("SELECT db FROM UserDatabase db ORDER BY db.name", entityClass).getResultList();
	}

}
