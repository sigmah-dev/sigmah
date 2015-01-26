package org.sigmah.server.service;

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
