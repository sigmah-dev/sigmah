package org.sigmah.server.dao;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.reminder.MonitoredPointList;

/**
 * Data Access Object for the {@link org.sigmah.server.domain.reminder.MonitoredPointList} domain class.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface MonitoredPointListDAO extends DAO<MonitoredPointList, Integer> {

	/**
	 * Finds the given {@code projectId} related {@link MonitoredPointList}.
	 * 
	 * @param projectId
	 *          The project id.
	 * @return The given {@code projectId} related {@link MonitoredPointList}, or {@code null}.
	 */
	MonitoredPointList findByProjectId(Integer projectId);

}
