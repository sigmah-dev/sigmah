package org.sigmah.server.dao;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.Amendment;
import org.sigmah.server.domain.Project;

/**
 * Data Access Object for the {@link org.sigmah.server.domain.Amendment} domain class.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface AmendmentDAO extends DAO<Amendment, Integer> {

	/**
	 * Finds the given {@code amendmentId} corresponding {@link Project}.
	 * 
	 * @param amendmentId
	 *          The amendment id.
	 * @return the given {@code amendmentId} corresponding {@link Project}, or {@code null} if no project exists for this
	 *         id.
	 */
	Project findAmendmentProject(final Integer amendmentId);

}
