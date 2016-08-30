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

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.value.Value;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.shared.command.UpdateProject;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.event.ValueEventWrapper;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;
import org.sigmah.shared.dto.referential.ProjectModelStatus;
import org.sigmah.shared.dto.value.TripletValueDTO;
import org.sigmah.shared.util.ValueResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
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
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.element.DefaultFlexibleElement;
import org.sigmah.server.handler.util.Conflicts;
import org.sigmah.server.handler.util.Handlers;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.server.service.ValueService;
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
import org.sigmah.shared.dto.referential.LogicalElementType;
import org.sigmah.shared.dto.referential.LogicalElementTypes;
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
	 * Service handling the update of Value objects.
	 */
	@Inject
	private ValueService valueService;
	
	/**
	 * Mapper to transform domain objects in DTO.
	 */
	@Inject
	private Mapper mapper;
	
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

		final User user = context.getUser();

		// Search the given project.
		final Project project = em().find(Project.class, projectId);
		if (project != null) {
			if (!Handlers.isProjectEditable(project, user)) {
				throw new IllegalStateException();
			}
		} else {
			// If project is null, it means the user is not trying to update a project but an org unit
			OrgUnit orgUnit = em().find(OrgUnit.class, projectId);
			if (!Handlers.isOrgUnitVisible(orgUnit, user)) {
				throw new IllegalStateException();
			}
		}

		// Verify if the modifications conflicts with the project state.
		final List<String> conflicts = searchForConflicts(project, values, context);

		// Track if an element part of the core version has been modified.
		boolean coreVersionHasBeenModified = false;

		// Iterating over the value change events
		for (final ValueEventWrapper valueEvent : values) {

			// Event parameters.
			final FlexibleElementDTO source = valueEvent.getSourceElement();
			final FlexibleElement element = em().find(FlexibleElement.class, source.getId());
			final TripletValueDTO updateListValue = valueEvent.getListValue();
			final String updateSingleValue = valueEvent.getSingleValue();
			
			final LogicalElementType type = LogicalElementTypes.of(source);

			LOG.debug("[execute] Updates value of element #{} ({})", source.getId(), source.getEntityName());
			LOG.debug("[execute] Event of type {} with value {} and list value {}.", valueEvent.getChangeType(), updateSingleValue, updateListValue);

			// Verify if the core version has been modified.
			coreVersionHasBeenModified = coreVersionHasBeenModified || element != null && element.isAmendable();
			
			if (type.toDefaultFlexibleElementType() != null && type.toDefaultFlexibleElementType() != DefaultFlexibleElementType.BUDGET) {
				// Case of the default flexible element which values arent't stored
				// like other values. These values impact directly the project.
				valueService.saveValue(updateSingleValue, valueEvent.isProjectCountryChanged(), historyDate, (DefaultFlexibleElement) element, projectId, user, comment);
			}
			else if (updateListValue != null) {
				// Special case : this value is a part of a list which is the true value of the flexible element. (only used for
				// the TripletValue class for the moment)
				valueService.saveValue(updateListValue, valueEvent.getChangeType(), historyDate, element, projectId, user, comment);
			}

			// Special case : this value is a part of a list which is the true value of the flexible element. (only used for
			// the TripletValue class for the moment)
			else {
				valueService.saveValue(updateSingleValue, historyDate, element, projectId, user, comment);
			}
		}
			
		final Project updatedProject = em().find(Project.class, projectId);
		if (updatedProject != null) {
			if(coreVersionHasBeenModified) {
				// Update the revision number
				updatedProject.setAmendmentRevision(updatedProject.getAmendmentRevision() == null ? 2 : updatedProject.getAmendmentRevision() + 1);
				em().merge(updatedProject);
			}
		}

		if (!conflicts.isEmpty()) {
			// A conflict was found.
			throw new UpdateConflictException(updatedProject.toContainerInformation(), conflicts.toArray(new String[0]));
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
     * Format the given value event.
     * 
     * @param valueEvent
     *          Value event to format.
     * @return The given value in HTML format.
     */
	public String getTargetValueFormatted(ValueEventWrapper valueEvent) {
		return valueEvent.getSourceElement().toHTML(valueEvent.getSingleValue());
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

		Integer projectOrgUnitId = null;
		if (project.getOrgUnit() == null) {
			// The project should be a draft project
			// Let's verify it
			if (project.getProjectModel().getStatus() != ProjectModelStatus.DRAFT) {
				LOG.error("Project {} doesn't have an OrgUnit.", project.getId());
			} else if (context.getUser().getMainOrgUnitWithProfiles() == null) {
				LOG.error("User {} doesn't have a main org unit.", context.getUser().getId());
			} else {
				// Let's get the main org unit from the user
				projectOrgUnitId = context.getUser().getMainOrgUnitWithProfiles().getOrgUnit().getId();
			}
		} else {
			projectOrgUnitId = project.getOrgUnit().getId();
		}

		final Language language = context.getLanguage();
		ProfileDTO profile = null;
		if (projectOrgUnitId != null) {
			profile = Handlers.aggregateProfiles(context.getUser(), mapper).get(projectOrgUnitId);
		}

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
							source.getFormattedLabel(), valueService.getCurrentValueFormatted(project.getId(), source)));
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
					source.getFormattedLabel(), valueService.getCurrentValueFormatted(project.getId(), source), getTargetValueFormatted(valueEvent)));
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
						source.getFormattedLabel(), valueService.getCurrentValueFormatted(project.getId(), source), getTargetValueFormatted(valueEvent)));
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
							source.getFormattedLabel(), valueService.getCurrentValueFormatted(project.getId(), source), getTargetValueFormatted(valueEvent)));
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
