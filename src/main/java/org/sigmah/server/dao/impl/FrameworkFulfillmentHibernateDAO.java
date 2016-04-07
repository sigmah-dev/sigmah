package org.sigmah.server.dao.impl;

import java.util.List;

import org.sigmah.server.dao.FrameworkDAO;
import org.sigmah.server.dao.FrameworkFulfillmentDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.Framework;
import org.sigmah.server.domain.FrameworkFulfillment;

public class FrameworkFulfillmentHibernateDAO extends AbstractDAO<FrameworkFulfillment, Integer> implements FrameworkFulfillmentDAO {
	@Override
	public List<FrameworkFulfillment> findByProjectModelId(Integer projectModelId) {
		return em().createQuery(
			"SELECT ff " +
			"FROM ProjectModel pm " +
			"JOIN pm.frameworkFulfillments ff " +
			"JOIN FETCH ff.frameworkElementImplementations fei " +
			"WHERE pm.id = :projectModelId " +
			"AND ff.framework.availabilityStatus = 'AVAILABLE' ",
			FrameworkFulfillment.class
		).setParameter("projectModelId", projectModelId).getResultList();
	}
}
