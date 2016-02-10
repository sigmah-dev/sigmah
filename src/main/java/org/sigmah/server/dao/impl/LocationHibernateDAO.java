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

import org.sigmah.server.dao.LocationDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.AdminEntity;
import org.sigmah.server.domain.Location;
import org.sigmah.server.domain.util.EntityConstants;

import com.google.inject.persist.Transactional;

/**
 * LocationDAO implementation.
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class LocationHibernateDAO extends AbstractDAO<Location, Integer> implements LocationDAO {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void updateAdminMembership(int locationId, int adminLevelId, int adminEntityId) {
		removeExistingRow(locationId, adminLevelId);
		addRow(locationId, adminEntityId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAdminMembership(int locationId, int adminEntityId) {
		addRow(locationId, adminEntityId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void removeMembership(int locationId, int adminLevelId) {
		removeExistingRow(locationId, adminLevelId);
	}

	// --------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// --------------------------------------------------------------------------------

	private void addRow(int locationId, int adminEntityId) {
		final Location location = em().find(Location.class, locationId);
		location.getAdminEntities().add(em().getReference(AdminEntity.class, adminEntityId));
	}

	private void removeExistingRow(int locationId, int adminLevelId) {

		final StringBuilder builder = new StringBuilder();

		builder.append("DELETE FROM ").append(EntityConstants.LOCATION_ADMIN_ENTITY_LINK_TABLE);
		builder.append(" WHERE ").append(EntityConstants.LOCATION_COLUMN_ID).append(" = ?1 ");
		builder.append(" AND ").append(EntityConstants.ADMIN_ENTITY_COLUMN_ID).append(" IN (");
		builder.append("   SELECT e.").append(EntityConstants.ADMIN_ENTITY_COLUMN_ID);
		builder.append("   FROM ").append(EntityConstants.ADMIN_ENTITY_TABLE).append(" e ");
		builder.append("   WHERE e.").append(EntityConstants.ADMIN_LEVEL_COLUMN_ID).append(" = ?2 ");
		builder.append(")");

		// TODO [DAO] Should use JPQL query.
		update(em().createNativeQuery(builder.toString()).setParameter(1, locationId).setParameter(2, adminLevelId));
	}
}
