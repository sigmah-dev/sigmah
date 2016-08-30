package org.sigmah.server.dao.impl;

import java.util.List;
import javax.persistence.TypedQuery;
import org.sigmah.server.dao.ProjectFundingDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.ProjectFunding;
import org.sigmah.shared.dto.ProjectFundingDTO;
import static org.sigmah.shared.dto.ProjectFundingDTO.LinkedProjectType.FUNDED_PROJECT;
import static org.sigmah.shared.dto.ProjectFundingDTO.LinkedProjectType.FUNDING_PROJECT;

/**
 * {@link ProjectFundingDAO} implementation.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public class ProjectFundingHibernateDAO extends AbstractDAO<ProjectFunding, Integer> implements ProjectFundingDAO {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectFunding> getLinkedProjects(final Integer projectId, final ProjectFundingDTO.LinkedProjectType linkedProjectType) {
		final String queryTerm;
		switch (linkedProjectType) {
			case FUNDING_PROJECT:
				queryTerm = "funding";
				break;

			case FUNDED_PROJECT:
				queryTerm = "funded";
				break;

			default:
				throw new UnsupportedOperationException("Given linked project type is unsupported: " + linkedProjectType);
		}

		final TypedQuery<ProjectFunding> query = em().createQuery("SELECT pf FROM Project p, IN(p." + queryTerm + ") pf WHERE p.id = :projectId", ProjectFunding.class);
		query.setParameter("projectId", projectId);

		return query.getResultList();
	}
	
}
