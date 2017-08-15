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

import java.util.List;
import javax.persistence.TypedQuery;
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
        
        @Override
    public List<PersonalEvent> findEventsByRefId(final Integer refId) {

        final TypedQuery<PersonalEvent> query = em().createQuery("SELECT pm FROM personalevent pm WHERE pm.calendarid = :refId", PersonalEvent.class);
        query.setParameter("calendarid", refId);

        return query.getResultList();
    }

}
