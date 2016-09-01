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


import java.util.*;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.sigmah.client.ui.presenter.CreateProjectPresenter;
import org.sigmah.client.ui.presenter.project.logframe.ProjectLogFramePresenter;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.*;
import org.sigmah.server.domain.calendar.PersonalCalendar;
import org.sigmah.server.domain.element.BudgetElement;
import org.sigmah.server.domain.element.BudgetRatioElement;
import org.sigmah.server.domain.element.DefaultFlexibleElement;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.layout.LayoutConstraint;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.domain.logframe.LogFrame;
import org.sigmah.server.domain.logframe.LogFrameGroup;
import org.sigmah.server.domain.logframe.LogFrameModel;
import org.sigmah.server.domain.profile.Profile;
import org.sigmah.server.domain.reminder.MonitoredPointList;
import org.sigmah.server.domain.reminder.ReminderList;
import org.sigmah.server.domain.value.Value;
import org.sigmah.server.handler.util.ProjectMapper;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectFundingDTO;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.referential.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Project} service.
 *
 * @author Alexander (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Singleton
public class ProjectService extends AbstractEntityService<Project, Integer, ProjectDTO> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ProjectService.class);

	/**
	 * Project mapper.
	 */
	@Inject
	private ProjectMapper projectMapper;

	/**
	 * Project funding service.
	 */
	@Inject
	private ProjectFundingService fundingService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Project create(final PropertyMap properties, final UserExecutionContext context) {

		if (LOG.isDebugEnabled()) {
			LOG.debug("Starting project creation for properties: {}.", properties);
		}

		final User user = context.getUser();

		// Creates a new calendar
		PersonalCalendar calendar = new PersonalCalendar();
		calendar.setName(properties.<String> get("calendarName"));
		em().persist(calendar);

		// Creates the new project
		Project project = new Project();

		// Userdatabase attributes.
		project.setStartDate(new Date());
		final User owner = em().getReference(User.class, user.getId());
		project.setOwner(owner);

		// Manager.
		// The default manager is the owner of the project.
		project.setManager(owner);

		// Monitored points.
		project.setPointsList(new MonitoredPointList());

		// Reminders.
		project.setRemindersList(new ReminderList());
		OrgUnit orgunit = null;
		// No organizational unit for the testProjects
		if (properties.get(ProjectDTO.ORG_UNIT_ID) != null) {
			// Org unit.
			int orgUnitId = Integer.parseInt("" + properties.get(ProjectDTO.ORG_UNIT_ID));
			orgunit = em().find(OrgUnit.class, orgUnitId);
			project.getPartners().add(orgunit);
		}

		// Country
		final Country country = getProjectCountry(orgunit);
		project.setCountry(country);

		// Amendment
		project.setAmendmentState(AmendmentState.DRAFT);
		project.setAmendmentVersion(1);
		project.setAmendmentRevision(1);

		if (LOG.isDebugEnabled()) {
			LOG.debug("[createProject] Selected country: " + country.getName() + ".");
		}

		// Considers name length constraints.
		final String name = properties.<String> get(ProjectDTO.NAME);
		if (name != null) {
			if (name.length() > 50) {
				project.setName(name.substring(0, 50));
			} else {
				project.setName(name);
			}
		} else {
			project.setName("noname");
		}

		// Considers name length constraints.
		final String fullName = properties.<String> get(ProjectDTO.FULL_NAME);
		if (fullName != null) {
			if (fullName.length() > 500) {
				project.setFullName(fullName.substring(0, 500));
			} else {
				project.setFullName(fullName);
			}
		} else {
			project.setFullName("");
		}

		project.setLastSchemaUpdate(new Date());
		project.setCalendarId(calendar.getId());

		// Project attributes.
		ProjectModel model = em().getReference(ProjectModel.class, properties.<Integer> get("modelId"));
		if (ProjectModelStatus.READY.equals(model.getStatus())) {
			model.setStatus(ProjectModelStatus.USED);
		}
		model = em().merge(model);
		project.setProjectModel(model);
		project.setLogFrame(null);

		// Let's add default team member profiles
		List<Profile> defaultTeamMemberProfiles = new ArrayList<>(model.getDefaultTeamMemberProfiles());
		project.setTeamMemberProfiles(defaultTeamMemberProfiles);

		// Creates and adds phases.
		for (final PhaseModel phaseModel : model.getPhaseModels()) {

			final Phase phase = new Phase();
			phase.setPhaseModel(phaseModel);

			project.addPhase(phase);

			if (LOG.isDebugEnabled()) {
				LOG.debug("[createProject] Creates and adds phase instance for model: " + phaseModel.getName() + ".");
			}

			// Searches the root phase.
			if (model.getRootPhaseModel() != null && phaseModel.getId().equals(model.getRootPhaseModel().getId())) {

				// Sets it.
				phase.setStartDate(new Date());
				project.setCurrentPhase(phase);

				if (LOG.isDebugEnabled()) {
					LOG.debug("[createProject] Sets the first phase: " + phaseModel.getName() + ".");
				}
			}
		}

		// The model doesn't define a root phase, select the first declared
		// phase as the first one.
		if (model.getRootPhaseModel() == null) {

			if (LOG.isDebugEnabled()) {
				LOG.debug("[createProject] No root phase defined for this model, active the first phase by default.");
			}

			// Selects the first phase by default.
			final Phase phase = project.getPhases().get(0);

			// Sets it.
			phase.setStartDate(new Date());
			project.setCurrentPhase(phase);

			if (LOG.isDebugEnabled()) {
				LOG.debug("[createProject] Sets the first phase: " + phase.getPhaseModel().getName() + ".");
			}
		}

		em().persist(project);

		if (LOG.isDebugEnabled()) {
			LOG.debug("[createProject] Project successfully created.");
		}

		// Updates the project with a default log frame.
		final LogFrame logFrame = createDefaultLogFrame(project);
		project.setLogFrame(logFrame);

		// Create the budget values
		final Double budgetPlanned = properties.<Double> get(ProjectDTO.BUDGET);
		final String budgetValue;
		if (budgetPlanned != null) {
			budgetValue = createPlannedBudgetValue(budgetPlanned, model, project, user);
		} else {
			budgetValue = null;
		}

		// Updates the project
		project = em().merge(project);

		// Create initials history tokens
		// BUGFIX #729 & #784
		createInitialHistoryTokens(project, budgetValue, user);

		// Create links (if requested)
		final ProjectDTO baseProject = properties.get(ProjectDTO.BASE_PROJECT);
		if(baseProject != null) {
			final CreateProjectPresenter.Mode mode = properties.get(ProjectDTO.CREATION_MODE);

			if(mode == CreateProjectPresenter.Mode.FUNDING_ANOTHER_PROJECT) {
				// Sets the funding parameters.
				final Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put(ProjectFundingDTO.FUNDING_ID, project.getId());
				parameters.put(ProjectFundingDTO.FUNDED_ID, baseProject.getId());
				parameters.put(ProjectFundingDTO.PERCENTAGE, properties.get(ProjectDTO.AMOUNT));

				final ProjectFunding funding = fundingService.create(new PropertyMap(parameters), context);
				if(project.getFunding() == null) {
					project.setFunding(new ArrayList<ProjectFunding>());
				}
				project.getFunding().add(funding);

			} else if(mode == CreateProjectPresenter.Mode.FUNDED_BY_ANOTHER_PROJECT) {
				// Sets the funding parameters.
				final Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put(ProjectFundingDTO.FUNDING_ID, baseProject.getId());
				parameters.put(ProjectFundingDTO.FUNDED_ID, project.getId());
				parameters.put(ProjectFundingDTO.PERCENTAGE, properties.get(ProjectDTO.AMOUNT));

				final ProjectFunding funded = fundingService.create(new PropertyMap(parameters), context);
				if(project.getFunded() == null) {
					project.setFunded(new ArrayList<ProjectFunding>());
				}
				project.getFunded().add(funded);
			}
		}

		return project;
	}

	/**
	 * Create the planned budget initial value.
	 *
	 * @param budgetPlanned
	 *			Planned budget. <code>null</code> value will result in a <code>NullPointerException</code>.
	 * @param model
	 *			Project model to read.
	 * @param project
	 *			Project to edit.
	 * @param user
	 *			User creating the project.
	 * @return  The saved value.
	 */
	private String createPlannedBudgetValue(final Double budgetPlanned, final ProjectModel model, final Project project, final User user) {
		
		final BudgetRatioElement budgetRatioElement = model.getFirstElementOfType(BudgetRatioElement.class);
		if (budgetRatioElement == null || budgetRatioElement.getPlannedBudget() == null) {
			return null;
		}
		
		final String budgetValue = budgetPlanned.toString();
		
		final Value value = new Value();
		value.setContainerId(project.getId());
		value.setElement(budgetRatioElement.getPlannedBudget());
		value.setLastModificationAction('C');
		value.setLastModificationDate(new Date());
		value.setLastModificationUser(user);
		value.setValue(budgetValue);
		em().persist(value);
		
		return budgetValue;
	}
	
	/**
	 * Creates a well-configured default log frame for the new project.
	 *
	 * @param project
	 *          The project.
	 * @return The log frame.
	 */
	private LogFrame createDefaultLogFrame(Project project) {

		// Creates a new log frame (with a default model)
		final LogFrame logFrame = new LogFrame();
		logFrame.setParentProject(project);

		// Default groups.
		final ArrayList<LogFrameGroup> groups = new ArrayList<LogFrameGroup>();

		LogFrameGroup group = new LogFrameGroup();
		group.setType(LogFrameGroupType.SPECIFIC_OBJECTIVE);
		group.setParentLogFrame(logFrame);
		group.setLabel(ProjectLogFramePresenter.DEFAULT_GROUP_LABEL);
		groups.add(group);

		group = new LogFrameGroup();
		group.setType(LogFrameGroupType.EXPECTED_RESULT);
		group.setParentLogFrame(logFrame);
		group.setLabel(ProjectLogFramePresenter.DEFAULT_GROUP_LABEL);
		groups.add(group);

		group = new LogFrameGroup();
		group.setType(LogFrameGroupType.ACTIVITY);
		group.setParentLogFrame(logFrame);
		group.setLabel(ProjectLogFramePresenter.DEFAULT_GROUP_LABEL);
		groups.add(group);

		group = new LogFrameGroup();
		group.setType(LogFrameGroupType.PREREQUISITE);
		group.setParentLogFrame(logFrame);
		group.setLabel(ProjectLogFramePresenter.DEFAULT_GROUP_LABEL);
		groups.add(group);

		logFrame.setGroups(groups);

		// Links to the log frame model.
		ProjectModel projectModel = project.getProjectModel();
		LogFrameModel logFrameModel = projectModel.getLogFrameModel();

		if (logFrameModel == null) {
			logFrameModel = ProjectModelService.createDefaultLogFrameModel(projectModel);
			logFrameModel.setName("Auto-created default model at " + new Date());
			em().persist(logFrameModel);

			projectModel.setLogFrameModel(logFrameModel);
			em().merge(projectModel);
		}

		logFrame.setLogFrameModel(logFrameModel);

		em().persist(logFrame);

		return logFrame;
	}

	/**
	 * Creates an history token for each default flexible element.
	 *
	 * @param project
	 *			Created project.
	 * @param budgetValue
	 *			Budget value (can be <code>null</code>).
	 * @param user
	 *			User creating the project.
	 */
	private void createInitialHistoryTokens(final Project project, final String budgetValue, final User user) {
		
		for (final DefaultFlexibleElement element : getDefaultElements(project)) {
			final Integer elementId;
			final String value;

			if (element instanceof BudgetRatioElement) {
				value = budgetValue;
				final FlexibleElement plannedBudgetElement = ((BudgetRatioElement) element).getPlannedBudget();
				elementId = plannedBudgetElement != null ? plannedBudgetElement.getId() : null;
			} else {
				value = element.getValue(project);
				elementId = element.getId();
			}

			if (value != null && elementId != null) {
				final HistoryToken historyToken = new HistoryToken();

				historyToken.setDate(new Date());
				historyToken.setElementId(elementId);
				historyToken.setProjectId(project.getId());
				historyToken.setType(ValueEventChangeType.ADD);
				historyToken.setUser(user);
				historyToken.setValue(value);

				em().persist(historyToken);
			}
		}
	}

	/**
	 * Find every default element contained in the model of the given project.
	 *
	 * @param project Project to search.
	 * @return A set of every default flexible element.
	 */
	private Set<DefaultFlexibleElement> getDefaultElements(Project project) {
		final Set<DefaultFlexibleElement> defaultElements = new HashSet<>();

		for(final LayoutGroup layoutGroup : project.getProjectModel().getProjectDetails().getLayout().getGroups()) {
			getDefaultElements(layoutGroup, defaultElements);
		}

		for(final PhaseModel phaseModel : project.getProjectModel().getPhaseModels()) {
			for(final LayoutGroup layoutGroup : phaseModel.getLayout().getGroups()) {
				getDefaultElements(layoutGroup, defaultElements);
			}
		}

		return defaultElements;
	}

	/**
	 * Find every default flexible elements in the given layout group and add
	 * them to the given set.
	 *
	 * @param layoutGroup Group tu search.
	 * @param defaultElements Set to fill.
	 */
	private void getDefaultElements(LayoutGroup layoutGroup, Set<DefaultFlexibleElement> defaultElements) {
		for(final LayoutConstraint constraint : layoutGroup.getConstraints()) {
			if(constraint.getElement() instanceof DefaultFlexibleElement) {
				defaultElements.add((DefaultFlexibleElement) constraint.getElement());
			}
		}
	}

	/**
	 * Searches the country for the given org unit.
	 *
	 * @param orgUnit
	 *          The org unit.
	 * @return The country
	 */
	private Country getProjectCountry(OrgUnit orgUnit) {

		if (orgUnit == null) {
			return getDefaultCountry();
		}

		Country country = null;
		OrgUnit current = orgUnit;

		while (country == null || current != null) {

			if ((country = current.getOfficeLocationCountry()) != null) {
				return country;
			} else {
				current = current.getParentOrgUnit();
			}

			// Root reached
			if (current == null) {
				break;
			}
		}

		return getDefaultCountry();
	}

	/**
	 * Gets the default country for all the application.
	 *
	 * @return The default country.
	 */
	private Country getDefaultCountry() {

		final Query q = em().createQuery("SELECT c FROM Country c WHERE c.name = :defaultName");
		// FIXME France by default
		q.setParameter("defaultName", Country.DEFAULT_COUNTRY_NAME);

		try {
			return (Country) q.getSingleResult();
		} catch (NoResultException e) {

			try {
				return (Country) em().createQuery("SELECT c FROM Country c").getResultList().get(0);
			} catch (Throwable e2) {
				throw new IllegalStateException("There is no country in database, unable to create a project.", e2);
			}
		}
	}

	/**
	 * Find the budget element of the given model.
	 *
	 * @param model Model to search.
	 * @return An instance of <code>BudgetElement</code> or <code>null</code> if
	 *		   none was found.
	 */
	private BudgetElement getBudgetElement(ProjectModel model) {
		BudgetElement budgetElement = null;
		if (model.getProjectBanner().getLayout() != null) {
			for (LayoutGroup lg : model.getProjectBanner().getLayout().getGroups()) {
				for (LayoutConstraint lc : lg.getConstraints()) {
					if (lc.getElement() instanceof BudgetElement) {
						budgetElement = (BudgetElement) lc.getElement();
					}
				}
			}
		}

		if (model.getProjectDetails().getLayout() != null) {
			for (LayoutGroup lg : model.getProjectDetails().getLayout().getGroups()) {
				for (LayoutConstraint lc : lg.getConstraints()) {
					if (lc.getElement() instanceof BudgetElement) {
						budgetElement = (BudgetElement) lc.getElement();
					}
				}
			}
		}

		for (PhaseModel phase : model.getPhaseModels()) {
			for (LayoutGroup lg : phase.getLayout().getGroups()) {
				for (LayoutConstraint lc : lg.getConstraints()) {
					if (lc.getElement() instanceof BudgetElement) {
						budgetElement = (BudgetElement) lc.getElement();
					}
				}
			}
		}
		return budgetElement;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Project update(final Integer entityId, final PropertyMap changes, final UserExecutionContext context) {

		for (Map.Entry<String, Object> entry : changes.entrySet()) {

			if ("fundingId".equals(entry.getKey())) {

				// Get the current project
				Project project = em().find(Project.class, entityId);

				// Get the project funding relation entity object
				ProjectFunding projectFunding = em().find(ProjectFunding.class, entry.getValue());

				// Remove it from the current project
				project.getFunding().remove(projectFunding);

				// Save
				em().merge(project);
				em().remove(projectFunding);

			}

			else if ("fundedId".equals(entry.getKey())) {

				// Get the current project
				Project project = em().find(Project.class, entityId);

				// Get the project funding relation entity object
				ProjectFunding projectFunding = em().find(ProjectFunding.class, entry.getValue());

				// Remove it from the current project
				project.getFunded().remove(projectFunding);

				// Save
				em().merge(project);
				em().remove(projectFunding);

			} else if ("dateDeleted".equals(entry.getKey())) {

				// Get the current project
				UserDatabase project = em().find(UserDatabase.class, entityId);

				// Mark the project in the state "deleted" (but don't delete it
				// really)
				project.delete();

				final List<ProjectFunding> listfundingsToDelete = new ArrayList<ProjectFunding>();

				// Saves all the projectFundings that need to be deleted
				// before deleting them from the deleted project
				if (project instanceof Project) {
					Project pr = (Project) project;

					listfundingsToDelete.addAll(pr.getFunded());
					listfundingsToDelete.addAll(pr.getFunding());

					((Project) project).getFunded().clear();
					((Project) project).getFunding().clear();
				}

				// Save
				em().merge(project);

				for (ProjectFunding pf : listfundingsToDelete) {
					em().remove(pf);
				}
			}
		}

		return em().find(Project.class, entityId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EntityDTO<?> handleMapping(final Project createdProject) throws CommandException {

		final ProjectDTO mappedProject = projectMapper.map(createdProject, false);
		mappedProject.setSpendBudget(0.0);
		mappedProject.setReceivedBudget(0.0);

		return mappedProject;
	}

}
