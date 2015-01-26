package org.sigmah.server.dao;

import java.util.List;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.Country;

/**
 * Data Access Object for {@link org.sigmah.server.domain.Country} objects.
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface CountryDAO extends DAO<Country, Integer> {

	/**
	 * Returns a list of Countries in alphabetical order. See {@link org.sigmah.server.domain.Country} for query
	 * definition
	 * 
	 * @return a list of Countries in alphabetical order
	 */
	List<Country> queryAllCountriesAlphabetically();

}
