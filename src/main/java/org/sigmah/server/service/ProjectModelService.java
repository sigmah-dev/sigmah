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


import org.sigmah.client.util.AdminUtil;
import org.sigmah.server.dao.FrameworkDAO;
import org.sigmah.server.dao.ProfileDAO;
import org.sigmah.server.dao.ProjectModelDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.*;
import org.sigmah.server.domain.element.DefaultFlexibleElement;
import org.sigmah.server.domain.layout.Layout;
import org.sigmah.server.domain.layout.LayoutConstraint;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.domain.logframe.LogFrameModel;
import org.sigmah.server.domain.profile.Profile;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.ModelUtil;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dto.PhaseModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;
import org.sigmah.shared.dto.referential.ProjectModelStatus;
import org.sigmah.shared.dto.referential.ProjectModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Date;
import java.util.HashSet;
import org.sigmah.server.computation.ServerDependencyResolver;
import org.sigmah.server.domain.element.BudgetRatioElement;
import org.sigmah.server.i18n.I18nServer;

/**
 * Handler for updating Project model command.
 *
 * @author nrebiai
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ProjectModelService extends AbstractEntityService<ProjectModel, Integer, ProjectModelDTO> {

	/**
	 * Logger.
	 */
	private final static Logger LOG = LoggerFactory.getLogger(ProjectModelService.class);

	/**
	 * Injected application mapper.
	 */
	@Inject
	private Mapper mapper;

	@Inject
	private ModelUtil modelUtil;

	/**
	 * Injected {@link ProjectModelDAO}.
	 */
	@Inject
	private ProjectModelDAO projectModelDAO;
	
	/**
	 * Injected {@link I18nServer}. Handle localization of lables.
	 */
	@Inject
	private I18nServer i18n;
    
	@Inject
	private ProfileDAO profileDAO;
	
	@Inject
	private FrameworkDAO frameworkDAO;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectModel create(PropertyMap properties, final UserExecutionContext context) {

		final ProjectModelDTO projectModel = (ProjectModelDTO) properties.get(AdminUtil.ADMIN_PROJECT_MODEL);

		// Only draft models can be changed.
		if (projectModel == null) {
			return null;
		}

		if (projectModel.getId() != null) {
			// Properties can only contain actual changes between old version and new one as verification has already been
			// done.
			return update(projectModel.getId(), properties, context);
		}

		// Create new draft ProjectModel
		ProjectModel pM = new ProjectModel();
		String pMName = (String) properties.get(AdminUtil.PROP_PM_NAME);
		ProjectModelType pMUse = (ProjectModelType) properties.get(AdminUtil.PROP_PM_USE);

		pM.setName(pMName);
		pM.setStatus(ProjectModelStatus.DRAFT);
		List<ProjectModelVisibility> visibilities = new ArrayList<ProjectModelVisibility>();
		ProjectModelVisibility v = new ProjectModelVisibility();
		v.setModel(pM);
		v.setType(pMUse);
		v.setOrganization(context.getUser().getOrganization());
		visibilities.add(v);
		pM.setVisibilities(visibilities);

		// Project model details
		ProjectDetails pMDetails = new ProjectDetails();

		Layout pMDetailsLayout = new Layout();
		pMDetailsLayout.setColumnsCount(1);
		pMDetailsLayout.setRowsCount(1);
		pMDetails.setLayout(pMDetailsLayout);
		pMDetails.setProjectModel(pM);

		LayoutGroup detailsGroup = new LayoutGroup();
		detailsGroup.setTitle("Default details group");
		detailsGroup.setColumn(0);
		detailsGroup.setRow(0);
		detailsGroup.setParentLayout(pMDetailsLayout);

		// Default flexible elements all in default details group
		int order = 0;
		for (final DefaultFlexibleElementType type : DefaultFlexibleElementType.values()) {
			DefaultFlexibleElement defaultElement;
			if (type == DefaultFlexibleElementType.BUDGET) {
				continue;
			} else if (type == DefaultFlexibleElementType.BUDGET_RATIO) {
				defaultElement = new BudgetRatioElement();
				defaultElement.setLabel(i18n.t(context.getLanguage(), "flexibleElementBudgetRatio"));
			} else {
				defaultElement = new DefaultFlexibleElement();
			}
			defaultElement.setType(type);
			defaultElement.setValidates(false);
			defaultElement.setAmendable(true);
			em().persist(defaultElement);
			LayoutConstraint defaultLayoutConstraint = new LayoutConstraint();
			defaultLayoutConstraint.setParentLayoutGroup(detailsGroup);
			defaultLayoutConstraint.setElement(defaultElement);
			defaultLayoutConstraint.setSortOrder(order++);
			detailsGroup.addConstraint(defaultLayoutConstraint);
		}

		List<LayoutGroup> detailsGroups = new ArrayList<LayoutGroup>();
		detailsGroups.add(detailsGroup);
		pMDetailsLayout.setGroups(detailsGroups);

		// Banner and groups for banner
		ProjectBanner pMBanner = new ProjectBanner();
		Layout pMBannerLayout = new Layout();
		pMBannerLayout.setColumnsCount(3);
		pMBannerLayout.setRowsCount(2);
		pMBanner.setLayout(pMBannerLayout);
		pMBanner.setProjectModel(pM);

		List<LayoutGroup> bannerGroups = new ArrayList<LayoutGroup>();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				LayoutGroup bannerGroup = new LayoutGroup();
				bannerGroup.setColumn(i);
				bannerGroup.setRow(j);
				bannerGroup.setParentLayout(pMBannerLayout);
				bannerGroups.add(bannerGroup);
			}
		}

		pMBannerLayout.setGroups(bannerGroups);

		pM.setProjectDetails(pMDetails);
		pM.setProjectBanner(pMBanner);

		projectModelDAO.persist(pM, context.getUser());
		// Add a root phase : one model has minimum one phase
		PhaseModel defaultRootPhase = new PhaseModel();
		defaultRootPhase.setName("Default root phase");
		defaultRootPhase.setParentProjectModel(pM);
		defaultRootPhase.setDisplayOrder(0);
		Layout phaseLayout = new Layout();
		phaseLayout.setColumnsCount(1);
		phaseLayout.setRowsCount(1);

		LayoutGroup phaseGroup = new LayoutGroup();
		phaseGroup.setTitle("Default phase group");
		phaseGroup.setColumn(0);
		phaseGroup.setRow(0);
		phaseGroup.setParentLayout(phaseLayout);

		List<LayoutGroup> phaseGroups = new ArrayList<LayoutGroup>();
		phaseGroups.add(phaseGroup);
		phaseLayout.setGroups(phaseGroups);

		defaultRootPhase.setLayout(phaseLayout);
		em().persist(defaultRootPhase);

		LogFrameModel defaultLogFrame = createDefaultLogFrameModel(pM);
		em().persist(defaultLogFrame);

		pM.setRootPhaseModel(defaultRootPhase);
		pM.setLogFrameModel(defaultLogFrame);

		return projectModelDAO.persist(pM, context.getUser());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectModel update(final Integer entityId, final PropertyMap changes, final UserExecutionContext context) {

		LOG.debug("Begins update the model.");

		ProjectModel model = projectModelDAO.findById(entityId);

		if (model == null) {
			throw new IllegalArgumentException("No ProjetModel can be found with id #" + entityId);
		}

		if (changes.get(AdminUtil.PROP_PM_NAME) != null) {
			model.setName((String) changes.get(AdminUtil.PROP_PM_NAME));
		}
		if (changes.get(AdminUtil.PROP_PM_STATUS) != null) {
			final ProjectModelStatus newStatus = changes.get(AdminUtil.PROP_PM_STATUS);
			if (frameworkDAO.countNotImplementedElementsByProjectModelId(model.getId()) > 0) {
				throw new IllegalArgumentException("A framework requirement was not entirely fulfilled.");
			}

			model.setStatus(newStatus);
		}
		if (changes.get(AdminUtil.PROP_PM_USE) instanceof ProjectModelType) {
			for (final ProjectModelVisibility v : model.getVisibilities()) {
				if (context.getUser().getOrganization().getId().equals(v.getOrganization().getId())) {
					v.setType((ProjectModelType) changes.get(AdminUtil.PROP_PM_USE));
					em().merge(v);
				}
			}
		}
		if (changes.containsKey(AdminUtil.PROP_PM_MAINTENANCE_DATE)) {
			final Object maintenanceDate = changes.get(AdminUtil.PROP_PM_MAINTENANCE_DATE);
			if(maintenanceDate instanceof Date) {
				model.setDateMaintenance((Date)maintenanceDate);
			} else {
				model.setDateMaintenance(null);
			}
		}
		if (changes.containsKey(AdminUtil.PROP_PM_DEFAULT_TEAM_MEMBER_PROFILES)) {
			Object defaultProfileIds = changes.get(AdminUtil.PROP_PM_DEFAULT_TEAM_MEMBER_PROFILES);
			if (defaultProfileIds instanceof Set) {
				Set<Integer> profileIds = (Set<Integer>) defaultProfileIds;
				if (profileIds.isEmpty()) {
					model.setDefaultTeamMemberProfiles(null);
				} else {
					List<Profile> profiles = profileDAO.findByIds(profileIds);
					model.setDefaultTeamMemberProfiles(profiles);
				}
			} else {
				model.setDefaultTeamMemberProfiles(null);
			}
		}

		model = em().merge(model);

		// --
		// Log Frame Model.
		// --

		if (changes.get(AdminUtil.PROP_LOG_FRAME) != null && (Boolean) changes.get(AdminUtil.PROP_LOG_FRAME)) {

			LogFrameModel logFrameModel = null;
			if (model.getLogFrameModel() != null) {
				logFrameModel = em().find(LogFrameModel.class, model.getLogFrameModel().getId());

			} else {
				logFrameModel = new LogFrameModel();
				logFrameModel.setProjectModel(model);
			}

			// BUGFIX #758 : Using containsKey instead of != null to verify if a property must be updated.
			if (changes.containsKey(AdminUtil.PROP_LOG_FRAME_NAME)) {
				logFrameModel.setName((String) changes.get(AdminUtil.PROP_LOG_FRAME_NAME));
			}
			if (changes.containsKey(AdminUtil.PROP_OBJ_MAX)) {
				logFrameModel.setSpecificObjectivesMax((Integer) changes.get(AdminUtil.PROP_OBJ_MAX));
			}
			if (changes.containsKey(AdminUtil.PROP_OBJ_MAX_PER_GROUP)) {
				logFrameModel.setSpecificObjectivesPerGroupMax((Integer) changes.get(AdminUtil.PROP_OBJ_MAX_PER_GROUP));
			}
			if (changes.containsKey(AdminUtil.PROP_OBJ_ENABLE_GROUPS)) {
				logFrameModel.setEnableSpecificObjectivesGroups((Boolean) changes.get(AdminUtil.PROP_OBJ_ENABLE_GROUPS));
			}
			if (changes.containsKey(AdminUtil.PROP_OBJ_MAX_GROUPS)) {
				logFrameModel.setSpecificObjectivesGroupsMax((Integer) changes.get(AdminUtil.PROP_OBJ_MAX_GROUPS));
			}
			if (changes.containsKey(AdminUtil.PROP_A_MAX)) {
				logFrameModel.setActivitiesMax((Integer) changes.get(AdminUtil.PROP_A_MAX));
			}
			if (changes.containsKey(AdminUtil.PROP_A_ENABLE_GROUPS)) {
				logFrameModel.setEnableActivitiesGroups((Boolean) changes.get(AdminUtil.PROP_A_ENABLE_GROUPS));
			}
			if (changes.containsKey(AdminUtil.PROP_A_MAX_PER_RESULT)) {
				logFrameModel.setActivitiesPerExpectedResultMax((Integer) changes.get(AdminUtil.PROP_A_MAX_PER_RESULT));
			}
			if (changes.containsKey(AdminUtil.PROP_A_MAX_GROUPS)) {
				logFrameModel.setActivitiesGroupsMax((Integer) changes.get(AdminUtil.PROP_A_MAX_GROUPS));
			}
			if (changes.containsKey(AdminUtil.PROP_A_MAX_PER_GROUP)) {
				logFrameModel.setActivitiesPerGroupMax((Integer) changes.get(AdminUtil.PROP_A_MAX_PER_GROUP));
			}
			if (changes.containsKey(AdminUtil.PROP_R_MAX)) {
				logFrameModel.setExpectedResultsMax((Integer) changes.get(AdminUtil.PROP_R_MAX));
			}
			if (changes.containsKey(AdminUtil.PROP_R_ENABLE_GROUPS)) {
				logFrameModel.setEnableExpectedResultsGroups((Boolean) changes.get(AdminUtil.PROP_R_ENABLE_GROUPS));
			}
			if (changes.containsKey(AdminUtil.PROP_R_MAX_PER_OBJ)) {
				logFrameModel.setExpectedResultsPerSpecificObjectiveMax((Integer) changes.get(AdminUtil.PROP_R_MAX_PER_OBJ));
			}
			if (changes.containsKey(AdminUtil.PROP_R_MAX_GROUPS)) {
				logFrameModel.setExpectedResultsGroupsMax((Integer) changes.get(AdminUtil.PROP_R_MAX_GROUPS));
			}
			if (changes.containsKey(AdminUtil.PROP_R_MAX_PER_GROUP)) {
				logFrameModel.setExpectedResultsPerGroupMax((Integer) changes.get(AdminUtil.PROP_R_MAX_PER_GROUP));
			}
			if (changes.containsKey(AdminUtil.PROP_P_MAX)) {
				logFrameModel.setPrerequisitesMax((Integer) changes.get(AdminUtil.PROP_P_MAX));
			}
			if (changes.containsKey(AdminUtil.PROP_P_ENABLE_GROUPS)) {
				logFrameModel.setEnablePrerequisitesGroups((Boolean) changes.get(AdminUtil.PROP_P_ENABLE_GROUPS));
			}
			if (changes.containsKey(AdminUtil.PROP_P_MAX_GROUPS)) {
				logFrameModel.setPrerequisitesGroupsMax((Integer) changes.get(AdminUtil.PROP_P_MAX_GROUPS));
			}
			if (changes.containsKey(AdminUtil.PROP_P_MAX_PER_GROUP)) {
				logFrameModel.setPrerequisitesPerGroupMax((Integer) changes.get(AdminUtil.PROP_P_MAX_PER_GROUP));
			}

			if (model.getLogFrameModel() != null) {
				logFrameModel = em().merge(logFrameModel);

			} else {
				em().persist(logFrameModel);
			}

			model.setLogFrameModel(logFrameModel);
		}

		// --
		// Flexible Element.
		// --

		if (changes.get(AdminUtil.PROP_FX_FLEXIBLE_ELEMENT) != null) {

			modelUtil.persistFlexibleElement(changes, model);

			model = projectModelDAO.findById(model.getId());

		}

		// --
		// Phases.
		// --

		final PhaseModelDTO phaseDTOToSave = (PhaseModelDTO) changes.get(AdminUtil.PROP_PHASE_MODEL);

		final Integer displayOrder = (Integer) changes.get(AdminUtil.PROP_PHASE_ORDER);
		final Boolean root = (Boolean) changes.get(AdminUtil.PROP_PHASE_ROOT);
		final Integer numRows = (Integer) changes.get(AdminUtil.PROP_PHASE_ROWS);
		final String guide = (String) changes.get(AdminUtil.PROP_PHASE_GUIDE);

		if (phaseDTOToSave != null) {
			PhaseModel phaseToSave;

			PhaseModel phaseFound = null;
			for (final PhaseModel phase : model.getPhaseModels()) {
				if (phaseDTOToSave.getId() != null && phaseDTOToSave.getId().equals(phase.getId())) {
					phaseFound = phase;
				}
			}

			if(phaseFound != null) {
				phaseToSave = phaseFound;
			} else {
				phaseToSave = new PhaseModel();
			}

			phaseToSave.setName(phaseDTOToSave.getName());
			if (displayOrder != null) {
				phaseToSave.setDisplayOrder(displayOrder);
			}
			// Guide
			if (guide != null) {
				phaseToSave.setGuide(guide);
			}
			// successors
			final HashSet<PhaseModel> existingPhaseModels = new HashSet<PhaseModel>(phaseToSave.getSuccessors());
			for (PhaseModelDTO sucDTO : phaseDTOToSave.getSuccessors()) {
				if (sucDTO.getId() != null && sucDTO.getId() > 0) {
					final PhaseModel successor = em().find(PhaseModel.class, sucDTO.getId());
					if(!existingPhaseModels.contains(successor)) {
						phaseToSave.getSuccessors().add(successor);
					} else {
						existingPhaseModels.remove(successor);
					}
				}
			}

			for(final PhaseModel removedSuccessor : existingPhaseModels) {
				phaseToSave.getSuccessors().remove(removedSuccessor);
			}

			if (phaseFound != null) {
				// phase is old
				phaseToSave = em().merge(phaseToSave);

			} else {
				// create new phase
				phaseToSave.setParentProjectModel(model);
				Layout phaseLayout = new Layout();
				phaseLayout.setColumnsCount(1);
				if (numRows != null)
					phaseLayout.setRowsCount(numRows);
				else
					phaseLayout.setRowsCount(1);

				LayoutGroup phaseGroup = new LayoutGroup();
				phaseGroup.setTitle(phaseToSave.getName() + " default group");
				phaseGroup.setColumn(0);
				phaseGroup.setRow(0);
				phaseGroup.setParentLayout(phaseLayout);

				List<LayoutGroup> phaseGroups = new ArrayList<LayoutGroup>();
				phaseGroups.add(phaseGroup);
				phaseLayout.setGroups(phaseGroups);

				phaseToSave.setLayout(phaseLayout);
				em().persist(phaseToSave);

				model.addPhase(phaseToSave);
			}

			if (root != null && root) {
				model.setRootPhaseModel(phaseToSave);

			} else if (root != null && !root && phaseFound != null && model.getRootPhaseModel().getId().equals(phaseFound.getId())) {
				// Only if phase was root and we want to change it.
				model.setRootPhaseModel(null);
			}

			model = em().merge(model);
		}

		return model;
	}

	/**
	 * Creates a new {@link LogFrameModel} from the given {@code model}.
	 *
	 * @param model
	 *          The project model.
	 * @return The {@link LogFrameModel} instance.
	 */
	static LogFrameModel createDefaultLogFrameModel(final ProjectModel model) {

		final LogFrameModel logFrameModel = new LogFrameModel();

		logFrameModel.setName("Default log frame");
		logFrameModel.setActivitiesGroupsMax(3);
		logFrameModel.setActivitiesMax(3);
		logFrameModel.setActivitiesPerExpectedResultMax(3);
		logFrameModel.setActivitiesPerGroupMax(3);
		logFrameModel.setEnableActivitiesGroups(true);

		logFrameModel.setEnableExpectedResultsGroups(true);
		logFrameModel.setExpectedResultsGroupsMax(3);
		logFrameModel.setExpectedResultsMax(3);
		logFrameModel.setExpectedResultsPerGroupMax(3);
		logFrameModel.setExpectedResultsPerSpecificObjectiveMax(3);

		logFrameModel.setSpecificObjectivesGroupsMax(3);
		logFrameModel.setEnableSpecificObjectivesGroups(true);
		logFrameModel.setSpecificObjectivesMax(3);
		logFrameModel.setSpecificObjectivesPerGroupMax(3);

		logFrameModel.setPrerequisitesGroupsMax(3);
		logFrameModel.setEnablePrerequisitesGroups(true);
		logFrameModel.setPrerequisitesMax(3);
		logFrameModel.setPrerequisitesPerGroupMax(3);

		logFrameModel.setProjectModel(model);

		return logFrameModel;
	}

}
