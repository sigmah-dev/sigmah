package org.sigmah.server.handler;

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

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Country;
import org.sigmah.server.domain.HistoryToken;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.util.Deleteable;
import org.sigmah.server.domain.value.TripletValue;
import org.sigmah.server.domain.value.Value;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.server.service.UserPermissionPolicy;
import org.sigmah.shared.command.UpdateProject;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.event.ValueEventWrapper;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;
import org.sigmah.shared.dto.referential.ValueEventChangeType;
import org.sigmah.shared.dto.value.TripletValueDTO;
import org.sigmah.shared.util.ValueResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.persist.Transactional;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import org.sigmah.offline.sync.SuccessCallback;
import org.sigmah.server.computation.ServerComputations;
import org.sigmah.server.computation.ServerValueResolver;
import org.sigmah.server.handler.util.Conflicts;
import org.sigmah.server.handler.util.Handlers;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.computation.Computation;
import org.sigmah.shared.computation.Computations;
import org.sigmah.shared.computation.dependency.CollectionDependency;
import org.sigmah.shared.computation.dependency.ContributionDependency;
import org.sigmah.shared.computation.dependency.Dependency;
import org.sigmah.shared.computation.dependency.DependencyVisitor;
import org.sigmah.shared.computation.dependency.SingleDependency;
import org.sigmah.shared.computation.value.ComputedValue;
import org.sigmah.shared.computation.value.ComputedValues;
import org.sigmah.shared.dispatch.FunctionalException;
import org.sigmah.shared.dispatch.UpdateConflictException;
import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.BudgetSubFieldDTO;
import org.sigmah.shared.dto.element.ComputationElementDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.dto.referential.AmendmentState;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.dto.value.ListableValue;
import org.sigmah.shared.util.ProfileUtils;

/**
 * Updates the values of the flexible elements for a specific project.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class UpdateProjectHandler extends AbstractCommandHandler<UpdateProject, VoidResult> {

	/**
	 * Logger.
	 */
	private final static Logger LOG = LoggerFactory.getLogger(UpdateProjectHandler.class);

	/**
	 * Mapper to transform domain objects in DTO.
	 */
	@Inject
	private Mapper mapper;
	
	/**
	 * Guice injector.
	 */
	@Inject
	private Injector injector;
	
	/**
	 * Language files.
	 */
	@Inject
	private I18nServer i18nServer;
	
	/**
	 * Conflict detector.
	 */
	@Inject
	private Conflicts conflictHandler;
	
	/**
	 * Value resolver for computations.
	 */
	@Inject
	private ServerValueResolver valueResolver;


	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public VoidResult execute(final UpdateProject cmd, final UserExecutionContext context) throws CommandException {

		if (LOG.isDebugEnabled()) {
			LOG.debug("[execute] Updates project #" + cmd.getProjectId() + " with following values #" + cmd.getValues().size() + " : " + cmd.getValues());
		}

		final List<ValueEventWrapper> values = cmd.getValues();
		final Integer projectId = cmd.getProjectId();
		final String comment = cmd.getComment();
		
		updateProject(values, projectId, context, comment);

		return null;
	}

	/**
	 * Update the project identified by <code>projectId</code> with the given values.
	 * 
	 * @param values Values to update.
	 * @param projectId Identifier of the project to update.
	 * @param context User context.
	 * @param comment Update comment.
	 * @throws CommandException If an error occurs during update.
	 */
	@Transactional(rollbackOn = CommandException.class)
	protected void updateProject(final List<ValueEventWrapper> values, final Integer projectId, UserExecutionContext context, String comment) throws CommandException {
		// This date must be the same for all the saved values !
		final Date historyDate = new Date();
		
		// Search the given project.
		final Project project = em().find(Project.class, projectId);
		
		// Verify if the modifications conflicts with the project state.
		final List<String> conflicts = searchForConflicts(project, values, context);
		
		final User user = context.getUser();
		
		// Track if an element part of the core version has been modified.
		boolean coreVersionHasBeenModified = false;
		
		// Iterating over the value change events
		for (final ValueEventWrapper valueEvent : values) {
			
			// Event parameters.
			final FlexibleElementDTO source = valueEvent.getSourceElement();
			final FlexibleElement element = em().find(FlexibleElement.class, source.getId());
			final TripletValueDTO updateListValue = valueEvent.getListValue();
			final String updateSingleValue = valueEvent.getSingleValue();
			final boolean isProjectCountryChanged = valueEvent.isProjectCountryChanged();

			LOG.debug("[execute] Updates value of element #{} ({})", source.getId(), source.getEntityName());
			LOG.debug("[execute] Event of type {} with value {} and list value {}.", valueEvent.getChangeType(), updateSingleValue, updateListValue);

			// Verify if the core version has been modified.
			coreVersionHasBeenModified = coreVersionHasBeenModified || element != null && element.isAmendable();
			
			// Case of the default flexible element which values arent't stored
			// like other values. These values impact directly the project.
			if (source instanceof DefaultFlexibleElementDTO && !((DefaultFlexibleElementType.BUDGET.equals(((DefaultFlexibleElementDTO) source).getType())))) {

				final DefaultFlexibleElementDTO defaultElement = (DefaultFlexibleElementDTO) source;

					LOG.debug("[execute] Default element case '{}'.", defaultElement.getType());

				// Saves the value and switch to the next value.
				final String oldValue = saveDefaultElement(projectId, defaultElement.getType(), updateSingleValue, isProjectCountryChanged);

				// Checks if the first value has already been historized or not.
				List<HistoryToken> results = null;
				if (element != null) {
					final TypedQuery<HistoryToken> query =
						em().createQuery("SELECT h FROM HistoryToken h WHERE h.elementId = :elementId AND h.projectId = :projectId", HistoryToken.class);
					query.setParameter("elementId", element.getId());
					query.setParameter("projectId", projectId);
					results = query.getResultList();
				}

				if (results == null || results.isEmpty()) {
					final Date oldDate;
					final User oldOwner;
					if (project != null) {
						oldDate = project.getLastSchemaUpdate();
						oldOwner = project.getOwner();
					} else {
						oldDate = new Date(historyDate.getTime() - 1);
						oldOwner = null;
					}

					// Historize the first value.
					if (oldValue != null) {
						historize(oldDate, element, projectId, oldOwner, ValueEventChangeType.ADD, oldValue, null, null);
					}
				}

				// Historize the value.
				historize(historyDate, element, projectId, user, ValueEventChangeType.EDIT, updateSingleValue, null, comment);

				continue;
			}

			// Retrieving the current value
			final Value currentValue = retrieveOrCreateValue(projectId, source.getId(), user);

			// Unique value of the flexible element.
			if (updateListValue == null) {

				if (LOG.isDebugEnabled()) {
					LOG.debug("[execute] Basic value case.");
				}

				currentValue.setValue(updateSingleValue);

				// Historize the value.
				historize(historyDate, element, projectId, user, ValueEventChangeType.EDIT, updateSingleValue, null, comment);
			}
			
			// Special case : this value is a part of a list which is the true value of the flexible element. (only used for
			// the TripletValue class for the moment)
			else {
				if (LOG.isDebugEnabled()) {
					LOG.debug("[execute] List value case.");
				}

				// The value of the element is a list of ids (default separated).
				final List<Integer> ids = ValueResultUtils.splitValuesAsInteger(currentValue.getValue());

				if (LOG.isDebugEnabled()) {
					LOG.debug("[execute] The current list of ids is : " + ids + ".");
				}

				// Cast the update value (as a DTO).
				switch (valueEvent.getChangeType()) {
					case ADD:
						onAdd(updateListValue, ids, currentValue, historyDate, element, projectId, user, comment);
						break;
						
					case REMOVE:
						if(!onDelete(updateListValue,  ids, currentValue, historyDate, element, projectId, user, comment)) {
							// Do not historize, the value hasn't been changed.
							continue;
						}
						break;
						
					case EDIT:
						onEdit(updateListValue, historyDate, element, projectId, user, comment);
						break;
						
					default:
						LOG.debug("[execute] Unknown command " + valueEvent.getChangeType() + ".");
						break;
				}

				LOG.debug("[execute] The new list of ids is : " + ids + ".");
			}

			// Store the value.
			em().merge(currentValue);
		}

		// Update user permissions
		final Project updatedProject = em().find(Project.class, projectId);
		if (updatedProject != null) {
			OrgUnit newOrgUnit = null;
			for (OrgUnit orgUnit : updatedProject.getPartners()) {
				newOrgUnit = orgUnit;
				break;
			}
			if (newOrgUnit != null) {
				final UserPermissionPolicy permissionPolicy = injector.getInstance(UserPermissionPolicy.class);
				permissionPolicy.deleteUserPemissionByProject(projectId);
				permissionPolicy.updateUserPermissionByOrgUnit(newOrgUnit);
			}
			
			if(coreVersionHasBeenModified) {
				// Update the revision number
				updatedProject.setAmendmentRevision(updatedProject.getAmendmentRevision() == null ? 2 : updatedProject.getAmendmentRevision() + 1);
				em().merge(updatedProject);
			}
		}

		if(!conflicts.isEmpty()) {
			// A conflict was found.
			throw new UpdateConflictException(updatedProject.toContainerInformation(), conflicts.toArray(new String[0]));
		}
	}

	protected void onAdd(final TripletValueDTO item, final List<Integer> ids, final Value currentValue, final Date historyDate, final FlexibleElement element, final Integer projectId, User user, String comment) {
		LOG.debug("[execute] Adds an element to the list.");
		
		// Adds the element.
		TripletValue entity = mapper.map(item, new TripletValue());
		entity = em().merge(entity);
		
		LOG.debug("[execute] Successfully create the entity with id #" + entity.getId() + ".");
		
		// Updates the value.
		ids.add(entity.getId());
		currentValue.setValue(ValueResultUtils.mergeElements(ids));
		
		// Historize the value.
		historize(historyDate, element, projectId, user, ValueEventChangeType.ADD, null, entity, comment);
	}

	protected boolean onDelete(final TripletValueDTO item, final List<Integer> ids, final Value currentValue, final Date historyDate, final FlexibleElement element, final Integer projectId, User user, String comment) {
		LOG.debug("[execute] Removes a element from the list.");

		// Retrieves the element.
		final TripletValue entity = em().find(TripletValue.class, item.getId());

		if(!(entity instanceof Deleteable)) {
			LOG.debug("[execute] The element isn't deletable, the event is ignored.");
			return false;
		}
		
		// Marks the entity as deleted.
		((Deleteable) entity).delete();
		em().merge(entity);
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("[execute] Successfully remove the entity with id #" + entity.getId() + ".");
		}
		
		// Updates the value.
		ids.remove(entity.getId());
		currentValue.setValue(ValueResultUtils.mergeElements(ids));
		
		// Historize the value.
		historize(historyDate, element, projectId, user, ValueEventChangeType.REMOVE, null, entity, comment);
		return true;
	}

	protected void onEdit(final TripletValueDTO item, final Date historyDate, final FlexibleElement element, final Integer projectId, User user, String comment) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("[execute] Edits a element from the list.");
		}
		
		// Retrieves the element.
		final TripletValue entity = mapper.map(item, new TripletValue());
		em().merge(entity);
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("[execute] Successfully edit the entity with id #" + entity.getId() + ".");
		}
		
		// Historize the value.
		historize(historyDate, element, projectId, user, ValueEventChangeType.EDIT, null, entity, comment);
	}

	private void historize(Date date, FlexibleElement element, Integer projectId, User user, ValueEventChangeType type, String singleValue, TripletValue listValue, String comment) {

		// Manages history.
		if (element != null && element.isHistorable()) {

			final HistoryToken historyToken = new HistoryToken();

			historyToken.setElementId(element.getId());
			historyToken.setProjectId(projectId);
			historyToken.setDate(date);
			historyToken.setUser(user);
			historyToken.setType(type);
			historyToken.setComment(comment);

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

		// Retrieving the current value
		Value currentValue = retrieveCurrentValue(projectId, elementId);

		// Update operation.
		if (currentValue != null) {
			LOG.debug("[execute] Retrieves a value for element #{0}.", elementId);
			currentValue.setLastModificationAction('U');
		}
		// Create operation
		else {
			LOG.debug("[execute] Creates a value for element #{0}.", elementId);

			currentValue = new Value();
			currentValue.setLastModificationAction('C');

			// Parent element
			final FlexibleElement element = em().find(FlexibleElement.class, elementId);
			currentValue.setElement(element);

			// Container
			currentValue.setContainerId(projectId);
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
	private Value retrieveCurrentValue(int projectId, Integer elementId) {
		final Query query = em().createQuery("SELECT v FROM Value v WHERE v.containerId = :projectId and v.element.id = :elementId");
		query.setParameter("projectId", projectId);
		query.setParameter("elementId", elementId);

		Value currentValue = null;

		try {
			currentValue = (Value) query.getSingleResult();
		} catch (NoResultException nre) {
			// No current value
		}
		
		return currentValue;
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
	private String getCurrentValueFormatted(int projectId, FlexibleElementDTO element) {
        
		final Value value = retrieveCurrentValue(projectId, element.getId());
		
		if(value != null) {
			return element.toHTML(value.getValue());
		} else {
			return "";
		}
	}
	
    /**
     * Format the given value event.
     * 
     * @param valueEvent
     *          Value event to format.
     * @return The given value in HTML format.
     */
	private String getTargetValueFormatted(ValueEventWrapper valueEvent) {
		return valueEvent.getSourceElement().toHTML(valueEvent.getSingleValue());
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
			LOG.error("[saveDefaultElement] The value isn't a string and cannot be considered.");
			return null;
		}

		final String stringValue = value;

		// Retrieves container.
		final Project project = em().find(Project.class, id);
		final OrgUnit orgUnit = em().find(OrgUnit.class, id);

		if (project == null && orgUnit == null) {
			LOG.error("[saveDefaultElement] Container with id '{}' not found.", id);
			return null;
		}

		if (project != null) {
			LOG.debug("[saveDefaultElement] Found project with code '{}'.", project.getName());
		} else {
			LOG.debug("[saveDefaultElement] Found org unit with code '{}'.", orgUnit.getName());
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

				LOG.debug("[saveDefaultElement] Set container code to '{}'.", stringValue);
				break;
				
			case TITLE:
				if (project != null) {
					oldValue = project.getFullName();
					project.setFullName(stringValue);
				} else {
					oldValue = orgUnit.getFullName();
					orgUnit.setFullName(stringValue);
				}

				if (LOG.isDebugEnabled()) {
					LOG.debug("[saveDefaultElement] Set container full name to '{}'.", stringValue);
				}
				break;
				
			case START_DATE: {
				// Decodes timestamp.
				if (project != null) {
					oldValue = project.getStartDate() == null ? null : String.valueOf(project.getStartDate().getTime());
					if ("".equals(stringValue)) {

						project.setStartDate(null);

						LOG.debug("[saveDefaultElement] Set container start date to null.");
						
					} else {
						final long timestamp = Long.valueOf(stringValue);
						final Date date = new Date(timestamp);
						project.setStartDate(date);

						LOG.debug("[saveDefaultElement] Set container start date to '{}'.", date);
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

						LOG.debug("[saveDefaultElement] Set container end date to null.");
						
					} else {
						final long timestamp = Long.valueOf(stringValue);
						final Date date = new Date(timestamp);
						project.setEndDate(date);

						LOG.debug("[saveDefaultElement] Set container end date to '{}'.", date);
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

					LOG.debug("[saveDefaultElement] Set container country to '{}'.", country.getName());
					
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

					LOG.debug("[saveDefaultElement] Set container manager to '{}'.", manager.getName());

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
						LOG.debug("Changing country is true.");
						project.setCountry(o.getOfficeLocationCountry());
					} else {
						LOG.debug("Changing country is false.");
					}

					LOG.debug("[saveDefaultElement] Set container org unit to '{}'.", o.getFullName());

				} else {
					oldValue = null;
				}
			}
				break;
			default:
				LOG.error("[saveDefaultElement] Unknown type '{}' for the default flexible elements.", type);
				return null;
		}

		// Updates container.
		if (project != null) {
			em().merge(project);
		} else {
			em().merge(orgUnit);
		}

		LOG.debug("[saveDefaultElement] Updates the container.");

		return oldValue;
	}
	
	/**
	 * Throw a functional exception if a conflict if found.
	 * 
	 * @param project Updated project.
	 * @param values Values to update.
	 * @param projectId 
	 * @throws FunctionalException 
	 */
	private List<String> searchForConflicts(final Project project, final List<ValueEventWrapper> values, final UserExecutionContext context) throws FunctionalException {
		
		final ArrayList<String> conflicts = new ArrayList<>();
		
		if (project == null) {
			// The user is modifying an org unit.
			// TODO: Verify if the user has the right to modify the org unit.
			return conflicts;
		}
		
		final Language language = context.getLanguage();
		final ProfileDTO profile = Handlers.aggregateProfiles(context.getUser(), mapper);
		
		if (project.getProjectModel().isUnderMaintenance()) {
			// BUGFIX #730: Verifying the maintenance status of projects.
			conflicts.add(i18nServer.t(language, "conflictEditingUnderMaintenanceProject",
				project.getName(), project.getFullName()));
			
			return conflicts;
		}
        
        // Verify computated values.
        conflictsRelatedToComputedElements(values, project, conflicts, language);
		
		if (ProfileUtils.isGranted(profile, GlobalPermissionEnum.MODIFY_LOCKED_CONTENT)) {
			// The user is allowed to edit locked fields.
			final boolean projectIsClosed = project.getCloseDate() != null;
			final boolean projectIsLocked = project.getAmendmentState() == AmendmentState.LOCKED;
			
			for (final ValueEventWrapper value : values) {
				final FlexibleElementDTO source = value.getSourceElement();
				
				final boolean phaseIsClosed = conflictHandler.isParentPhaseClosed(source.getId(), project.getId());
				
				if (projectIsClosed || phaseIsClosed || (source.getAmendable() && projectIsLocked)) {
					final ValueResult result = new ValueResult();
					result.setValueObject(value.getSingleValue());
					result.setValuesObject(value.getListValue() != null ? Collections.<ListableValue>singletonList(value.getListValue()) : null);

					if(!source.isCorrectRequiredValue(result)) {
						conflicts.add(i18nServer.t(language, "conflictModifyLockedContentEmptyValue",
							source.getFormattedLabel(), getCurrentValueFormatted(project.getId(), source)));
					}
				}
			}
			
			return conflicts;
		}
		
		if (project.getCloseDate() != null) {
			// User is trying to modify a closed project.
			for (final ValueEventWrapper valueEvent : values) {
				final FlexibleElementDTO source = valueEvent.getSourceElement();
				
				conflicts.add(i18nServer.t(language, "conflictUpdatingAClosedProject",
					source.getFormattedLabel(), getCurrentValueFormatted(project.getId(), source), getTargetValueFormatted(valueEvent)));
			}
			
		} else {
			// Verify if the user is trying to modify a closed phase.
			Iterator<ValueEventWrapper> iterator = values.iterator();
			while (iterator.hasNext()) {
				final ValueEventWrapper valueEvent = iterator.next();
				final FlexibleElementDTO source = valueEvent.getSourceElement();

				if (conflictHandler.isParentPhaseClosed(source.getId(), project.getId())) {
					// Removing the current value event from the update list.
					iterator.remove();
					
					conflicts.add(i18nServer.t(language, "conflictUpdatingAClosedPhase",
						source.getFormattedLabel(), getCurrentValueFormatted(project.getId(), source), getTargetValueFormatted(valueEvent)));
				}
			}
			
			// Verify if the user is trying to modify a locked field.
			if (project.getAmendmentState() == AmendmentState.LOCKED) {
				iterator = values.iterator();
				while (iterator.hasNext()) {
					final ValueEventWrapper valueEvent = iterator.next();
					final FlexibleElementDTO source = valueEvent.getSourceElement();

					final boolean conflict;
					if (source.getAmendable()) {
						if (source instanceof BudgetElementDTO) {
							final BudgetSubFieldDTO divisorField = ((BudgetElementDTO)source).getRatioDivisor();
							final Value value = retrieveCurrentValue(project.getId(), source.getId());
							conflict = getValueOfSubField(value.getValue(), divisorField) != getValueOfSubField(valueEvent.getSingleValue(), divisorField);

						} else {
							conflict = true;
						}
					} else {
						conflict = false;
					}

					if (conflict) {
						// Removing the current value event from the update list.
						iterator.remove();
						
						conflicts.add(i18nServer.t(language, "conflictUpdatingALockedField",
							source.getFormattedLabel(), getCurrentValueFormatted(project.getId(), source), getTargetValueFormatted(valueEvent)));
					}
				}
			}
		}
		
		return conflicts;
	}

    /**
     * Verify updates done to computed values.
     * 
     * @param values
     *          Changed values.
     * @param project
     *          Edited project.
     * @param conflicts
     *          List of conflicts.
     * @param language 
     *          Language of the user.
     */
    private void conflictsRelatedToComputedElements(final List<ValueEventWrapper> values, final Project project, final List<String> conflicts, final Language language) {
        
        for (final ValueEventWrapper value : values) {
            final FlexibleElementDTO source = value.getSourceElement();
            
            if (source instanceof ComputationElementDTO && ((ComputationElementDTO) source).hasConstraints()) {
                // Recompute the value and check that the result matches the constraints.
                final ComputationElementDTO computationElement = (ComputationElementDTO) source;
                
                final ComputedValue[] serverResult = new ComputedValue[1];
                final ComputedValue clientResult = ComputedValues.from(value.getSingleValue());
                
                final Computation computation = Computations.parse(computationElement.getRule(), ServerComputations.getAllElementsFromModel(project.getProjectModel()));
                computation.computeValueWithWrappersAndResolver(project.getId(), values, valueResolver, new SuccessCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        serverResult[0] = ComputedValues.from(result);
                    }
                });
                
                if (!clientResult.equals(serverResult[0])) {
                    // Updating the value.
                    value.setSingleValue(serverResult[0].toString());
                }
                
                final int comparison = serverResult[0].matchesConstraints(computationElement);
                if (comparison != 0) {
                    final String greaterOrLess, breachedConstraint;
                    if (comparison < 0) {
                        greaterOrLess = i18nServer.t(language, "flexibleElementComputationLess");
                        breachedConstraint = computationElement.getMinimumValue();
                    } else {
                        greaterOrLess = i18nServer.t(language, "flexibleElementComputationGreater");
                        breachedConstraint = computationElement.getMaximumValue();
                    }
                    
                    final List<ValueEventWrapper> changes = computation.getRelatedChanges(values);
                    final String fieldList = org.sigmah.shared.util.Collections.join(changes, new org.sigmah.shared.util.Collections.Mapper<ValueEventWrapper, String>() {
                        
                        @Override
                        public String forEntry(ValueEventWrapper entry) {
                            return entry.getSourceElement().getFormattedLabel();
                        }
                    }, ", ");
                    
                    conflicts.add(i18nServer.t(language, "conflictComputationOutOfBound",
                            fieldList, value.getSingleValue(), source.getFormattedLabel(), greaterOrLess, breachedConstraint) 
                            + dependenciesLastValuesForComputation(computation, project.getId(), language));
                }
            }
        }
    }
    
    /**
     * Returns a list of the details of each dependency of the computation. <br>
     * <br>
     * The details for each dependency contains:<ul>
     * <li>Label of the flexible element.</li>
     * <li>Last saved value (or '-' if unmodified).</li>
     * <li>Short name of the author of the last modification (or '-' if unmodified).</li>
     * <li>Date of the last modification (or '-' if unmodified).</li>
     * </ul>
     * 
     * @param computation
     *          Computation.
     * @param projectId
     *          Identifier of the current project.
     * @param language
     *          Language to use to create the messages.
     * @return A list of details about the dependencies.
     * 
     * @see #flexibleElementDetails(org.sigmah.shared.dto.element.FlexibleElementDTO, int, org.sigmah.shared.Language, java.text.DateFormat) 
     */
    private String dependenciesLastValuesForComputation(final Computation computation, final int projectId, final Language language) {
        final DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.forLanguageTag(language.getLocale()));
        
        return org.sigmah.shared.util.Collections.join(computation.getDependencies(), new org.sigmah.shared.util.Collections.Mapper<Dependency, String>() {
                        
            @Override
            public String forEntry(final Dependency entry) {
				final StringBuilder stringBuilder = new StringBuilder();
				
				entry.accept(new DependencyVisitor() {
					
					@Override
					public void visit(SingleDependency dependency) {
						stringBuilder
							.append("\n")
							.append(flexibleElementDetails(dependency.getFlexibleElement(), projectId, language, formatter));
					}

					@Override
					public void visit(CollectionDependency dependency) {
						throw new UnsupportedOperationException("Not supported yet.");
					}

					@Override
					public void visit(ContributionDependency dependency) {
						throw new UnsupportedOperationException("Not supported yet.");
					}
					
				});
				
				return stringBuilder.toString();
            }
        }, "");
    }
    
    /**
     * Finds the current value of the given element and returns a line with the details.
     * <br>
     * <br>
     * The returned details are:<ul>
     * <li>Label of the flexible element.</li>
     * <li>Last saved value (or '-' if unmodified).</li>
     * <li>Short name of the author of the last modification (or '-' if unmodified).</li>
     * <li>Date of the last modification (or '-' if unmodified).</li>
     * </ul>
     * 
     * @param entry
     *          Flexible element to format.
     * @param projectId
     *          Identifier of the project.
     * @param language
     *          Language to use to creates the message.
     * @param formatter
     *          Date formatter.
     * @return The formatted line.
     */
    private String flexibleElementDetails(final FlexibleElementDTO entry, final int projectId, final Language language, final DateFormat formatter) {
        
        final String title = entry.getFormattedLabel();
        final String value, author, date;

        final Value currentValue = retrieveCurrentValue(projectId, entry.getId());
        if (currentValue != null) {
            value = currentValue.getValue();
            author = User.getUserShortName(currentValue.getLastModificationUser());
            date = formatter.format(currentValue.getLastModificationDate());
        } else {
            value = "-";
            author = "-";
            date = "-";
        }

        return i18nServer.t(language, "conflictComputationDependencyDetails", title, value, author, date);
    }
    
	/**
	 * Retrieves the value of the given field as a double.
	 * 
	 * @param valueResult
     *          Raw value of a budget element.
	 * @param budgetSubField
     *          Sub field to search.
	 * @return The value of the given budget sub field.
	 */
	private double getValueOfSubField(final String valueResult, final BudgetSubFieldDTO budgetSubField) {
        
		final Map<Integer, String> values = ValueResultUtils.splitMapElements(valueResult);
		final String value = values.get(budgetSubField.getId());
		
		if (value != null && value.matches("^[0-9]+(([.][0-9]+)|)$")) {
			return Double.parseDouble(value);
		} else {
			return 0.0;
		}
	}
	
}
