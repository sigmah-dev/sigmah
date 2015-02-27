package org.sigmah.shared.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.base.mapping.CustomMappingField;
import org.sigmah.shared.dto.base.mapping.IsMappingMode;
import org.sigmah.shared.dto.base.mapping.MappingField;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.logframe.LogFrameModelDTO;
import org.sigmah.shared.dto.referential.ElementTypeEnum;
import org.sigmah.shared.dto.referential.ProjectModelStatus;
import org.sigmah.shared.dto.referential.ProjectModelType;

/**
 * ProjectModelDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class ProjectModelDTO extends AbstractModelDataEntityDTO<Integer> implements IsModel {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8508466949743884046L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "ProjectModel";

	// DTO attributes keys.
	public static final String NAME = "name";
	public static final String STATUS = "status";

	public static final String VISIBILITIES = "visibilities";
	public static final String ROOT_PHASE_MODEL = "rootPhaseModel";
	public static final String PHASE_MODELS = "phaseModels";
	public static final String PROJECT_BANNER = "projectBanner";
	public static final String PROJECT_DETAILS = "projectDetails";
	public static final String LOG_FRAME_MODEL = "logFrameModel";
	public static final String MAINTENANCE_DATE = "dateMaintenance";
	public static final String UNDER_MAINTENANCE = "underMaintenance";

	/**
	 * Mapping configurations.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
	 */
	public static enum Mode implements IsMappingMode {

		/**
		 * Base mapping retrieving only project base data (no related DTO).
		 */
		BASE(
					new MappingField(VISIBILITIES),
					new MappingField(ROOT_PHASE_MODEL),
					new MappingField(PHASE_MODELS),
					new MappingField(PROJECT_BANNER),
					new MappingField(PROJECT_DETAILS),
					new MappingField(LOG_FRAME_MODEL)),

		/**
		 * In addition to base data, this mapping includes:
		 * <ul>
		 * <li>{@link ProjectModelDTO#VISIBILITIES}</li>
		 * </ul>
		 */
		WITH_VISIBILITIES(
											new MappingField(ROOT_PHASE_MODEL),
											new MappingField(PHASE_MODELS),
											new MappingField(PROJECT_BANNER),
											new MappingField(PROJECT_DETAILS),
											new MappingField(LOG_FRAME_MODEL)),

		/**
		 * In addition to base data, this mapping includes:
		 * <ul>
		 * <li>{@link ProjectModelDTO#VISIBILITIES}</li>
		 * <li>{@link ProjectModelDTO#PROJECT_BANNER}</li>
		 * </ul>
		 */
		WITH_VISIBILITIES_AND_BANNER(new MappingField(ROOT_PHASE_MODEL), new MappingField(PHASE_MODELS), new MappingField(PROJECT_DETAILS), new MappingField(
			LOG_FRAME_MODEL)),

		/**
		 * Mapping all data
		 */
		ALL(), ;

		private final CustomMappingField[] customFields;
		private final MappingField[] excludedFields;

		private Mode(final MappingField... excludedFields) {
			this(null, excludedFields);
		}

		private Mode(final CustomMappingField... customFields) {
			this(customFields, (MappingField[]) null);
		}

		private Mode(final CustomMappingField[] customFields, final MappingField... excludedFields) {
			this.customFields = customFields;
			this.excludedFields = excludedFields;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getMapId() {
			return name();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CustomMappingField[] getCustomFields() {
			return customFields;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MappingField[] getExcludedFields() {
			return excludedFields;
		}
	}

	/**
	 * Localizes an flexible element in the project model.
	 * 
	 * @author tmi (v1.3)
	 */
	protected static class LocalizedElement {

		private final PhaseModelDTO phaseModel;
		private final FlexibleElementDTO element;

		protected LocalizedElement(PhaseModelDTO phaseModel, FlexibleElementDTO element) {
			this.phaseModel = phaseModel;
			this.element = element;
		}

		/**
		 * Gets the flexible element.
		 * 
		 * @return The flexible element.
		 */
		public FlexibleElementDTO getElement() {
			return element;
		}

		/**
		 * Get the phase model in which the element is displayed, or <code>null</code> if the element is in the details
		 * page.
		 * 
		 * @return The phase model of the element or <code>null</code>.
		 */
		public PhaseModelDTO getPhaseModel() {
			return phaseModel;
		}
	}

	private transient HashMap<Class<? extends FlexibleElementDTO>, List<LocalizedElement>> localizedElements;

	public ProjectModelDTO() {
		// Serialization.
	}

	/**
	 * Initializes a new project model with the given {@code status}.
	 * 
	 * @param status
	 *          The project model status.
	 */
	public ProjectModelDTO(final ProjectModelStatus status) {
		setStatus(status);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelType getModelType() {
		return ModelType.ProjectModel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AbstractModelDataEntityDTO<?>> getHasLayoutElements() {

		final List<AbstractModelDataEntityDTO<?>> hasLayoutElements = new ArrayList<AbstractModelDataEntityDTO<?>>();

		hasLayoutElements.add(getProjectDetails());

		if (getPhaseModels() != null) {
			for (final PhaseModelDTO phaseModel : getPhaseModels()) {
				hasLayoutElements.add(phaseModel);
			}
		}

		return hasLayoutElements;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(ToStringBuilder builder) {
		builder.append(NAME, getName());
		builder.append(STATUS, getStatus());
	}

	// ---------------------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// ---------------------------------------------------------------------------------------------

	// Project model name.
	@Override
	public String getName() {
		return get(NAME);
	}

	public void setName(String name) {
		set(NAME, name);
	}

	// Root phase model DTO.
	public PhaseModelDTO getRootPhaseModel() {
		return get(ROOT_PHASE_MODEL);
	}

	public void setRootPhaseModel(PhaseModelDTO rootPhaseModel) {
		set(ROOT_PHASE_MODEL, rootPhaseModel);
	}

	// Phase models list.
	public List<PhaseModelDTO> getPhaseModels() {
		return get(PHASE_MODELS);
	}

	public void setPhaseModels(List<PhaseModelDTO> phaseModels) {
		set(PHASE_MODELS, phaseModels);
	}

	// Reference to the project banner.
	public ProjectBannerDTO getProjectBanner() {
		return get(PROJECT_BANNER);
	}

	public void setProjectBanner(ProjectBannerDTO projectBanner) {
		set(PROJECT_BANNER, projectBanner);
	}

	// Reference to the project details.
	public ProjectDetailsDTO getProjectDetails() {
		return get(PROJECT_DETAILS);
	}

	public void setProjectDetails(ProjectDetailsDTO projectDetails) {
		set(PROJECT_DETAILS, projectDetails);
	}

	// Project visibilities.
	public List<ProjectModelVisibilityDTO> getVisibilities() {
		return get(VISIBILITIES);
	}

	public void setVisibilities(List<ProjectModelVisibilityDTO> visibilities) {
		set(VISIBILITIES, visibilities);
	}

	// LogFrame.
	public LogFrameModelDTO getLogFrameModel() {
		return get(LOG_FRAME_MODEL);
	}

	public void setLogFrameModel(LogFrameModelDTO logFrameModel) {
		set(LOG_FRAME_MODEL, logFrameModel);
	}

	// Status.
	@Override
	public ProjectModelStatus getStatus() {
		return get(STATUS);
	}

	public void setStatus(ProjectModelStatus status) {
		set(STATUS, status);
	}
	
	// Maintenance.
	@Override
	public boolean isUnderMaintenance() {
		return get(UNDER_MAINTENANCE);
	}
	
	public void setUnderMaintenance(boolean underMaintenance) {
		set(UNDER_MAINTENANCE, underMaintenance);
	}
	
	// Maintenance start date.
	public Date getDateMaintenance() {
		return get(MAINTENANCE_DATE);
	}
	
	public void setDateMaintenance(Date date) {
		set(MAINTENANCE_DATE, date);
	}

	// ---------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------

	/**
	 * Gets the type of this model for the given organization. If this model isn't visible for this organization,
	 * <code>null</code> is returned.
	 * 
	 * @param organizationId
	 *          The organization.
	 * @return The type of this model for the given organization, <code>null</code> otherwise.
	 */
	public ProjectModelType getVisibility(int organizationId) {

		if (getVisibilities() == null) {
			return null;
		}

		for (final ProjectModelVisibilityDTO visibility : getVisibilities()) {
			if (visibility.getOrganizationId().equals(organizationId)) {
				return visibility.getType();
			}
		}

		return null;
	}

	/**
	 * Returns the current project model corresponding global export elements.<br>
	 * Only the following types of elements are returned:
	 * <ul>
	 * <li>{@link ElementTypeEnum#DEFAULT}</li>
	 * <li>{@link ElementTypeEnum#CHECKBOX}</li>
	 * <li>{@link ElementTypeEnum#TEXT_AREA}</li>
	 * <li>{@link ElementTypeEnum#TRIPLETS}</li>
	 * <li>{@link ElementTypeEnum#QUESTION}</li>
	 * </ul>
	 * 
	 * @return The current project model corresponding global export elements.
	 */
	public List<FlexibleElementDTO> getGlobalExportElements() {

		final List<FlexibleElementDTO> allElements = new ArrayList<FlexibleElementDTO>();

		// add phase groups
		for (final PhaseModelDTO phaseDTO : getPhaseModels()) {
			for (final LayoutGroupDTO lg : phaseDTO.getLayout().getGroups()) {
				for (final LayoutConstraintDTO lc : lg.getConstraints()) {
					final FlexibleElementDTO element = lc.getFlexibleElementDTO();
					element.setGroup(lg);
					element.setConstraint(lc);
					element.setContainerModel(phaseDTO);

					final ElementTypeEnum type = element.getElementType();
					if (ElementTypeEnum.DEFAULT == type
						|| ElementTypeEnum.CHECKBOX == type
						|| ElementTypeEnum.TEXT_AREA == type
						|| ElementTypeEnum.TRIPLETS == type
						|| ElementTypeEnum.QUESTION == type) {
						allElements.add(element);
					}
				}
			}
		}

		// add details groups
		final ProjectDetailsDTO p = getProjectDetails();
		p.setName();
		setProjectDetails(p);
		if (getProjectDetails().getLayout() != null) {
			for (final LayoutGroupDTO lg : getProjectDetails().getLayout().getGroups()) {
				for (final LayoutConstraintDTO lc : lg.getConstraints()) {
					final FlexibleElementDTO element = lc.getFlexibleElementDTO();
					element.setGroup(lg);
					element.setConstraint(lc);
					element.setContainerModel(getProjectDetails());

					final ElementTypeEnum type = element.getElementType();
					if (ElementTypeEnum.DEFAULT == type
						|| ElementTypeEnum.CHECKBOX == type
						|| ElementTypeEnum.TEXT_AREA == type
						|| ElementTypeEnum.TRIPLETS == type
						|| ElementTypeEnum.QUESTION == type) {
						allElements.add(element);
					}
				}
			}
		}

		return allElements;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<FlexibleElementDTO> getAllElements() {

		final List<FlexibleElementDTO> allElements = new ArrayList<FlexibleElementDTO>();
		final List<FlexibleElementDTO> bannerElements = new ArrayList<FlexibleElementDTO>();

		// --
		// Banner.
		// --

		if (this.getProjectBanner().getLayout() != null) {
			for (final LayoutGroupDTO lg : getProjectBanner().getLayout().getGroups()) {
				for (final LayoutConstraintDTO lc : lg.getConstraints()) {
					final FlexibleElementDTO f = lc.getFlexibleElementDTO();
					f.setBannerConstraint(lc);
					bannerElements.add(f);
				}
			}
		}

		// --
		// Phases.
		// --

		for (final PhaseModelDTO phaseDTO : getPhaseModels()) {
			for (final LayoutGroupDTO lg : phaseDTO.getLayout().getGroups()) {
				for (final LayoutConstraintDTO lc : lg.getConstraints()) {
					final FlexibleElementDTO f = lc.getFlexibleElementDTO();
					f.setGroup(lg);
					f.setConstraint(lc);
					f.setContainerModel(phaseDTO);
					for (final FlexibleElementDTO bf : bannerElements) {
						if (f.getId().equals(bf.getId())) {
							f.setBannerConstraint(bf.getBannerConstraint());
						}
					}
					allElements.add(f);
				}
			}
		}

		// --
		// Project Details.
		// --

		final ProjectDetailsDTO p = getProjectDetails();
		p.setName();
		setProjectDetails(p);
		if (getProjectDetails().getLayout() != null) {
			for (final LayoutGroupDTO lg : getProjectDetails().getLayout().getGroups()) {
				for (final LayoutConstraintDTO lc : lg.getConstraints()) {
					final FlexibleElementDTO f = lc.getFlexibleElementDTO();
					f.setGroup(lg);
					f.setConstraint(lc);
					f.setContainerModel(getProjectDetails());
					for (final FlexibleElementDTO bf : bannerElements) {
						if (f.getId().equals(bf.getId())) {
							f.setBannerConstraint(bf.getBannerConstraint());
						}
					}
					allElements.add(f);
				}
			}
		}

		return allElements;
	}

	/**
	 * Gets all the flexible elements instances of the given class in this model (phases and details page). The banner is
	 * ignored cause the elements in it are read-only.
	 * 
	 * @param clazz
	 *          The class of the searched flexible elements.
	 * @return The elements localized for the given class, or <code>null</code> if there is no element of this class.
	 */
	public List<LocalizedElement> getLocalizedElements(Class<? extends FlexibleElementDTO> clazz) {

		if (localizedElements != null) {
			return localizedElements.get(clazz);
		}

		localizedElements = new HashMap<Class<? extends FlexibleElementDTO>, List<LocalizedElement>>();

		// Details.
		for (final LayoutGroupDTO group : getProjectDetails().getLayout().getGroups()) {

			// For each constraint.
			for (final LayoutConstraintDTO constraint : group.getConstraints()) {

				// Gets the element and its class.
				final FlexibleElementDTO element = constraint.getFlexibleElementDTO();
				List<LocalizedElement> elements = localizedElements.get(element.getClass());

				// First element for this class.
				if (elements == null) {
					elements = new ArrayList<LocalizedElement>();
					localizedElements.put(element.getClass(), elements);
				}

				// Maps the element.
				elements.add(new LocalizedElement(null, element));
			}
		}

		// For each phase.
		for (final PhaseModelDTO phaseModel : getPhaseModels()) {
			// For each group.
			for (final LayoutGroupDTO group : phaseModel.getLayout().getGroups()) {
				// For each constraint.
				for (final LayoutConstraintDTO constraint : group.getConstraints()) {

					// Gets the element and its class.
					final FlexibleElementDTO element = constraint.getFlexibleElementDTO();
					List<LocalizedElement> elements = localizedElements.get(element.getClass());

					// First element for this class.
					if (elements == null) {
						elements = new ArrayList<LocalizedElement>();
						localizedElements.put(element.getClass(), elements);
					}

					// Maps the element.
					elements.add(new LocalizedElement(phaseModel, element));
				}
			}
		}

		return localizedElements.get(clazz);
	}

}
