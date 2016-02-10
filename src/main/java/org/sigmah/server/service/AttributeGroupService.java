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

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Activity;
import org.sigmah.server.domain.AttributeGroup;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dto.AttributeGroupDTO;

import com.google.inject.Singleton;

/**
 * {@link AttributeGroup} corresponding service implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class AttributeGroupService extends AbstractEntityService<AttributeGroup, Integer, AttributeGroupDTO> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AttributeGroup create(final PropertyMap properties, final UserExecutionContext context) {

		final AttributeGroup group = new AttributeGroup();

		updateAttributeGroupProperties(group, properties);

		em().persist(group);

		final Activity activity = em().find(Activity.class, properties.get("activityId"));
		activity.getAttributeGroups().add(group);

		return group;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AttributeGroup update(final Integer entityId, final PropertyMap changes, final UserExecutionContext context) {

		final AttributeGroup group = em().find(AttributeGroup.class, entityId);

		updateAttributeGroupProperties(group, changes);

		return group;
	}

	private static final void updateAttributeGroupProperties(AttributeGroup group, PropertyMap changes) {

		if (changes.containsKey("name")) {
			group.setName((String) changes.get("name"));
		}

		if (changes.containsKey("multipleAllowed")) {
			group.setMultipleAllowed((Boolean) changes.get("multipleAllowed"));
		}

		if (changes.containsKey("sortOrder")) {
			group.setSortOrder((Integer) changes.get("sortOrder"));
		}

	}

}
