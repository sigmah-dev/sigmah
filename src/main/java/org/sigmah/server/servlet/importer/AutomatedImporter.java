package org.sigmah.server.servlet.importer;

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

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sigmah.client.ui.presenter.CreateProjectPresenter;
import org.sigmah.server.domain.User;
import org.sigmah.shared.command.AmendmentActionCommand;
import org.sigmah.shared.command.AutomatedImport;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.UpdateProject;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ElementExtractedValue;
import org.sigmah.shared.dto.ImportDetails;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.BudgetSubFieldDTO;
import org.sigmah.shared.dto.element.event.ValueEvent;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.referential.AmendmentAction;
import org.sigmah.shared.dto.referential.AutomatedImportStatus;
import org.sigmah.shared.dto.referential.BudgetSubFieldType;
import org.sigmah.shared.dto.referential.ContainerInformation;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;
import org.sigmah.shared.dto.referential.LogicalElementType;
import org.sigmah.shared.dto.referential.LogicalElementTypes;
import org.sigmah.shared.dto.referential.ProjectModelStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrap an importer to perform automatic and silent importation.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class AutomatedImporter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AutomatedImporter.class);
	
	/**
	 * Importer used to retrieve the data of the file.
	 */
	private final Importer importer;

	/**
	 * Creates a new automated importer with the given <code>importer</code>.
	 * <p>
	 * The importer must be initialized.
	 * 
	 * @param importer 
	 *          Importer to use.
	 * @see Importer#initialize() 
	 */
	public AutomatedImporter(Importer importer) {
		this.importer = importer;
	}
	
	/**
	 * Importation the data with the given configuration.
	 * 
	 * @param configuration
	 *          Import configuration.
	 * @return A list of status for 
	 */
	public List<BaseModelData> importCorrespondances(AutomatedImport configuration) {
		
		final ArrayList<BaseModelData> result = new ArrayList<>();
		
		while (importer.hasNext()) {
			final ImportDetails details = importer.next();
			
			switch (details.getEntityStatus()) {
			case PROJECT_FOUND_CODE:
			case ORGUNIT_FOUND_CODE:
				result.add(onContainerFound(details, configuration));
				break;
			case SEVERAL_PROJECTS_FOUND_CODE:
			case SEVERAL_ORGUNITS_FOUND_CODE:
				result.addAll(onSeveralContainersFound(details, configuration));
				break;
			case PROJECT_LOCKED_CODE:
				result.add(onProjectLocked(details, configuration));
				break;
			case PROJECT_NOT_FOUND_CODE:
				result.add(onProjectNotFound(details, configuration));
				break;
			case ORGUNIT_NOT_FOUND_CODE:
				result.add(onOrgUnitNotFound(details, configuration));
				break;
			default:
				throw new UnsupportedOperationException("Entity status '" + details.getEntityStatus() + "' is not supported.");
			}
		}
		
		return result;
	}

	/**
	 * Update the project/orgunit found.
	 * 
	 * @param details
	 *          Import details.
	 * @param configuration 
	 *          Import configuration.
	 * @return A pair containing information about the container before the
	 * update and the status of the importation.
	 */
	private BaseModelData onContainerFound(final ImportDetails details, final AutomatedImport configuration) {
		
		final Map.Entry<EntityDTO<Integer>, List<ElementExtractedValue>> singleEntry = details.getEntitiesToImport().entrySet().iterator().next();
		final EntityDTO<Integer> container = singleEntry.getKey();
		
		updateContainerWithDetails(container, singleEntry.getValue(), configuration.getFileName());
		
		return toBaseModelData(container, AutomatedImportStatus.UPDATED);
	}
	
	/**
	 * Update every project/orgunit if necessary.
	 * 
	 * @param details
	 *          Import details.
	 * @param configuration 
	 *          Import configuration.
	 * @return A list of pairs containing information about the containers 
	 * before the updates and the status of the importation.
	 */
	private List<BaseModelData> onSeveralContainersFound(final ImportDetails details, final AutomatedImport configuration) {
		
		final ArrayList<BaseModelData> result = new ArrayList<>();
		
		if (configuration.isUpdateAllMatches()) {
			for (final Map.Entry<EntityDTO<Integer>, List<ElementExtractedValue>> entry : details.getEntitiesToImport().entrySet()) {
				final EntityDTO<Integer> container = entry.getKey();
				updateContainerWithDetails(container, entry.getValue(), configuration.getFileName());
				result.add(toBaseModelData(container, AutomatedImportStatus.UPDATED));
			}
		} else {
			for (final Map.Entry<EntityDTO<Integer>, List<ElementExtractedValue>> entry : details.getEntitiesToImport().entrySet()) {
				result.add(toBaseModelData(entry.getKey(), AutomatedImportStatus.ABIGUOUS));
			}
		}
		
		return result;
	}
	
	/**
	 * Unlock the project if necessary and if the user has the required accesses
	 * and update it.
	 * 
	 * @param details
	 *          Import details.
	 * @param configuration 
	 *          Import configuration.
	 * @return A pair containing information about the container before the
	 * update and the status of the importation.
	 */
	private BaseModelData onProjectLocked(final ImportDetails details, final AutomatedImport configuration) {
		
		AutomatedImportStatus status;
		
		final Map.Entry<EntityDTO<Integer>, List<ElementExtractedValue>> singleEntry = details.getEntitiesToImport().entrySet().iterator().next();
		final EntityDTO<Integer> container = singleEntry.getKey();
		
		if (configuration.isUnlockProjectCores()) {
			try {
				importer.getExecutionContext().execute(new AmendmentActionCommand(singleEntry.getKey().getId(), AmendmentAction.UNLOCK));
				updateContainerWithDetails(container, singleEntry.getValue(), configuration.getFileName());
				status = AutomatedImportStatus.UNLOCKED_AND_UPDATED;
			} catch (CommandException e) {
				LOGGER.warn("An error occured while trying to unlock the project " + container, e);
				status = AutomatedImportStatus.UNLOCK_FAILED;
			}
		} else {
			status = AutomatedImportStatus.WAS_LOCKED;
		}
		
		return toBaseModelData(container, status);
	}
	
	/**
	 * Create the project if necessary and if the user has the required accesses
	 * and update it.
	 * 
	 * @param details
	 *          Import details.
	 * @param configuration 
	 *          Import configuration.
	 * @return A pair containing information about the container and the status 
	 * of the importation.
	 */
	private BaseModelData onProjectNotFound(final ImportDetails details, final AutomatedImport configuration) {
		
		AutomatedImportStatus status;
		
		final List<ElementExtractedValue> values = details.getEntitiesToImport().values().iterator().next();
		final Map<String, Object> properties = toBasicProperties(values);
		
		ProjectDTO project = new ProjectDTO();
		project.setName((String) properties.get(ProjectDTO.NAME));
		project.setFullName((String) properties.get(ProjectDTO.FULL_NAME));
		
		if (configuration.isCreateProjects()) {
			properties.putAll(toProjectCreationProperties(details));
			
			try {
				project = createProjectWithProperties(properties);
				updateContainerWithDetails(project, values, configuration.getFileName());
				status = AutomatedImportStatus.CREATED_AND_UPDATED;
			} catch (CommandException ex) {
				LOGGER.warn("An error occured while trying to create a new project.", ex);
				status = AutomatedImportStatus.CREATION_FAILED;
			}
		} else {
			status = AutomatedImportStatus.NOT_FOUND;
		}
		
		return toBaseModelData(project, status);
	}
	
	/**
	 * Mark the given organizational unit as not found.
	 * 
	 * @param details
	 *          Import details.
	 * @param configuration
	 *          Import configuration.
	 * @return A pair containing information about the container and the status 
	 * of the importation.
	 */
	private BaseModelData onOrgUnitNotFound(final ImportDetails details, final AutomatedImport configuration) {
		
		final List<ElementExtractedValue> values = details.getEntitiesToImport().values().iterator().next();
		final Map<String, Object> properties = toBasicProperties(values);
		
		final OrgUnitDTO orgUnit = new OrgUnitDTO();
		orgUnit.setName((String) properties.get(OrgUnitDTO.NAME));
		orgUnit.setFullName((String) properties.get(OrgUnitDTO.FULL_NAME));
		
		return toBaseModelData(orgUnit, AutomatedImportStatus.NOT_FOUND);
	}
	
	// --
	// ENTITY CREATION AND UPDATE.
	// --

	/**
	 * Creates a new project with the given properties.
	 * 
	 * @param projectProperties
	 *          Properties of the project to create.
	 * @return The new project.
	 * @throws CommandException
	 *          If an error occured during the creation of the project.
	 */
	private ProjectDTO createProjectWithProperties(final Map<String, Object> projectProperties) throws CommandException {
		
		final CreateResult createResult = importer.getExecutionContext().execute(new CreateEntity(ProjectDTO.ENTITY_NAME, projectProperties));
		return (ProjectDTO) createResult.getEntity();
	}
	
	/**
	 * Update the given container with given elements.
	 * 
	 * @param container
	 *          Container to update (can be a project or an org unit).
	 * @param extractedValues
	 *          Extracted values to set.
	 * @param fileName 
	 *          Name of the imported file.
	 */
	private void updateContainerWithDetails(final EntityDTO<Integer> container, final List<ElementExtractedValue> extractedValues, final String fileName) {
		
		final ArrayList<ValueEvent> values = new ArrayList<>();
		for (final ElementExtractedValue value : extractedValues) {
			final ValueEvent event = value != null ? value.toValueEvent() : null;
			
			if (event != null) {
				values.add(event);
			}
		}
		
		final UpdateProject updateProject = new UpdateProject(container.getId(), values, "Imported from file '" + fileName + "'.");
		try {
			importer.getExecutionContext().execute(updateProject);
		} catch (CommandException ex) {
			LOGGER.error("An error occured while importing values for the project #" + container.getId() + ".", ex);
		}
	}
	
	// --
	// UTILITY METHODS.
	// --
	
	/**
	 * Extract the basic properties of a project/orgunit (code, title and 
	 * budget) from the given values.
	 * 
	 * @param values
	 *          Extracted values.
	 * @return A map of the basic properties of a project/orgunit.
	 */
	private Map<String, Object> toBasicProperties(final List<ElementExtractedValue> values) {
		
		final HashMap<String, Object> projectProperties = new HashMap<String, Object>();
		
		for (final ElementExtractedValue extractedValue : values) {
			final LogicalElementType type = LogicalElementTypes.of(extractedValue.getElement());
			final DefaultFlexibleElementType defaultType = type.toDefaultFlexibleElementType();
			
			if (defaultType != null) switch (defaultType) {
				case CODE:
					projectProperties.put(ProjectDTO.NAME, extractedValue.getNewValue());
					break;
				case TITLE:
					projectProperties.put(ProjectDTO.FULL_NAME, extractedValue.getNewValue());
					break;
				case BUDGET:
					final BudgetElementDTO budgetElement = (BudgetElementDTO) extractedValue.getElement();
					for (BudgetSubFieldDTO budgetSubField : budgetElement.getBudgetSubFields()) {
						final Double value = (Double) extractedValue.getNewBudgetValues().get(budgetSubField.getId());

						if (budgetSubField.getType() == BudgetSubFieldType.PLANNED && value != null) {
							projectProperties.put(ProjectDTO.BUDGET, value);
						}
					}
					break;
				default:
					break;
			}
		}
		
		return projectProperties;
	}
	
	/**
	 * Retrieve the properties required to create a new project.
	 * <p>
	 * The properties are:<ul>
	 * <li>Creation mode (project or test project)</li>
	 * <li>Project model identifier</li>
	 * <li>Organizational unit identifier</li>
	 * <li>Name of the calendar</li>
	 * </ul>
	 * 
	 * @param details
	 *          Importation details.
	 * @return A map of the properties required to create a new project.
	 */
	private Map<String, Object> toProjectCreationProperties(final ImportDetails details) {
		
		final HashMap<String, Object> projectProperties = new HashMap<>();
		
		final CreateProjectPresenter.Mode creationMode = details.getModelStatus() == ProjectModelStatus.DRAFT ?
				CreateProjectPresenter.Mode.TEST_PROJECT :
				CreateProjectPresenter.Mode.PROJECT;
		
		projectProperties.put(ProjectDTO.MODEL_ID, getProjectModelIdForName(details.getModelName()));
		projectProperties.put(ProjectDTO.ORG_UNIT_ID, getOrgUnitId());
		projectProperties.put(ProjectDTO.CALENDAR_NAME, i18n("calendarDefaultName"));
		projectProperties.put(ProjectDTO.CREATION_MODE, creationMode);
		
		return projectProperties;
	}

	/**
	 * Creates a new instance of <code>ContainerInformation</code> from the
	 * given entity.
	 * 
	 * @param container
	 *          Container to use (can be a project or an org unit).
	 * @return A new instance of <code>ContainerInformation</code>.
	 */
	private ContainerInformation toContainerInformation(final EntityDTO<Integer> container) {
		
		if (container instanceof ProjectDTO) {
			final ProjectDTO project = (ProjectDTO) container;
			return new ContainerInformation(project.getId(), project.getName(), project.getFullName(), true);
		} else if (container instanceof OrgUnitDTO) {
			final OrgUnitDTO orgUnit = (OrgUnitDTO) container;
			return new ContainerInformation(orgUnit.getId(), orgUnit.getName(), orgUnit.getFullName(), false);
		} else {
			throw new IllegalArgumentException("Container should either be a project or an org unit.");
		}
	}
	
	private BaseModelData toBaseModelData(final EntityDTO<Integer> container, final AutomatedImportStatus status) {
		
		final BaseModelData modelData = new BaseModelData();
		
		final ModelData source = (ModelData) container;
		
		modelData.set(ProjectDTO.ID, container.getId());
		modelData.set(ProjectDTO.NAME, source.get(ProjectDTO.NAME));
		modelData.set(ProjectDTO.FULL_NAME, source.get(ProjectDTO.FULL_NAME));
		modelData.set("entityName", container.getEntityName());
		modelData.set("status", status);
		
		return modelData;
	}
	
	/**
	 * Find the identifier of one of the project model that has the given name.
	 * If multiple models have the same name, result of this method is random.
	 * 
	 * @param projectModelName
	 *          Name of the project model to search.
	 * @return the identifier of a project model named with the given name.
	 */
	private Integer getProjectModelIdForName(final String projectModelName) {
		final List<Integer> projectModels = importer.em().createQuery("SELECT pm.id FROM ProjectModel AS pm WHERE pm.name = :name", Integer.class)
				.setParameter("name", projectModelName)
				.getResultList();
		
		if (projectModels.isEmpty()) {
			throw new IllegalArgumentException("Project model '" + projectModelName + "' was not found.");
		} else if (projectModels.size() > 1) {
			LOGGER.warn("Multiple project models with name '" + projectModelName + "' exists, using #" + projectModels.get(0) + " for the creation.");
		}
		return projectModels.get(0);
	}
	
	/**
	 * Find the identifier of the organizational unit of the current user.
	 * 
	 * @return the identifier of the orgunit of the current user.
	 */
	private String getOrgUnitId() {
		final User user = importer.getExecutionContext().getUser();
		final Integer orgUnitId = user.getOrgUnitWithProfiles().getOrgUnit().getId();
		
		if (orgUnitId != null) {
			return orgUnitId.toString();
		} else {
			throw new IllegalArgumentException("Current user has no organizational unit.");
		}
	}
	
	/**
	 * Find the translate value for the given <code>key</code> using the 
	 * language of the current user.
	 * 
	 * @param key
	 *          Key of the message to translate.
	 * @return The translated key.
	 */
	private String i18n(String key) {
		return importer.getTranslator().t(importer.getExecutionContext().getLanguage(), key);
	}
	
}
