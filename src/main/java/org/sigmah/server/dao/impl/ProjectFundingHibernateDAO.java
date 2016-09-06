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
