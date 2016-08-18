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

import org.sigmah.server.dao.FrameworkDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.Framework;

public class FrameworkHibernateDAO extends AbstractDAO<Framework, Integer> implements FrameworkDAO {
	@Override
	public List<Framework> findAvailableFrameworksForOrganizationId(Integer organizationId) {
		return em().createQuery(
			"SELECT f " +
			"FROM Framework f " +
			"JOIN FETCH f.frameworkHierarchies fh " +
			"WHERE f.availabilityStatus = 'AVAILABLE' " +
			"AND f.organization.id = :organizationId ",
			Framework.class
		).setParameter("organizationId", organizationId).getResultList();
	}

	@Override
	public long countNotImplementedElementsByProjectModelId(Integer projectModelId) {
		return em().createQuery(
			"SELECT count(*) " +
			"FROM ProjectModel pm " +
			"INNER JOIN pm.frameworkFulfillments ff " +
			"LEFT JOIN ff.framework f " +
			"LEFT JOIN f.frameworkHierarchies fh " +
			"LEFT JOIN fh.frameworkElements fe " +
			"WHERE pm.id = :projectModelId " +
			"AND f.availabilityStatus = 'AVAILABLE' " +
			"AND fe.id NOT IN (" +
				"SELECT fei.frameworkElement.id " +
				"FROM ff.frameworkElementImplementations fei " +
			") ",
			Long.class
		).setParameter("projectModelId", projectModelId).getSingleResult();
	}
}
