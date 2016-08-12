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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.sigmah.server.dao.ReminderDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.reminder.Reminder;
import org.sigmah.server.domain.util.DomainFilters;

/**
 * {@link ReminderDAO} implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ReminderHibernateDAO extends AbstractDAO<Reminder, Integer> implements ReminderDAO {
	@Override
	public List<Reminder> findNotCompletedByProjectIds(Set<Integer> projectIds) {
		if (projectIds == null || projectIds.isEmpty()) {
			return Collections.emptyList();
		}

		// Disable the ActivityInfo filter on Userdatabase.
		DomainFilters.disableUserFilter(em());

		return em().createQuery(
			"SELECT r " +
			"FROM Project p " +
			"JOIN p.remindersList.reminders r " +
			"WHERE p.id IN (:projectIds) " +
			"AND r.completionDate IS NULL " +
			"AND r.deleted = false ",
			Reminder.class
		).setParameter("projectIds", projectIds).getResultList();
	}
}
