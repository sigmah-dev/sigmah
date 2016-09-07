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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.sigmah.server.dao.base.EntityManagerProvider;
import org.sigmah.server.domain.Country;
import org.sigmah.server.domain.HistoryToken;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.ProjectFunding;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.element.ComputationElement;
import org.sigmah.server.domain.element.DefaultFlexibleElement;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.layout.LayoutGroupIteration;
import org.sigmah.server.domain.util.Deleteable;
import org.sigmah.server.domain.value.TripletValue;
import org.sigmah.server.domain.value.Value;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.event.ValueEventWrapper;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;
import org.sigmah.shared.dto.referential.ValueEventChangeType;
import org.sigmah.shared.dto.value.TripletValueDTO;
import org.sigmah.shared.util.ValueResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service handling the update of Value objects.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
@Singleton
public class ValueService extends EntityManagerProvider {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ValueService.class);
	
	/**
	 * Mapper to transform domain objects in DTO.
	 */
	@Inject
	private Mapper mapper;
	
	@Inject
	private ComputationService computationService;
	
	public void saveValue(final String value, final FlexibleElement element, final Integer projectId, final Integer iterationId, final User user) {
		
		saveValue(value, new Date(), element, projectId, iterationId, user, null);
	}
	
	public void saveValue(final String value, final Date historyDate, final FlexibleElement element, final Integer projectId, final Integer iterationId, final User user, final String comment) {
		
		// Retrieving the current value
		final Value currentValue = retrieveOrCreateValue(projectId, element.getId(), iterationId, user);
		currentValue.setValue(value);
		
		// Store the value.
		em().merge(currentValue);
		
		// Historize the value.
		historize(historyDate, element, projectId, iterationId, user, value, comment);
		
		updateImpactedComputations(element, projectId, user);
	}
	
	public void saveValue(final String value, final boolean isProjectCountryChanged, final Date historyDate, final DefaultFlexibleElement element, final Integer projectId, final User user, final String comment) {
		
		// Saves the value and switch to the next value.
		final String oldValue = saveDefaultElement(projectId, element.getType(), value, isProjectCountryChanged);

		// Checks if the first value has already been historized or not.
		final TypedQuery<HistoryToken> query =
			em().createQuery("SELECT h FROM HistoryToken h WHERE h.elementId = :elementId AND h.projectId = :projectId", HistoryToken.class);
		query.setParameter("elementId", element.getId());
		query.setParameter("projectId", projectId);
		
		final List<HistoryToken> results = query.getResultList();

		if (results == null || results.isEmpty()) {
			final Date oldDate;
			final User oldOwner;
			
			final Project project = em().find(Project.class, projectId);
			if (project != null) {
				oldDate = project.getLastSchemaUpdate();
				oldOwner = project.getOwner();
			} else {
				oldDate = new Date(historyDate.getTime() - 1);
				oldOwner = null;
			}

			// Historize the first value.
			if (oldValue != null) {
				historize(oldDate, element, projectId, null, oldOwner, ValueEventChangeType.ADD, oldValue, null, null);
			}
		}

		// Historize the value.
		historize(historyDate, element, projectId, null, user, ValueEventChangeType.EDIT, value, null, comment);
	}
	
	public void saveValue(final TripletValueDTO value, final ValueEventChangeType changeType, final Date historyDate, final FlexibleElement element, final Integer projectId, final Integer iterationId, final User user, final String comment) {
		
		// Retrieving the current value
		final Value currentValue = retrieveOrCreateValue(projectId, element.getId(), iterationId, user);
		
		// The value of the element is a list of ids (default separated).
		final List<Integer> ids = ValueResultUtils.splitValuesAsInteger(currentValue.getValue());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("[execute] The current list of ids is : " + ids + ".");
		}
		
		// Cast the update value (as a DTO).
		switch (changeType) {
			case ADD:
				onAdd(value, ids, currentValue, historyDate, element, projectId, iterationId, user, comment);
				break;

			case REMOVE:
				if(!onDelete(value,  ids, currentValue, historyDate, element, projectId, iterationId, user, comment)) {
					// Do not save, the value hasn't been changed.
					return;
				}
				break;

			case EDIT:
				onEdit(value, historyDate, element, projectId, iterationId, user, comment);
				break;

			default:
				LOGGER.debug("[execute] Unknown command " + changeType + ".");
				break;
		}

		LOGGER.debug("[execute] The new list of ids is : " + ids + ".");
		
		// Store the value.
		em().merge(currentValue);
	}

	public void saveValue(Set<Integer> multivaluedIdsValue, ValueEventWrapper changeType, Date historyDate, FlexibleElement element, Integer projectId, Integer iterationId, User user, String comment) {

		// Retrieving the current value
		final Value currentValue = retrieveOrCreateValue(projectId, element.getId(), iterationId, user);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("[execute] Multivalued ids value case.");
		}

		Set<Integer> currentIds = new HashSet<>();
		if (currentValue != null && currentValue.getValue() != null && !currentValue.getValue().isEmpty()) {
			currentIds.addAll(ValueResultUtils.splitValuesAsInteger(currentValue.getValue()));
		}
		switch (changeType.getChangeType()) {
			case ADD:
				currentIds.addAll(multivaluedIdsValue);
				break;
			case REMOVE:
				currentIds.removeAll(multivaluedIdsValue);
				break;
			case EDIT:
				currentIds = multivaluedIdsValue;
				break;
			default:
				throw new IllegalStateException("Unknown ValueEventChangeType : " + changeType.getChangeType());
		}
		String serializedValue = ValueResultUtils.mergeElements(new ArrayList<Integer>(currentIds));
		currentValue.setValue(serializedValue);

		if (changeType.getChangeType() == ValueEventChangeType.EDIT) {
			historize(historyDate, element, projectId, iterationId, user, changeType.getChangeType(), serializedValue, null, comment);
		} else {
			for (Integer id : multivaluedIdsValue) {
				historize(historyDate, element, projectId, iterationId, user, changeType.getChangeType(), String.valueOf(id), null, comment);
			}
		}
	}
	
	/**
     * Finds the current value of the given element from the database and returns it as HTML.
     * 
     * @param projectId
     *          Identifier of the project.
     * @param element
     *          Element to search.
     * @return The value of the given element.
     */
	public String getCurrentValueFormatted(int projectId, FlexibleElementDTO element) {
        
		final Value value = retrieveCurrentValue(projectId, element.getId(), null);
		
		if(value != null) {
			return element.toHTML(value.getValue());
		} else {
			return "";
		}
	}
	
	/**
	 * Retrieves the value for the given project and the given element.
	 * If there isn't a value yet, it will be created.
	 * 
	 * @param projectId
	 *          The project id.
	 * @param elementId
	 *          The source element id.
	 * @param user
	 *          The user which launch the command.
	 * @return The value.
	 */
	public Value retrieveOrCreateValue(int projectId, Integer elementId, User user) {
		return retrieveOrCreateValue(projectId, elementId, null, user);
	}

	public Value retrieveOrCreateValue(int projectId, Integer elementId, Integer iterationId, User user) {

		// Retrieving the current value
		Value currentValue = retrieveCurrentValue(projectId, elementId, iterationId);

		// Update operation.
		if (currentValue != null) {
			LOGGER.debug("[execute] Retrieves a value for element #{0}.", elementId);
			currentValue.setLastModificationAction('U');
		}
		// Create operation
		else {
			LOGGER.debug("[execute] Creates a value for element #{0}.", elementId);

			currentValue = new Value();
			currentValue.setLastModificationAction('C');

			// Parent element
			final FlexibleElement element = em().find(FlexibleElement.class, elementId);
			currentValue.setElement(element);

			// Container
			currentValue.setContainerId(projectId);

			// Iteration
			if(iterationId != null) {
				final LayoutGroupIteration iteration = em().find(LayoutGroupIteration.class, iterationId);
				currentValue.setLayoutGroupIteration(iteration);
			}
		}

		// Updates the value's fields.
		currentValue.setLastModificationDate(new Date());
		currentValue.setLastModificationUser(user);

		return currentValue;
	}
	
	/**
	 * Retrieves the value for the given project and the given element but 
	 * don't create an empty value if none exists.
	 * 
	 * @param projectId
	 *          The project id.
	 * @param elementId
	 *          The source element id.
	 * @return  The value or <code>null</code> if not found.
	 */
	public Value retrieveCurrentValue(int projectId, Integer elementId, Integer iterationId) {
		final Query query;


		if(iterationId == null) {
			query = em().createQuery("SELECT v FROM Value v WHERE v.containerId = :projectId and v.element.id = :elementId and v.layoutGroupIteration.id IS NULL");
			query.setParameter("projectId", projectId);
			query.setParameter("elementId", elementId);
		} else {
			query = em().createQuery("SELECT v FROM Value v WHERE v.element.id = :elementId AND v.layoutGroupIteration.id = :iterationId");
			query.setParameter("elementId", elementId);
			query.setParameter("iterationId", iterationId);
		}

		Value currentValue = null;

		try {
			currentValue = (Value) query.getSingleResult();
		} catch (NoResultException nre) {
			// No current value
		}

		return currentValue;
	}
	
	/**
	 * Updates the current project with the new value of a default element.
	 * 
	 * @param id
	 *          The project id.
	 * @param type
	 *          The type of the default element.
	 * @param value
	 *          The new value.
	 * @return The old value.
	 */
	private String saveDefaultElement(int id, DefaultFlexibleElementType type, String value, boolean isProjectCountryChanged) {

		// All default values are managed as strings.
		// See DefaultFlexibleElementDTO.getComponent();
		if (value == null) {
			LOGGER.error("[saveDefaultElement] The value isn't a string and cannot be considered.");
			return null;
		}

		final String stringValue = value;

		// Retrieves container.
		final Project project = em().find(Project.class, id);
		final OrgUnit orgUnit = em().find(OrgUnit.class, id);

		if (project == null && orgUnit == null) {
			LOGGER.error("[saveDefaultElement] Container with id '{}' not found.", id);
			return null;
		}

		if (project != null) {
			LOGGER.debug("[saveDefaultElement] Found project with code '{}'.", project.getName());
		} else {
			LOGGER.debug("[saveDefaultElement] Found org unit with code '{}'.", orgUnit.getName());
		}

		final String oldValue;

		switch (type) {
			case CODE:
				if (project != null) {
					oldValue = project.getName();
					project.setName(stringValue);
				} else {
					oldValue = orgUnit.getName();
					orgUnit.setName(stringValue);
				}

				LOGGER.debug("[saveDefaultElement] Set container code to '{}'.", stringValue);
				break;
				
			case TITLE:
				if (project != null) {
					oldValue = project.getFullName();
					project.setFullName(stringValue);
				} else {
					oldValue = orgUnit.getFullName();
					orgUnit.setFullName(stringValue);
				}

				LOGGER.debug("[saveDefaultElement] Set container full name to '{}'.", stringValue);
				break;
				
			case START_DATE: {
				// Decodes timestamp.
				if (project != null) {
					oldValue = project.getStartDate() == null ? null : String.valueOf(project.getStartDate().getTime());
					if ("".equals(stringValue)) {

						project.setStartDate(null);

						LOGGER.debug("[saveDefaultElement] Set container start date to null.");
						
					} else {
						final long timestamp = Long.valueOf(stringValue);
						final Date date = new Date(timestamp);
						project.setStartDate(date);

						LOGGER.debug("[saveDefaultElement] Set container start date to '{}'.", date);
					}
					
				} else {
					oldValue = null;
				}
			}
				break;
				
			case END_DATE: {
				// Decodes timestamp.
				if (project != null) {
					oldValue = project.getEndDate() == null ? null : String.valueOf(project.getEndDate().getTime());
					if ("".equals(stringValue)) {

						project.setEndDate(null);

						LOGGER.debug("[saveDefaultElement] Set container end date to null.");
						
					} else {
						final long timestamp = Long.valueOf(stringValue);
						final Date date = new Date(timestamp);
						project.setEndDate(date);

						LOGGER.debug("[saveDefaultElement] Set container end date to '{}'.", date);
					}
				} else {
					oldValue = null;
				}
			}
				break;
				
			case COUNTRY: {
				if (orgUnit != null) {
					if (orgUnit.getOfficeLocationCountry() != null) {
						oldValue = String.valueOf(orgUnit.getOfficeLocationCountry().getId());
					} else {
						oldValue = null;
					}

					// Retrieves country.
					final Country country = em().find(Country.class, Integer.valueOf(stringValue));
					orgUnit.setOfficeLocationCountry(country);

					LOGGER.debug("[saveDefaultElement] Set container country to '{}'.", country.getName());
					
				} else {
					oldValue = null;
				}
			}
				break;
				
			case MANAGER: {
				if (project != null) {
					oldValue = project.getManager() == null ? null : String.valueOf(project.getManager().getId());

					// Retrieves manager.
					final User manager = em().find(User.class, Integer.valueOf(stringValue));
					project.setManager(manager);

					LOGGER.debug("[saveDefaultElement] Set container manager to '{}'.", manager.getName());

				} else {
					oldValue = null;
				}
			}
				break;
				
			case ORG_UNIT: {
				if (project != null) {

					OrgUnit old = null;
					for (OrgUnit p : project.getPartners()) {
						old = p;
						break;
					}

					oldValue = old == null ? null : String.valueOf(old.getId());

					// Retrieves manager.
					final OrgUnit o = em().find(OrgUnit.class, Integer.valueOf(stringValue));
					project.getPartners().clear();
					project.getPartners().add(o);

					if (isProjectCountryChanged) {
						LOGGER.debug("Changing country is true.");
						project.setCountry(o.getOfficeLocationCountry());
					} else {
						LOGGER.debug("Changing country is false.");
					}

					LOGGER.debug("[saveDefaultElement] Set container org unit to '{}'.", o.getFullName());

				} else {
					oldValue = null;
				}
			}
				break;
			default:
				LOGGER.error("[saveDefaultElement] Unknown type '{}' for the default flexible elements.", type);
				return null;
		}

		// Updates container.
		if (project != null) {
			em().merge(project);
		} else {
			em().merge(orgUnit);
		}

		LOGGER.debug("[saveDefaultElement] Updates the container.");

		return oldValue;
	}
	
	protected void onAdd(final TripletValueDTO item, final List<Integer> ids, final Value currentValue, final Date historyDate, final FlexibleElement element, final Integer projectId, final Integer iterationId, User user, String comment) {
		
		LOGGER.debug("[execute] Adds an element to the list.");
		
		// Adds the element.
		TripletValue entity = mapper.map(item, new TripletValue());
		entity = em().merge(entity);
		
		LOGGER.debug("[execute] Successfully create the entity with id #" + entity.getId() + ".");
		
		// Updates the value.
		ids.add(entity.getId());
		currentValue.setValue(ValueResultUtils.mergeElements(ids));
		
		// Historize the value.
		historize(historyDate, element, projectId, iterationId, user, ValueEventChangeType.ADD, null, entity, comment);
	}

	protected boolean onDelete(final TripletValueDTO item, final List<Integer> ids, final Value currentValue, final Date historyDate, final FlexibleElement element, final Integer projectId, final Integer iterationId, User user, String comment) {
		
		LOGGER.debug("[execute] Removes a element from the list.");

		// Retrieves the element.
		final TripletValue entity = em().find(TripletValue.class, item.getId());

		if(!(entity instanceof Deleteable)) {
			LOGGER.debug("[execute] The element isn't deletable, the event is ignored.");
			return false;
		}
		
		// Marks the entity as deleted.
		((Deleteable) entity).delete();
		em().merge(entity);
		
		LOGGER.debug("[execute] Successfully remove the entity with id #" + entity.getId() + ".");
		
		// Updates the value.
		ids.remove(entity.getId());
		currentValue.setValue(ValueResultUtils.mergeElements(ids));
		
		// Historize the value.
		historize(historyDate, element, projectId, iterationId, user, ValueEventChangeType.REMOVE, null, entity, comment);
		return true;
	}

	protected void onEdit(final TripletValueDTO item, final Date historyDate, final FlexibleElement element, final Integer projectId, final Integer iterationId, User user, String comment) {
		
		LOGGER.debug("[execute] Edits a element from the list.");
		
		// Retrieves the element.
		final TripletValue entity = mapper.map(item, new TripletValue());
		em().merge(entity);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("[execute] Successfully edit the entity with id #" + entity.getId() + ".");
		}
		
		// Historize the value.
		historize(historyDate, element, projectId, iterationId, user, ValueEventChangeType.EDIT, null, entity, comment);
	}
	
	/**
	 * Add to the historic the previous value of the given element.
	 * 
	 * @param date
	 * @param element
	 * @param projectId
	 * @param user
	 * @param singleValue
	 * @param comment 
	 */
	private void historize(final Date date, final FlexibleElement element, final Integer projectId, final Integer iterationId, final User user, final String singleValue, final String comment) {
		historize(date, element, projectId, iterationId, user, ValueEventChangeType.EDIT, singleValue, null, comment);
	}
	
	/**
	 * Add to the historic the previous value of the given element.
	 * 
	 * @param date
	 * @param element
	 * @param projectId
	 * @param user
	 * @param type
	 * @param singleValue
	 * @param listValue
	 * @param comment 
	 */
	private void historize(final Date date, final FlexibleElement element, final Integer projectId, final Integer iterationId, final User user, final ValueEventChangeType type, final String singleValue, final TripletValue listValue, final String comment) {

		// Manages history.
		if (element != null && element.isHistorable()) {

			final HistoryToken historyToken = new HistoryToken();

			historyToken.setElementId(element.getId());
			historyToken.setProjectId(projectId);
			historyToken.setDate(date);
			historyToken.setUser(user);
			historyToken.setType(type);
			historyToken.setComment(comment);
			historyToken.setLayoutGroupIterationId(iterationId);

			// Sets the value or list value.
			if (listValue == null) {
				historyToken.setValue(element.asHistoryToken(singleValue));
			} else {
				historyToken.setValue(element.asHistoryToken(listValue));
			}

			em().persist(historyToken);
		}
	}
	
	/**
	 * Update the computation elements referencing the given element.
	 * 
	 * @param element
	 *			Flexible element whose value was updated.
	 * @param projectId
	 *			Identifier of the project containing the flexible element.
	 * @param user 
	 *			User changing the value of the flexible element.
	 */
	private void updateImpactedComputations(final FlexibleElement element, final Integer projectId, final User user) {
		final Collection<ComputationElement> computationElements = computationService.getComputationElementsReferencingElement(element);
		
		if (computationElements.isEmpty()) {
			return;
		}
		
		final Project project = em().find(Project.class, projectId);
		
		final ArrayList<ProjectFunding> allFundings = new ArrayList<>();
		allFundings.addAll(project.getFunded());
		allFundings.addAll(project.getFunding());
		
		for (final ComputationElement computationElement : computationElements) {
			final ProjectModel parentModel = computationService.getParentProjectModel(computationElement);
			
			if (parentModel != null) {
				final Integer parentModelId = parentModel.getId();
				for (final ProjectFunding projectFunding : allFundings) {
					final Project fundedProject = projectFunding.getFunded();
					if (parentModelId.equals(fundedProject.getProjectModel().getId())) {
						computationService.updateComputationValueForProject(computationElement, fundedProject, user);
					}
					final Project fundingProject = projectFunding.getFunding();
					if (parentModelId.equals(fundingProject.getProjectModel().getId())) {
						computationService.updateComputationValueForProject(computationElement, fundingProject, user);
					}
				}
			}
		}
	}
	
}
