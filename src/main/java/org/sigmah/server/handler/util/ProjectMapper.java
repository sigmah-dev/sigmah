package org.sigmah.server.handler.util;

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


import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.TypedQuery;

import org.apache.commons.lang3.BooleanUtils;
import org.sigmah.server.dao.base.EntityManagerProvider;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Phase;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.ProjectFunding;
import org.sigmah.server.domain.ProjectModelVisibility;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.category.CategoryElement;
import org.sigmah.server.domain.category.CategoryType;
import org.sigmah.server.domain.element.BudgetElement;
import org.sigmah.server.domain.element.BudgetSubField;
import org.sigmah.server.domain.element.QuestionChoiceElement;
import org.sigmah.server.domain.value.Value;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectModelVisibilityDTO;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.category.CategoryElementDTO;
import org.sigmah.shared.dto.category.CategoryTypeDTO;
import org.sigmah.shared.dto.country.CountryDTO;
import org.sigmah.shared.util.ValueResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Responsible for the mapping of project objects.
 * 
 * @author tmi
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ProjectMapper extends EntityManagerProvider {

	/**
	 * Injected mapper.
	 */
	@Inject
	private Mapper mapper;

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ProjectMapper.class);

	/**
	 * <p>
	 * Map a project into a project light DTO.
	 * </p>
	 * <p>
	 * Populates the following fields of the returned {@link ProjectDTO} instance:
	 * <ul>
	 * <li>{@link EntityDTO#ID}</li>
	 * <li>{@link ProjectDTO#NAME}</li>
	 * <li>{@link ProjectDTO#FULL_NAME}</li>
	 * <li>{@link ProjectDTO#START_DATE}</li>
	 * <li>{@link ProjectDTO#END_DATE}</li>
	 * <li>{@link ProjectDTO#CLOSE_DATE}</li>
	 * <li>{@link ProjectDTO#ACTIVITY_ADVANCEMENT}</li>
	 * <li>{@link ProjectDTO#COUNTRY}</li>
	 * <li>{@link ProjectDTO#CURRENT_PHASE_NAME}</li>
	 * <li>{@link ProjectDTO#VISIBILITIES}</li>
	 * <li>{@link ProjectDTO#ORG_UNIT_NAME}</li>
	 * <li>{@link ProjectDTO#CATEGORY_ELEMENTS}</li>
	 * <li>{@link ProjectDTO#RATIO_DIVIDEND_LABEL}</li>
	 * <li>{@link ProjectDTO#RATIO_DIVIDEND_TYPE}</li>
	 * <li>{@link ProjectDTO#RATIO_DIVIDEND_VALUE}</li>
	 * <li>{@link ProjectDTO#RATIO_DIVISOR_LABEL}</li>
	 * <li>{@link ProjectDTO#RATIO_DIVISOR_TYPE}</li>
	 * <li>{@link ProjectDTO#RATIO_DIVISOR_VALUE}</li>
	 * <li>{@link ProjectDTO#PLANNED_BUDGET}</li>
	 * <li>{@link ProjectDTO#RECEIVED_BUDGET}</li>
	 * <li>{@link ProjectDTO#SPEND_BUDGET}</li>
	 * <li>{@link ProjectDTO#CHILDREN_PROJECTS}</li>
	 * <li>{@link ProjectDTO#FAVORITE_USERS}</li>
	 * </ul>
	 * </p>
	 * 
	 * @param project
	 *          The project.
	 * @param mapChildren
	 *          If the children projects must be retrieved.
	 * @return The light DTO.
	 */
	public ProjectDTO map(final Project project, final boolean mapChildren) {

		final StringBuilder sb = new StringBuilder();
		sb.append("Project mapping:\n");

		final ProjectDTO projectDTO = new ProjectDTO();

		// ---------------
		// -- SIMPLE FIELDS
		// ---------------

		long start = new Date().getTime();

		projectDTO.setId(project.getId());
		projectDTO.setName(project.getName());
		projectDTO.setFullName(project.getFullName());
		projectDTO.setStartDate(project.getStartDate());
		projectDTO.setEndDate(project.getEndDate());
		projectDTO.setCloseDate(project.getCloseDate());
		projectDTO.setActivityAdvancement(project.getActivityAdvancement());
		projectDTO.setCountry(mapper.map(project.getCountry(), new CountryDTO()));

		sb.append("- SIMPLE FIELDS: ");
		sb.append(new Date().getTime() - start);
		sb.append("ms.\n");

		// ---------------
		// -- CURRENT PHASE
		// ---------------

		start = new Date().getTime();

		final Phase currentPhase = project.getCurrentPhase();
		if (currentPhase != null) {
			projectDTO.setCurrentPhaseName(currentPhase.getPhaseModel().getName());
		}

		sb.append("- CURRENT PHASE: ");
		sb.append(new Date().getTime() - start);
		sb.append("ms.\n");

		// ---------------
		// -- VISIBILITIES
		// ---------------

		start = new Date().getTime();

		final ArrayList<ProjectModelVisibilityDTO> visibilities = new ArrayList<ProjectModelVisibilityDTO>();
		for (final ProjectModelVisibility v : project.getProjectModel().getVisibilities()) {
			final ProjectModelVisibilityDTO vDTO = new ProjectModelVisibilityDTO();
			vDTO.setId(v.getId());
			vDTO.setType(v.getType());
			vDTO.setOrganizationId(v.getOrganization().getId());
			visibilities.add(vDTO);
		}
		projectDTO.setVisibilities(visibilities);

		sb.append("- VISIBILITIES: ");
		sb.append(new Date().getTime() - start);
		sb.append("ms.\n");

		// ---------------
		// -- ORG UNIT
		// ---------------

		start = new Date().getTime();

		// Fill the org unit.
		final TypedQuery<OrgUnit> orgUnitQuery = em().createQuery("SELECT o FROM OrgUnit o WHERE :project MEMBER OF o.databases", OrgUnit.class);
		orgUnitQuery.setParameter("project", project);

		for (final OrgUnit orgUnit : orgUnitQuery.getResultList()) {
			projectDTO.setOrgUnitName(orgUnit.getName() + " - " + orgUnit.getFullName());
			break;
		}

		sb.append("- ORG UNIT: ");
		sb.append(new Date().getTime() - start);
		sb.append("ms.\n");

		// ---------------
		// -- CATEGORIES
		// ---------------

		start = new Date().getTime();

		final TypedQuery<Value> categoriesQuery =
				em().createQuery(
					"SELECT v FROM Value v JOIN v.element e WHERE v.containerId = :projectId AND "
						+ "e.id IN (SELECT q.id FROM QuestionElement q WHERE q.categoryType IS NOT NULL)", Value.class);
		categoriesQuery.setParameter("projectId", project.getId());

		final HashSet<CategoryElementDTO> elements = new HashSet<CategoryElementDTO>();

		for (final Value value : categoriesQuery.getResultList()) {

			List<Integer> values = ValueResultUtils.splitValuesAsInteger(value.getValue());
			if (!values.isEmpty()) {

				final TypedQuery<QuestionChoiceElement> choicesQuery =
						em().createQuery("SELECT c FROM QuestionChoiceElement c WHERE c.id IN (:ids)", QuestionChoiceElement.class);
				choicesQuery.setParameter("ids", ValueResultUtils.splitValuesAsInteger(value.getValue()));

				for (final QuestionChoiceElement choice : choicesQuery.getResultList()) {

					final CategoryType parent = choice.getCategoryElement().getParentType();
					final CategoryTypeDTO parentDTO = new CategoryTypeDTO();
					parentDTO.setId(parent.getId());
					parentDTO.setLabel(parent.getLabel());
					parentDTO.setIcon(parent.getIcon());

					final CategoryElement element = choice.getCategoryElement();
					final CategoryElementDTO elementDTO = new CategoryElementDTO();
					elementDTO.setId(element.getId());
					elementDTO.setLabel(element.getLabel());
					elementDTO.setColor(element.getColor());
					elementDTO.setParentCategoryDTO(parentDTO);

					elements.add(elementDTO);
				}
			}
		}
		projectDTO.setCategoryElements(elements);

		fillBudget(project, projectDTO);

		sb.append("- CATEGORIES: ");
		sb.append(new Date().getTime() - start);
		sb.append("ms.\n");

		// ---------------
		// -- CHILDREN
		// ---------------

		start = new Date().getTime();

		final ArrayList<ProjectDTO> children = new ArrayList<ProjectDTO>();

		// Maps the funding projects.
		if (mapChildren && project.getFunding() != null) {
			for (final ProjectFunding funding : project.getFunding()) {

				final Project pFunding = funding.getFunding();

				if (pFunding != null) {
					// Recursive call to retrieve the child (without its children).
					children.add(map(pFunding, false));
				}
			}
		}

		// Maps the funded projects.
		if (mapChildren && project.getFunded() != null) {
			for (final ProjectFunding funded : project.getFunded()) {

				final Project pFunded = funded.getFunded();

				if (pFunded != null) {
					// Recursive call to retrieve the child (without its children).
					children.add(map(pFunded, false));
				}
			}
		}

		projectDTO.setChildrenProjects(children);

		// ------------------
		// -- FAVORITE USERS
		// ------------------

		if (project.getFavoriteUsers() != null) {

			final Set<UserDTO> favoriteUsesSet = new HashSet<UserDTO>();

			for (final User u : project.getFavoriteUsers()) {
				// favoriteUsesSet.add(dozermapper.map(u, new UserDTO()));
				UserDTO uDTO = new UserDTO();
				uDTO.setId(u.getId());
				uDTO.setChangePasswordKey(u.getChangePasswordKey());
				uDTO.setDateChangePasswordKeyIssued(u.getDateChangePasswordKeyIssued());
				uDTO.setEmail(u.getEmail());
				uDTO.setFirstName(u.getFirstName());
				uDTO.setLocale(u.getLocale());
				uDTO.setActive(BooleanUtils.isNotFalse(u.getActive()));

				favoriteUsesSet.add(uDTO);
			}

			projectDTO.setFavoriteUsers(favoriteUsesSet);

		} else {
			projectDTO.setFavoriteUsers(null);
		}

		// ---END----

		sb.append("- CHILDREN: ");
		sb.append(new Date().getTime() - start);
		sb.append("ms.\n");

		if (LOG.isDebugEnabled()) {
			LOG.debug(sb.toString());
		}

		return projectDTO;
	}

	public void fillBudget(final Project project, final ProjectDTO projectDTO) throws NumberFormatException {
		final TypedQuery<Value> budgetValueQuery = em().createQuery("SELECT v FROM Value v WHERE v.containerId = :projectId AND v.element.id IN (:ids)", Value.class);
		budgetValueQuery.setParameter("projectId", project.getId());
		budgetValueQuery.setParameter("ids", em().createQuery("SELECT b.id FROM BudgetElement b").getResultList());
		
		final Iterator<Value> i = budgetValueQuery.getResultList().iterator();
		
		if (i.hasNext()) {
			final Value budgetValue = i.next();
			final BudgetElement budgetElement = (BudgetElement) budgetValue.getElement();
			final Map<Integer, String> values = ValueResultUtils.splitMapElements(budgetValue.getValue());
			
			if (budgetElement.getRatioDividend() != null) {
				if (budgetElement.getRatioDividend().getType() != null) {
					projectDTO.setRatioDividendType(budgetElement.getRatioDividend().getType());
				} else {
					projectDTO.setRatioDividendLabel(budgetElement.getRatioDividend().getLabel());
				}
				if (values.get(budgetElement.getRatioDividend().getId().intValue()) != null) {
					projectDTO.setRatioDividendValue(Double.parseDouble(values.get(budgetElement.getRatioDividend().getId().intValue())));
				}
			}
			
			if (budgetElement.getRatioDivisor() != null) {
				if (budgetElement.getRatioDivisor().getType() != null) {
					projectDTO.setRatioDivisorType(budgetElement.getRatioDivisor().getType());
				} else {
					projectDTO.setRatioDivisorLabel(budgetElement.getRatioDivisor().getLabel());
				}
				if (values.get(budgetElement.getRatioDivisor().getId().intValue()) != null) {
					projectDTO.setRatioDivisorValue(Double.parseDouble(values.get(budgetElement.getRatioDivisor().getId().intValue())));
				}
			}
			
			for (BudgetSubField budgetSubField : budgetElement.getBudgetSubFields()) {
				if (budgetSubField.getType() != null) {
					if (values.get(budgetSubField.getId().intValue()) != null) {
						switch (budgetSubField.getType()) {
							case PLANNED:
								projectDTO.setPlannedBudget(Double.parseDouble(values.get(budgetSubField.getId().intValue())));
								break;
							case RECEIVED:
								projectDTO.setReceivedBudget(Double.parseDouble(values.get(budgetSubField.getId().intValue())));
								break;
							case SPENT:
								projectDTO.setSpendBudget(Double.parseDouble(values.get(budgetSubField.getId().intValue())));
								break;
							default:
								break;
								
						}
					}
					
				}
			}
		}
	}

}
