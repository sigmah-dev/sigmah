package org.sigmah.server.service;

import javax.persistence.Query;

import org.sigmah.client.util.AdminUtil;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.category.CategoryElement;
import org.sigmah.server.domain.category.CategoryType;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dto.category.CategoryElementDTO;
import org.sigmah.shared.dto.category.CategoryTypeDTO;

import com.google.inject.Singleton;

/**
 * Create privacy group policy.
 * 
 * @author nrebiai
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class CategoryElementService extends AbstractEntityService<CategoryElement, Integer, CategoryElementDTO> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CategoryElement create(final PropertyMap properties, final UserExecutionContext context) {

		CategoryElement categoryElementToPersist = null;
		final User executingUser = context.getUser();

		final String name = (String) properties.get(AdminUtil.PROP_CATEGORY_TYPE_NAME);
		final String label = (String) properties.get(AdminUtil.PROP_CATEGORY_ELEMENT_NAME);
		final String color = (String) properties.get(AdminUtil.PROP_CATEGORY_ELEMENT_COLOR);
		final CategoryTypeDTO category = (CategoryTypeDTO) properties.get(AdminUtil.PROP_CATEGORY_TYPE);

		if (label == null || color == null || category == null) {
			throw new IllegalArgumentException("Invalid argument.");
		}

		// Saves categoryElement.
		final CategoryType parentType = em().find(CategoryType.class, category.getId());
		if (parentType == null) {
			// TODO ?
		}

		final Query query =
				em().createQuery("SELECT c FROM CategoryElement c WHERE c.label = :name AND c.parentType = :category  and c.organization.id = :orgid ORDER BY c.id");
		query.setParameter("orgid", executingUser.getOrganization().getId());
		query.setParameter("name", name);
		query.setParameter("category", parentType);

		try {

			if (query.getSingleResult() != null) {
				categoryElementToPersist = (CategoryElement) query.getSingleResult();
				categoryElementToPersist.setLabel(label);
				categoryElementToPersist.setColor(color);
				categoryElementToPersist.setParentType(parentType);
				categoryElementToPersist.setOrganization(executingUser.getOrganization());
				categoryElementToPersist = em().merge(categoryElementToPersist);

			} else {
				categoryElementToPersist = new CategoryElement();
				categoryElementToPersist.setLabel(label);
				categoryElementToPersist.setColor(color);
				categoryElementToPersist.setOrganization(executingUser.getOrganization());
				categoryElementToPersist.setParentType(parentType);
				em().persist(categoryElementToPersist);
			}

		} catch (final Exception e) {
			categoryElementToPersist = new CategoryElement();
			categoryElementToPersist.setLabel(label);
			categoryElementToPersist.setColor(color);
			categoryElementToPersist.setOrganization(executingUser.getOrganization());
			categoryElementToPersist.setParentType(parentType);
			em().persist(categoryElementToPersist);
		}

		return categoryElementToPersist;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CategoryElement update(Integer entityId, PropertyMap changes, final UserExecutionContext context) {
		throw new UnsupportedOperationException("Policy update method is not implemented for '" + entityClass.getSimpleName() + "'.");
	}

}
