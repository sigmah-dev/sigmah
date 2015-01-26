package org.sigmah.server.dao;

import java.util.Collection;
import java.util.List;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.value.Value;

/**
 * Data Access Object for the {@link Value} domain class.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface ValueDAO extends DAO<Value, Integer> {

	/**
	 * Retrieves the {@link Value} list related to the given {@code orgUnits} entities.
	 * 
	 * @param orgUnits
	 *          The {@link OrgUnit} entities.
	 * @return The {@link Value} list related to the given {@code orgUnits} entities.
	 */
	List<Value> findValuesForOrgUnits(Collection<OrgUnit> orgUnits);

}
