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
