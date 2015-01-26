package org.sigmah.server.service;

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
