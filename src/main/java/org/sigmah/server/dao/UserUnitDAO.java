package org.sigmah.server.dao;

import java.util.List;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.profile.OrgUnitProfile;

/**
 * Data Access Object for the {@link org.sigmah.server.domain.User} domain class.
 * 
 * @author nrebiai
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface UserUnitDAO extends DAO<OrgUnitProfile, Integer> {

	OrgUnitProfile findOrgUnitProfileByUser(User user);

	boolean doesOrgUnitProfileExist(User user);

	List<User> findUsersByOrgUnit(List<OrgUnit> orgUnits);

}
