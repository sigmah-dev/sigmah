package org.sigmah.server.dao.impl;

import java.util.List;

import org.sigmah.server.dao.CountryDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.Country;

/**
 * CountryDAO implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class CountryHibernateDAO extends AbstractDAO<Country, Integer> implements CountryDAO {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Country> queryAllCountriesAlphabetically() {
		return em().createQuery("SELECT c FROM Country c ORDER BY c.name", Country.class).getResultList();
	}

}
