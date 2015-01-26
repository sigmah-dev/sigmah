package org.sigmah.server.dao;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.reminder.ReminderList;

/**
 * Data Access Object for the {@link org.sigmah.server.domain.reminder.ReminderList} domain class.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface ReminderListDAO extends DAO<ReminderList, Integer> {

	/**
	 * Finds the given {@code projectId} related {@link ReminderList}.
	 * 
	 * @param projectId
	 *          The project id.
	 * @return The given {@code projectId} related {@link ReminderList}, or {@code null}.
	 */
	ReminderList findByProjectId(Integer projectId);

}
