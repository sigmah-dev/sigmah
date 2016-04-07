package org.sigmah.server.dao.impl;

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
