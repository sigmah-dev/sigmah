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
import org.sigmah.server.domain.category.CategoryType;
import org.sigmah.server.domain.element.BudgetElement;
import org.sigmah.server.domain.element.BudgetSubField;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.element.QuestionChoiceElement;
import org.sigmah.server.domain.element.QuestionElement;
import org.sigmah.server.domain.layout.Layout;
import org.sigmah.server.domain.layout.LayoutConstraint;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;

import javax.persistence.EntityManager;
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
		LayoutGroup groupToPersist = mapper.map(layoutGroupDTOToPersist, new LayoutGroup());

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

	public void saveLayoutGroups(List<LayoutGroup> layoutGroups) {
		EntityManager entityManager = em();
		if (layoutGroups == null) {
			return;
		}
		for (LayoutGroup layoutGroup : layoutGroups) {
			List<LayoutConstraint> layoutConstraints = layoutGroup.getConstraints();
			if (layoutConstraints == null) {
				continue;
			}
			for (LayoutConstraint layoutConstraint : layoutConstraints) {
				if (layoutConstraint.getElement() == null) {
					continue;
				}
				if (layoutConstraint.getElement() instanceof QuestionElement) {
					List<QuestionChoiceElement> questionChoiceElements = ((QuestionElement) layoutConstraint.getElement()).getChoices();
					CategoryType type = ((QuestionElement) layoutConstraint.getElement()).getCategoryType();
					if (questionChoiceElements != null || type != null) {
						FlexibleElement parent = layoutConstraint.getElement();
						((QuestionElement) parent).setChoices(null);
						((QuestionElement) parent).setCategoryType(null);
						entityManager.persist(parent);

						// Save QuestionChoiceElement with their QuestionElement parent(saved above)
						if (questionChoiceElements != null) {
							for (QuestionChoiceElement questionChoiceElement : questionChoiceElements) {
								if (questionChoiceElement != null) {
									questionChoiceElement.setId(null);
									questionChoiceElement.setParentQuestion((QuestionElement) parent);

									// BUGFIX #652: Removed the duplication of the category element.
									entityManager.persist(questionChoiceElement);
								}
							}
							// Set saved QuestionChoiceElement to QuestionElement parent and update it
							((QuestionElement) parent).setChoices(questionChoiceElements);
						}

						// Save the Category type of QuestionElement parent(saved above)
						if (type != null) {
							// Set the saved CategoryType to QuestionElement parent and update it
							((QuestionElement) parent).setCategoryType(type);
						}
						// Update the QuestionElement parent
						entityManager.merge(parent);
					} else {
						entityManager.persist(layoutConstraint.getElement());
					}
				} else if (layoutConstraint.getElement() instanceof BudgetElement) {
					List<BudgetSubField> budgetSubFields = ((BudgetElement) layoutConstraint.getElement()).getBudgetSubFields();
					if (budgetSubFields != null) {
						FlexibleElement parent = layoutConstraint.getElement();
						((BudgetElement) parent).setBudgetSubFields(null);
						((BudgetElement) parent).setRatioDividend(null);
						((BudgetElement) parent).setRatioDivisor(null);

						for (BudgetSubField budgetSubField : budgetSubFields) {
							if (budgetSubField != null) {
								budgetSubField.setId(null);
								if (budgetSubField.getType() != null) {
									switch (budgetSubField.getType()) {
										case PLANNED:
											((BudgetElement) parent).setRatioDivisor(budgetSubField);
											break;
										case RECEIVED:
											break;
										case SPENT:
											((BudgetElement) parent).setRatioDividend(budgetSubField);
											break;
										default:
											throw new IllegalStateException();
									}
								}
								budgetSubField.setBudgetElement((BudgetElement) parent);

								entityManager.persist(budgetSubField);
							}
						}
						entityManager.persist(parent);
					} else {
						entityManager.persist(layoutConstraint.getElement());
					}
				} else {
					entityManager.persist(layoutConstraint.getElement());
				}
			}
		}
	}
}
