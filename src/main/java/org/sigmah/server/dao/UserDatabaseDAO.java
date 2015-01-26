package org.sigmah.server.dao;

import java.util.List;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.UserDatabase;

/**
 * Data Access Object for {@link UserDatabase} domain classes.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface UserDatabaseDAO extends DAO<UserDatabase, Integer> {

	List<UserDatabase> queryAllUserDatabasesAlphabetically();

}
