package org.sigmah.server.service;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.ProjectFunding;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dto.ProjectFundingDTO;

import com.google.inject.Singleton;

/**
 * {@link ProjectFunding} corresponding service implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ProjectFundingService extends AbstractEntityService<ProjectFunding, Integer, ProjectFundingDTO> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectFunding create(final PropertyMap properties, final UserExecutionContext context) {

		// Retrieves parameters.
		final Object fundingId = properties.get(ProjectFundingDTO.FUNDING_ID);
		final Object fundedId = properties.get(ProjectFundingDTO.FUNDED_ID);
		final Object percentage = properties.get(ProjectFundingDTO.PERCENTAGE);

		// Retrieves projects.
		final Project fundingProject = em().find(Project.class, fundingId);
		final Project fundedProject = em().find(Project.class, fundedId);

		// Retrieves the eventual already existing link.
		final TypedQuery<ProjectFunding> query = em().createQuery("SELECT f FROM ProjectFunding f WHERE f.funding = :p1 AND f.funded = :p2", entityClass);
		query.setParameter("p1", fundingProject);
		query.setParameter("p2", fundedProject);

		ProjectFunding funding;

		// Updates or creates the link.
		try {

			funding = query.getSingleResult();

		} catch (final NoResultException e) {
			funding = new ProjectFunding();
			funding.setFunding(fundingProject);
			funding.setFunded(fundedProject);
		}

		funding.setPercentage((Double) percentage);

		// Saves.
		em().persist(funding);

		return funding;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectFunding update(final Integer entityId, final PropertyMap changes, final UserExecutionContext context) {

		ProjectFunding projectFunding = em().find(ProjectFunding.class, entityId);

		if (projectFunding != null) {
			projectFunding.setPercentage((Double) changes.get(ProjectFundingDTO.PERCENTAGE));
			projectFunding = em().merge(projectFunding);
		}

		return projectFunding;
	}

}
