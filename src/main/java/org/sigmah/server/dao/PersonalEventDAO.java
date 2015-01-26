package org.sigmah.server.dao;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.calendar.PersonalEvent;

/**
 * Personal even DAO interface.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface PersonalEventDAO extends DAO<PersonalEvent, Integer> {

	void merge(PersonalEvent event);

}
