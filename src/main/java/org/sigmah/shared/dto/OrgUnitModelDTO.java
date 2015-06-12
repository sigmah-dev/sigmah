package org.sigmah.shared.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.base.mapping.CustomMappingField;
import org.sigmah.shared.dto.base.mapping.IsMappingMode;
import org.sigmah.shared.dto.base.mapping.MappingField;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

/**
 * OrgUnitModelDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class OrgUnitModelDTO extends AbstractModelDataEntityDTO<Integer> implements IsModel {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -6438355456637422931L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "OrgUnitModel";

	// DTO attributes keys.
	public static final String NAME = "name";
	public static final String TITLE = "title";
	public static final String HAS_BUDGET = "hasBudget";
	public static final String CAN_CONTAIN_PROJECTS = "canContainProjects";
	public static final String STATUS = "status";
	public static final String TOP_MODEL = "topModel";
	public static final String MAINTENANCE_DATE = "dateMaintenance";
	public static final String UNDER_MAINTENANCE = "underMaintenance";

	public static final String BANNER = "banner";
	public static final String DETAILS = "details";

	/**
	 * Mapping configurations.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static enum Mode implements IsMappingMode {

		/**
		 * Base mapping that does not map following data:
		 * <ul>
		 * <li> {@link OrgUnitModelDTO#BANNER}</li>
		 * <li> {@link OrgUnitModelDTO#DETAILS}</li>
		 * </ul>
		 */
		BASE(new MappingField(BANNER), new MappingField(DETAILS)),

		/**
		 * Mapping all data
		 */
		ALL();

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
	 * Localizes an flexible element in the organizational unit model.
	 * 
	 * @author kma (1.3)
	 */
	public static class LocalizedElement {

		private final FlexibleElementDTO element;

		public LocalizedElement(FlexibleElementDTO element) {
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
	}

	/**
	 * Localized flexible elements.
	 */
	private transient HashMap<Class<? extends FlexibleElementDTO>, List<LocalizedElement>> localizedElements;

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
		return ModelType.OrgUnitModel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AbstractModelDataEntityDTO<?>> getHasLayoutElements() {
		final List<AbstractModelDataEntityDTO<?>> hasLayoutElements = new ArrayList<AbstractModelDataEntityDTO<?>>();
		hasLayoutElements.add(getDetails());
		return hasLayoutElements;
	}

	// Name
	@Override
	public String getName() {
		return get(NAME);
	}

	public void setName(String name) {
		set(NAME, name);
	}

	// Title
	public String getTitle() {
		return get(TITLE);
	}

	public void setTitle(String title) {
		set(TITLE, title);
	}

	// Banner
	public OrgUnitBannerDTO getBanner() {
		return get(BANNER);
	}

	public void setBanner(OrgUnitBannerDTO banner) {
		set(BANNER, banner);
	}

	// Details
	public OrgUnitDetailsDTO getDetails() {
		return get(DETAILS);
	}

	public void setDetails(OrgUnitDetailsDTO details) {
		set(DETAILS, details);
	}

	// Budget
	public Boolean getHasBudget() {
		return (Boolean) get(HAS_BUDGET);
	}

	public void setHasBudget(Boolean hasBudget) {
		set(HAS_BUDGET, hasBudget);
	}

	// Can contain projects
	public Boolean getCanContainProjects() {
		return (Boolean) get(CAN_CONTAIN_PROJECTS);
	}

	public void setCanContainProjects(Boolean canContainProjects) {
		set(CAN_CONTAIN_PROJECTS, canContainProjects);
	}

	@Override
	public ProjectModelStatus getStatus() {
		return (ProjectModelStatus) get(STATUS);
	}

	public void setStatus(ProjectModelStatus status) {
		set(STATUS, status);
	}
	
	// Maintenance.
	@Override
	public boolean isUnderMaintenance() {
		final Boolean underMaintenance = get(UNDER_MAINTENANCE);
		return underMaintenance != null && underMaintenance;
	}
	
	public void setUnderMaintenance(boolean underMaintenance) {
		set(UNDER_MAINTENANCE, underMaintenance);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEditable() {
		return getStatus().isEditable() || isUnderMaintenance();
	}
	
	// Maintenance start date.
	@Override
	public Date getDateMaintenance() {
		return get(MAINTENANCE_DATE);
	}
	
	public void setDateMaintenance(Date date) {
		set(MAINTENANCE_DATE, date);
	}

	public void setTopOrgUnitModel(boolean is) {
		set(TOP_MODEL, is);
	}

	public boolean isTopOrgUnitModel() {
		return ClientUtils.isTrue(get(TOP_MODEL));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<FlexibleElementDTO> getAllElements() {
		List<FlexibleElementDTO> allElements = new ArrayList<FlexibleElementDTO>();
		List<FlexibleElementDTO> bannerElements = new ArrayList<FlexibleElementDTO>();

		// banner
		if (this.getBanner().getLayout() != null) {
			for (LayoutGroupDTO lg : getBanner().getLayout().getGroups()) {
				for (LayoutConstraintDTO lc : lg.getConstraints()) {
					FlexibleElementDTO f = lc.getFlexibleElementDTO();
					f.setBannerConstraint(lc);
					bannerElements.add(f);
				}
			}
		}

		// Details
		OrgUnitDetailsDTO d = getDetails();
		d.setName();
		setDetails(d);
		if (getDetails().getLayout() != null) {
			for (LayoutGroupDTO lg : getDetails().getLayout().getGroups()) {
				for (LayoutConstraintDTO lc : lg.getConstraints()) {
					FlexibleElementDTO f = lc.getFlexibleElementDTO();
					f.setGroup(lg);
					f.setConstraint(lc);
					f.setContainerModel(getDetails());
					for (FlexibleElementDTO bf : bannerElements) {
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
	 * Gets all the flexible elements instances of the given class in this model (details page). The banner is ignored
	 * cause the elements in it are read-only.
	 * 
	 * @param clazz
	 *          The class of the searched flexible elements.
	 * @return The elements localized for the given class, or <code>null</code> if there is no element of this class.
	 */
	public List<LocalizedElement> getLocalizedElements(Class<? extends FlexibleElementDTO> clazz) {

		if (localizedElements == null) {

			localizedElements = new HashMap<Class<? extends FlexibleElementDTO>, List<LocalizedElement>>();

			// Details
			for (final LayoutGroupDTO group : getDetails().getLayout().getGroups()) {

				// For each constraint
				for (final LayoutConstraintDTO constraint : group.getConstraints()) {

					// Gets the element and its class
					final FlexibleElementDTO element = constraint.getFlexibleElementDTO();
					List<LocalizedElement> elements = localizedElements.get(element.getClass());

					// First element for this class
					if (elements == null) {
						elements = new ArrayList<LocalizedElement>();
						localizedElements.put(element.getClass(), elements);
					}

					// Maps the element.
					elements.add(new LocalizedElement(element));
				}
			}
		}
		return localizedElements.get(clazz);
	}
}
