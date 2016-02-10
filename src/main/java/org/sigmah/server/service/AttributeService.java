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
import org.sigmah.server.domain.Attribute;
import org.sigmah.server.domain.AttributeGroup;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dto.AttributeDTO;

import com.google.inject.Singleton;

/**
 * {@link Attribute} corresponding service implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class AttributeService extends AbstractEntityService<Attribute, Integer, AttributeDTO> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Attribute create(final PropertyMap properties, final UserExecutionContext context) {

		final Attribute attribute = new Attribute();
		attribute.setGroup(em().getReference(AttributeGroup.class, properties.get("attributeGroupId")));

		updateAttributeProperties(properties, attribute);

		em().persist(attribute);

		return attribute;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Attribute update(final Integer entityId, final PropertyMap changes, final UserExecutionContext context) {

		final Attribute attribute = em().find(Attribute.class, entityId);

		// TODO: decide where attributes belong and how to manage them
		// assertDesignPriviledges(user, attribute.get);

		updateAttributeProperties(changes, attribute);

		return attribute;
	}

	private static final void updateAttributeProperties(PropertyMap changes, Attribute attribute) {

		if (changes.containsKey("name")) {
			attribute.setName((String) changes.get("name"));
		}

		if (changes.containsKey("sortOrder")) {
			attribute.setSortOrder((Integer) changes.get("sortOrder"));
		}

		// TODO: update lastSchemaUpdate
	}

}
