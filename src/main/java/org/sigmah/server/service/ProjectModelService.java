package org.sigmah.server.service;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.util.AdminUtil;
import org.sigmah.server.dao.ProjectModelDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.PhaseModel;
import org.sigmah.server.domain.ProjectBanner;
import org.sigmah.server.domain.ProjectDetails;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.ProjectModelVisibility;
import org.sigmah.server.domain.element.BudgetElement;
import org.sigmah.server.domain.element.BudgetSubField;
import org.sigmah.server.domain.element.DefaultFlexibleElement;
import org.sigmah.server.domain.layout.Layout;
import org.sigmah.server.domain.layout.LayoutConstraint;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.domain.logframe.LogFrameModel;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.ModelUtil;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dto.PhaseModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.referential.BudgetSubFieldType;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;
import org.sigmah.shared.dto.referential.ProjectModelStatus;
import org.sigmah.shared.dto.referential.ProjectModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Date;
import java.util.HashSet;

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

	/**
	 * Injected {@link ProjectModelDAO}.
	 */
	@Inject
	private ProjectModelDAO projectModelDAO;

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
		for (DefaultFlexibleElementType e : DefaultFlexibleElementType.values()) {
			DefaultFlexibleElement defaultElement;
			if (DefaultFlexibleElementType.BUDGET.equals(e)) {
				defaultElement = new BudgetElement();

				List<BudgetSubField> budgetSubFields = new ArrayList<BudgetSubField>();
				// Adds the 3 default budget sub fields
				int y = 1;
				for (BudgetSubFieldType type : BudgetSubFieldType.values()) {
					BudgetSubField b = new BudgetSubField();
					b.setBudgetElement(((BudgetElement) defaultElement));
					b.setType(type);
					b.setFieldOrder(y);
					if (BudgetSubFieldType.PLANNED.equals(type)) {
						((BudgetElement) defaultElement).setRatioDivisor(b);
					} else if (BudgetSubFieldType.SPENT.equals(type)) {
						((BudgetElement) defaultElement).setRatioDividend(b);
					}
					budgetSubFields.add(b);
					y++;
				}
				((BudgetElement) defaultElement).setBudgetSubFields(budgetSubFields);
			} else {
				defaultElement = new DefaultFlexibleElement();
			}
			defaultElement.setType(e);
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
			model.setStatus((ProjectModelStatus) changes.get(AdminUtil.PROP_PM_STATUS));
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

			if (changes.get(AdminUtil.PROP_LOG_FRAME_NAME) != null) {
				logFrameModel.setName((String) changes.get(AdminUtil.PROP_LOG_FRAME_NAME));
			}
			if (changes.get(AdminUtil.PROP_OBJ_MAX) != null) {
				logFrameModel.setSpecificObjectivesMax((Integer) changes.get(AdminUtil.PROP_OBJ_MAX));
			}
			if (changes.get(AdminUtil.PROP_OBJ_MAX_PER_GROUP) != null) {
				logFrameModel.setSpecificObjectivesPerGroupMax((Integer) changes.get(AdminUtil.PROP_OBJ_MAX_PER_GROUP));
			}
			if (changes.get(AdminUtil.PROP_OBJ_ENABLE_GROUPS) != null) {
				logFrameModel.setEnableSpecificObjectivesGroups((Boolean) changes.get(AdminUtil.PROP_OBJ_ENABLE_GROUPS));
			}
			if (changes.get(AdminUtil.PROP_OBJ_MAX_GROUPS) != null) {
				logFrameModel.setSpecificObjectivesGroupsMax((Integer) changes.get(AdminUtil.PROP_OBJ_MAX_GROUPS));
			}
			if (changes.get(AdminUtil.PROP_A_MAX) != null) {
				logFrameModel.setActivitiesMax((Integer) changes.get(AdminUtil.PROP_A_MAX));
			}
			if (changes.get(AdminUtil.PROP_A_ENABLE_GROUPS) != null) {
				logFrameModel.setEnableActivitiesGroups((Boolean) changes.get(AdminUtil.PROP_A_ENABLE_GROUPS));
			}
			if (changes.get(AdminUtil.PROP_A_MAX_PER_RESULT) != null) {
				logFrameModel.setActivitiesPerExpectedResultMax((Integer) changes.get(AdminUtil.PROP_A_MAX_PER_RESULT));
			}
			if (changes.get(AdminUtil.PROP_A_MAX_GROUPS) != null) {
				logFrameModel.setActivitiesGroupsMax((Integer) changes.get(AdminUtil.PROP_A_MAX_GROUPS));
			}
			if (changes.get(AdminUtil.PROP_A_MAX_PER_GROUP) != null) {
				logFrameModel.setActivitiesPerGroupMax((Integer) changes.get(AdminUtil.PROP_A_MAX_PER_GROUP));
			}
			if (changes.get(AdminUtil.PROP_R_MAX) != null) {
				logFrameModel.setExpectedResultsMax((Integer) changes.get(AdminUtil.PROP_R_MAX));
			}
			if (changes.get(AdminUtil.PROP_R_ENABLE_GROUPS) != null) {
				logFrameModel.setEnableExpectedResultsGroups((Boolean) changes.get(AdminUtil.PROP_R_ENABLE_GROUPS));
			}
			if (changes.get(AdminUtil.PROP_R_MAX_PER_OBJ) != null) {
				logFrameModel.setExpectedResultsPerSpecificObjectiveMax((Integer) changes.get(AdminUtil.PROP_R_MAX_PER_OBJ));
			}
			if (changes.get(AdminUtil.PROP_R_MAX_GROUPS) != null) {
				logFrameModel.setExpectedResultsGroupsMax((Integer) changes.get(AdminUtil.PROP_R_MAX_GROUPS));
			}
			if (changes.get(AdminUtil.PROP_R_MAX_PER_GROUP) != null) {
				logFrameModel.setExpectedResultsPerGroupMax((Integer) changes.get(AdminUtil.PROP_R_MAX_PER_GROUP));
			}
			if (changes.get(AdminUtil.PROP_P_MAX) != null) {
				logFrameModel.setPrerequisitesMax((Integer) changes.get(AdminUtil.PROP_P_MAX));
			}
			if (changes.get(AdminUtil.PROP_P_ENABLE_GROUPS) != null) {
				logFrameModel.setEnablePrerequisitesGroups((Boolean) changes.get(AdminUtil.PROP_P_ENABLE_GROUPS));
			}
			if (changes.get(AdminUtil.PROP_P_MAX_GROUPS) != null) {
				logFrameModel.setPrerequisitesGroupsMax((Integer) changes.get(AdminUtil.PROP_P_MAX_GROUPS));
			}
			if (changes.get(AdminUtil.PROP_P_MAX_PER_GROUP) != null) {
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

			ModelUtil.persistFlexibleElement(em(), mapper, changes, model);
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
