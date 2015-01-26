package org.sigmah.server.service;

import javax.persistence.Query;

import org.sigmah.client.util.AdminUtil;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.category.CategoryType;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dto.category.CategoryTypeDTO;
import org.sigmah.shared.dto.referential.CategoryIcon;

import com.google.inject.Singleton;

/**
 * Create privacy group policy.
 * 
 * @author nrebiai
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class CategoryTypeService extends AbstractEntityService<CategoryType, Integer, CategoryTypeDTO> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CategoryType create(final PropertyMap properties, final UserExecutionContext context) {

		final String name = (String) properties.get(AdminUtil.PROP_CATEGORY_TYPE_NAME);
		final CategoryIcon icon = (CategoryIcon) properties.get(AdminUtil.PROP_CATEGORY_TYPE_ICON);
		final User executingUser = context.getUser();

		if (name == null || icon == null) {
			throw new IllegalArgumentException("Invalid argument.");
		}

		CategoryType categoryToPersist = null;

		// Saves CategoryType.
		final Query query = em().createQuery("SELECT c FROM CategoryType c WHERE c.label = :name and c.organization.id = :orgid ORDER BY c.id");
		query.setParameter("orgid", executingUser.getOrganization().getId());
		query.setParameter("name", name);

		try {

			if (query.getSingleResult() != null) {
				categoryToPersist = (CategoryType) query.getSingleResult();
				categoryToPersist.setLabel(name);
				categoryToPersist.setOrganization(executingUser.getOrganization());
				categoryToPersist.setIcon(icon);
				categoryToPersist = em().merge(categoryToPersist);
			} else {
				categoryToPersist = new CategoryType();
				categoryToPersist.setLabel(name);
				categoryToPersist.setIcon(icon);
				categoryToPersist.setOrganization(executingUser.getOrganization());
				em().persist(categoryToPersist);
			}

		} catch (final Exception e) {
			categoryToPersist = new CategoryType();
			categoryToPersist.setLabel(name);
			categoryToPersist.setIcon(icon);
			categoryToPersist.setOrganization(executingUser.getOrganization());
			em().persist(categoryToPersist);
		}

		return categoryToPersist;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CategoryType update(Integer entityId, PropertyMap changes, final UserExecutionContext context) {
		throw new UnsupportedOperationException("Policy update method is not implemented for '" + entityClass.getSimpleName() + "'.");
	}

}
