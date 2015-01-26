package org.sigmah.server.dao.impl;

import org.sigmah.server.dao.PersonalEventDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.calendar.PersonalEvent;

/**
 * PersonalEventDAO implementation.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class PersonalEventHibernateDAO extends AbstractDAO<PersonalEvent, Integer> implements PersonalEventDAO {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void merge(PersonalEvent event) {
		em().merge(event);
	}

}
