package org.sigmah.server.service;

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

import org.sigmah.client.util.AdminUtil;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.layout.Layout;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javax.persistence.TypedQuery;

/**
 * Create layout group policy.
 * 
 * @author nrebiai
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class LayoutGroupService extends AbstractEntityService<LayoutGroup, Integer, LayoutGroupDTO> {

	/**
	 * Injected application mapper.
	 */
	@Inject
	private Mapper mapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LayoutGroup create(final PropertyMap properties, final UserExecutionContext context) {

		final LayoutGroupDTO layoutGroupDTOToPersist = (LayoutGroupDTO) properties.get(AdminUtil.PROP_NEW_GROUP_LAYOUT);
		LayoutGroup groupToPersist = mapper.map(layoutGroupDTOToPersist, LayoutGroup.class);

		if (layoutGroupDTOToPersist.getId() != null) {
			final int groupId = layoutGroupDTOToPersist.getId();

			// Find current order.
			final TypedQuery<Integer> rowQuery = em().createQuery("SELECT lg.row FROM LayoutGroup lg where lg.id = :id", Integer.class);
			rowQuery.setParameter("id", groupId);

			final int oldRow = rowQuery.getSingleResult();
			final int row = groupToPersist.getRow();
			
			// Update.
			groupToPersist = em().merge(groupToPersist);

			if (oldRow != row) {
				// Groups have been reordered.
				final int change = row > oldRow ? -1 : 1;
				final int impact = Math.max(row, oldRow);
				
				final Layout layout = em().find(Layout.class, groupToPersist.getParentLayout().getId());
				for (final LayoutGroup other : layout.getGroups()) {
					if (groupId != other.getId() && other.getRow() <= impact) {
						other.setRow(other.getRow() + change);
						em().persist(other);
					}
				}
			}

		} else {
			// New group.
			final Layout layout = groupToPersist.getParentLayout();
			
			// Moving down existing groups.
			for (final LayoutGroup layoutGroup : layout.getGroups()) {
				if (layoutGroup.getRow() >= groupToPersist.getRow()) {
					layoutGroup.setRow(layoutGroup.getRow() + 1);
				}
			}

			// Adding a row to the parent layout.
			layout.setRowsCount(layout.getRowsCount() + 1);
			em().merge(layout);
			
			em().persist(groupToPersist);
		}

		return groupToPersist;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LayoutGroup update(Integer entityId, PropertyMap changes, final UserExecutionContext context) {
		throw new UnsupportedOperationException("No policy update operation implemented for '" + entityClass.getSimpleName() + "' entity.");
	}

}
