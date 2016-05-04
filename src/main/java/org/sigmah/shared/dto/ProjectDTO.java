package org.sigmah.shared.dto;

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
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.sigmah.client.ui.res.icon.dashboard.funding.FundingIconProvider;
import org.sigmah.client.ui.res.icon.dashboard.funding.FundingIconProvider.IconSize;
import org.sigmah.client.util.DateUtils;
import org.sigmah.client.util.NumberUtils;
import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractTreeModelEntityDTO;
import org.sigmah.shared.dto.base.mapping.CustomMappingField;
import org.sigmah.shared.dto.base.mapping.IsMappingMode;
import org.sigmah.shared.dto.base.mapping.MappingField;
import org.sigmah.shared.dto.category.CategoryElementDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementContainer;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.logframe.LogFrameDTO;
import org.sigmah.shared.dto.referential.AmendmentState;
import org.sigmah.shared.dto.referential.BudgetSubFieldType;
import org.sigmah.shared.dto.referential.ProjectModelType;
import org.sigmah.shared.dto.reminder.MonitoredPointListDTO;
import org.sigmah.shared.dto.reminder.ReminderListDTO;
import org.sigmah.shared.dto.value.ValueDTO;

import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.user.client.ui.Image;

/**
 * DTO mapping class for entity Project.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class ProjectDTO extends AbstractTreeModelEntityDTO<Integer> implements DefaultFlexibleElementContainer {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -8604264278832531036L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "Project";

	// DTO 'base' attributes keys.
	public static final String NAME = "name";
	public static final String FULL_NAME = "fullName";
	public static final String COMPLETE_NAME = "completeName";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String CLOSE_DATE = "closeDate";
	public static final String CALENDAR_ID = "calendarId";
	public static final String ACTIVITY_ADVANCEMENT = "activityAdvancement";
	public static final String AMENDMENT_STATE = "amendmentState";
	public static final String AMENDMENT_VERSION = "amendmentVersion";
	public static final String AMENDMENT_REVISION = "amendmentRevision";

	// Related objects/collections attributes keys.
	public static final String FUNDING = "funding";
	public static final String FUNDED = "funded";
	public static final String PROJECT_MODEL = "projectModel";
	public static final String LOG_FRAME = "logFrame";
	public static final String PHASES = "phases";
	public static final String VALUES = "values";
	public static final String CURRENT_PHASE = "currentPhase";
	public static final String FAVORITE_USERS = "favoriteUsers";
	public static final String MANAGER = "manager";
	public static final String POINTS_LIST = "pointsList";
	public static final String REMINDERS_LIST = "remindersList";
	public static final String AMENDMENTS = "amendments";
	public static final String OWNER = "owner";
	public static final String COUNTRY = "country";

	// Calculated DTO attributes (dynamically set).
	public static final String CURRENT_PHASE_NAME = "currentPhaseName";
	public static final String VISIBILITIES = "visibilities";
	public static final String ORG_UNIT_ID = "orgUnitId";
	public static final String ORG_UNIT_NAME = "orgUnitName";
	public static final String CHILDREN_PROJECTS = "childrenProjects";
	public static final String CATEGORY_ELEMENTS = "categoryElements";
	public static final String TYPE_ICON_HTML = "typeIconHtml";
	public static final String CURRENT_AMENDMENT = "currentAmendment";
	public static final String PLANNED_BUDGET = "plannedBudget";
	public static final String SPEND_BUDGET = "spendBudget";
	public static final String RECEIVED_BUDGET = "receivedBudget";
	public static final String RATIO_DIVIDEND_VALUE = "ratioDividendValue";
	public static final String RATIO_DIVIDEND_LABEL = "ratioDividendLabel";
	public static final String RATIO_DIVIDEND_TYPE = "ratioDividendType";
	public static final String RATIO_DIVISOR_VALUE = "ratioDivisorValue";
	public static final String RATIO_DIVISOR_LABEL = "ratioDivisorLabel";
	public static final String RATIO_DIVISOR_TYPE = "ratioDivisorType";
	
	// Keys used by project creation
	public static final String BUDGET = "budget";
	public static final String MODEL_ID = "modelId";
	public static final String CALENDAR_NAME = "calendarName";
	public static final String AMOUNT = "amount";
	public static final String CREATION_MODE = "creationMode";
	public static final String BASE_PROJECT = "baseProject";

	/**
	 * Mapping configurations.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static enum Mode implements IsMappingMode {

		/**
		 * Specific mapping mode using <b>only</b> {@link org.sigmah.server.handler.util.ProjectMapper ProjectMapper}.
		 * 
		 * @see org.sigmah.server.handler.util.ProjectMapper
		 */
		// TODO Try to merge this exception into generic system.
		_USE_PROJECT_MAPPER,

		/**
		 * Base mapping retrieving only project base data (no related DTO).
		 */
		BASE(
			new MappingField(OWNER),
			new MappingField(PROJECT_MODEL),
			new MappingField(LOG_FRAME),
			new MappingField(FUNDING),
			new MappingField(FUNDED),
			new MappingField(VALUES),
			new MappingField(PHASES),
			new MappingField(CURRENT_PHASE),
			new MappingField(FAVORITE_USERS),
			new MappingField(MANAGER),
			new MappingField(POINTS_LIST),
			new MappingField(REMINDERS_LIST),
			new MappingField(AMENDMENTS),
			new MappingField(COUNTRY)),

		/**
		 * In addition to base data, this mapping includes:
		 * <ul>
		 * <li>{@link ProjectDTO#OWNER}</li>
		 * </ul>
		 */
		WITH_USER(
			new MappingField(PROJECT_MODEL),
			new MappingField(LOG_FRAME),
			new MappingField(FUNDING),
			new MappingField(FUNDED),
			new MappingField(VALUES),
			new MappingField(PHASES),
			new MappingField(CURRENT_PHASE),
			new MappingField(FAVORITE_USERS),
			new MappingField(MANAGER),
			new MappingField(POINTS_LIST),
			new MappingField(REMINDERS_LIST),
			new MappingField(AMENDMENTS),
			new MappingField(COUNTRY)),

		/**
		 * In addition to base data, this mapping includes:
		 * <ul>
		 * <li>{@link ProjectDTO#FUNDING}</li>
		 * <li>{@link ProjectDTO#FUNDED}</li>
		 * </ul>
		 */
		WITH_RELATED_PROJECTS(
			new MappingField(OWNER), 
			new MappingField(PROJECT_MODEL), 
			new MappingField(LOG_FRAME), 
			new MappingField(VALUES), 
			new MappingField(PHASES), 
			new MappingField(CURRENT_PHASE), 
			new MappingField(FAVORITE_USERS), 
			new MappingField(MANAGER), 
			new MappingField(POINTS_LIST), 
			new MappingField(REMINDERS_LIST), 
			new MappingField(AMENDMENTS), 
			new MappingField(COUNTRY)),
		
		;

		private final CustomMappingField[] customFields;
		private final MappingField[] excludedFields;

		private Mode(final MappingField... excludedFields) {
			this(null, excludedFields);
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
	 * @author tmi
	 */
	public static final class LocalizedElement<E extends FlexibleElementDTO> extends ProjectModelDTO.LocalizedElement<E> {

		private final PhaseDTO phase;
		private final int projectId;

		private LocalizedElement(ProjectModelDTO.LocalizedElement<E> localized, PhaseDTO phase, int projectId) {
			super(localized.getPhaseModel(), localized.getElement());
			this.phase = phase;
			this.projectId = projectId;
		}

		/**
		 * Get the phase model in which the element is displayed, or <code>null</code> if the element is in the details
		 * page.
		 * 
		 * @return The phase model of the element or <code>null</code>.
		 */
		public PhaseDTO getPhase() {
			return phase;
		}

		public int getProjectId() {
			return projectId;
		}
	}

	private transient HashMap<PhaseModelDTO, PhaseDTO> mappedPhases;

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
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(NAME, getName());
		builder.append(FULL_NAME, getFullName());
		builder.append(COMPLETE_NAME, getCompleteName());
		builder.append(START_DATE, getStartDate());
		builder.append(END_DATE, getEndDate());
		builder.append(CLOSE_DATE, getCloseDate());
	}

	// Project name
	@Override
	public String getName() {
		return get(NAME);
	}

	public void setName(String name) {
		set(NAME, name);
		generateCompleteName();
	}

	// Project full name
	@Override
	public String getFullName() {
		return get(FULL_NAME);
	}

	public void setFullName(String fullName) {
		set(FULL_NAME, fullName);
		generateCompleteName();
	}

	// Complete name (updated dynamically with 'setName()' and 'setFullName()')
	public String getCompleteName() {
		return get(COMPLETE_NAME);
	}

	// Project start date
	@Override
	public Date getStartDate() {
		return get(START_DATE);
	}

	public void setStartDate(Date startDate) {
		set(START_DATE, startDate);
	}

	// Project end date
	@Override
	public Date getEndDate() {
		return get(END_DATE);
	}

	public void setEndDate(Date endDate) {
		set(END_DATE, endDate);
	}

	// Reference to the Project Model
	public ProjectModelDTO getProjectModel() {
		return get(PROJECT_MODEL);
	}

	public void setProjectModel(ProjectModelDTO projectModel) {
		set(PROJECT_MODEL, projectModel);
	}

	// Owner project name
	public UserDTO getOwner() {
		return get(OWNER);
	}

	public void setOwner(UserDTO owner) {
		set(OWNER, owner);
	}

	// Owner project name
	@Override
	public String getOwnerName() {
		return getOwner() != null ? getOwner().getName() : null;
	}

	// Owner project first name
	@Override
	public String getOwnerFirstName() {
		return getOwner() != null ? getOwner().getFirstName() : null;
	}

	// Owner project email
	public String getOwnerEmail() {
		return getOwner() != null ? getOwner().getEmail() : null;
	}

	// Reference to the project phases list
	public List<PhaseDTO> getPhases() {
		return get(PHASES);
	}

	public void setPhases(List<PhaseDTO> phases) {
		set(PHASES, phases);
	}

	// Reference to the project values list
	public List<ValueDTO> getValues() {
		return get(VALUES);
	}

	public void setValues(List<ValueDTO> values) {
		set(VALUES, values);
	}

	// Reference to the current phase.
	public PhaseDTO getCurrentPhase() {
		return get(CURRENT_PHASE);
	}

	public void setCurrentPhase(PhaseDTO currentPhase) {
		set(CURRENT_PHASE, currentPhase);
	}

	// Reference to the current phase name.
	public String getCurrentPhaseName() {
		return get(CURRENT_PHASE_NAME);
	}

	public void setCurrentPhaseName(String currentPhaseName) {
		set(CURRENT_PHASE_NAME, currentPhaseName);
	}

	// Calendar id.
	public Integer getCalendarId() {
		return (Integer) get(CALENDAR_ID);
	}

	public void setCalendarId(Integer calendarId) {
		set(CALENDAR_ID, calendarId);
	}

	// LogFrame.
	public LogFrameDTO getLogFrame() {
		return get(LOG_FRAME);
	}

	public void setLogFrame(LogFrameDTO logFrame) {
		set(LOG_FRAME, logFrame);
	}

	// Activities advancement
	public Integer getActivityAdvancement() {
		return get(ACTIVITY_ADVANCEMENT);
	}

	public void setActivityAdvancement(Integer activityAdvancement) {
		set(ACTIVITY_ADVANCEMENT, activityAdvancement);
	}

	@Override
	public Double getPlannedBudget() {
		final Double b = (Double) get(PLANNED_BUDGET);
		return b != null ? b : 0.0;
	}

	public void setPlannedBudget(Double plannedBudget) {
		set(PLANNED_BUDGET, plannedBudget);
	}

	@Override
	public Double getSpendBudget() {
		final Double b = (Double) get(SPEND_BUDGET);
		return b != null ? b : 0.0;
	}

	public void setSpendBudget(Double spendBudget) {
		set(SPEND_BUDGET, spendBudget);
	}

	@Override
	public Double getReceivedBudget() {
		final Double b = (Double) get(RECEIVED_BUDGET);
		return b != null ? b : 0.0;
	}

	public void setReceivedBudget(Double receivedBudget) {
		set(RECEIVED_BUDGET, receivedBudget);
	}

	public List<ProjectFundingDTO> getFunding() {
		return get(FUNDING);
	}

	public void setFunding(List<ProjectFundingDTO> funding) {
		set(FUNDING, funding);
	}

	public List<ProjectFundingDTO> getFunded() {
		return get(FUNDED);
	}

	public void setFunded(List<ProjectFundingDTO> funded) {
		set(FUNDED, funded);
	}

	@Override
	public org.sigmah.shared.dto.country.CountryDTO getCountry() {
		return get(COUNTRY);
	}

	public void setCountry(org.sigmah.shared.dto.country.CountryDTO country) {
		set(COUNTRY, country);
	}

	@Override
	public UserDTO getManager() {
		return get(MANAGER);
	}

	public void setManager(UserDTO manager) {
		set(MANAGER, manager);
	}

	public MonitoredPointListDTO getPointsList() {
		return get(POINTS_LIST);
	}

	public void setPointsList(MonitoredPointListDTO pointsList) {
		set(POINTS_LIST, pointsList);
	}

	public ReminderListDTO getRemindersList() {
		return get(REMINDERS_LIST);
	}

	public void setRemindersList(ReminderListDTO remindersList) {
		set(REMINDERS_LIST, remindersList);
	}

	public void setCloseDate(Date closeDate) {
		set(CLOSE_DATE, closeDate);
	}

	public Date getCloseDate() {
		return get(CLOSE_DATE);
	}

	public AmendmentState getAmendmentState() {
		return get(AMENDMENT_STATE);
	}

	public void setAmendmentState(AmendmentState amendmentState) {
		set(AMENDMENT_STATE, amendmentState);
	}

	public Integer getAmendmentVersion() {
		return get(AMENDMENT_VERSION);
	}

	public void setAmendmentVersion(Integer amendmentVersion) {
		set(AMENDMENT_VERSION, amendmentVersion);
	}

	public Integer getAmendmentRevision() {
		return get(AMENDMENT_REVISION);
	}

	public void setAmendmentRevision(Integer amendmentRevision) {
		set(AMENDMENT_REVISION, amendmentRevision);
	}

	public List<AmendmentDTO> getAmendments() {
		return get(AMENDMENTS);
	}

	public void setAmendments(List<AmendmentDTO> amendments) {
		set(AMENDMENTS, amendments);
	}

	public AmendmentDTO getCurrentAmendment() {
		return (AmendmentDTO) get(CURRENT_AMENDMENT);
	}

	public void setCurrentAmendment(AmendmentDTO currentAmendment) {
		set(CURRENT_AMENDMENT, currentAmendment);
	}

	// Org Unit id.
	@Override
	public Integer getOrgUnitId() {
		return (Integer) get(ORG_UNIT_ID);
	}

	public void setOrgUnitId(Integer orgUnit) {
		set(ORG_UNIT_ID, orgUnit);
	}

	// Org Unit name.
	public String getOrgUnitName() {
		return get(ORG_UNIT_NAME);
	}

	public void setOrgUnitName(String orgUnitName) {
		set(ORG_UNIT_NAME, orgUnitName);
	}

	// Users who choose this project for their favorite project
	public Set<UserDTO> getFavoriteUsers() {
		return get(FAVORITE_USERS);
	}

	public void setFavoriteUsers(Set<UserDTO> favoriteUsers) {
		set(FAVORITE_USERS, favoriteUsers);
	}

	// Project visibilities (XML mapping)
	public List<ProjectModelVisibilityDTO> getVisibilities() {
		return get(VISIBILITIES);
	}

	public void setVisibilities(List<ProjectModelVisibilityDTO> visibilities) {
		set(VISIBILITIES, visibilities);
	}

	// ---------------------------------------------------------------------------------------------
	//
	// MANUAL MAPPING METHODS.
	//
	// @see ProjectMapper
	//
	// ---------------------------------------------------------------------------------------------

	public Double getRatioDividendValue() {
		return get(RATIO_DIVIDEND_VALUE);
	}

	public void setRatioDividendValue(Double ratioDividendValue) {
		set(RATIO_DIVIDEND_VALUE, ratioDividendValue);
	}

	public String getRatioDividendLabel() {
		return get(RATIO_DIVIDEND_LABEL);
	}

	public void setRatioDividendLabel(String ratioDividendLabel) {
		set(RATIO_DIVIDEND_LABEL, ratioDividendLabel);
	}

	public BudgetSubFieldType getRatioDividendType() {
		return get(RATIO_DIVIDEND_TYPE);
	}

	public void setRatioDividendType(BudgetSubFieldType ratioDividendType) {
		set(RATIO_DIVIDEND_TYPE, ratioDividendType);
	}

	public Double getRatioDivisorValue() {
		return get(RATIO_DIVISOR_VALUE);
	}

	public void setRatioDivisorValue(Double ratioDivisorValue) {
		set(RATIO_DIVISOR_VALUE, ratioDivisorValue);
	}

	public String getRatioDivisorLabel() {
		return get(RATIO_DIVISOR_LABEL);
	}

	public void setRatioDivisorLabel(String ratioDivisorLabel) {
		set(RATIO_DIVISOR_LABEL, ratioDivisorLabel);
	}

	public BudgetSubFieldType getRatioDivisorType() {
		return get(RATIO_DIVISOR_TYPE);
	}

	public void setRatioDivisorType(BudgetSubFieldType ratioDivisorType) {
		set(RATIO_DIVISOR_TYPE, ratioDivisorType);
	}

	// Categories.
	public Set<CategoryElementDTO> getCategoryElements() {
		return get(CATEGORY_ELEMENTS);
	}

	public void setCategoryElements(Set<CategoryElementDTO> categoryElements) {
		set(CATEGORY_ELEMENTS, categoryElements);
	}

	// Children (projects funded by this project)
	public List<ProjectDTO> getChildrenProjects() {
		return get(CHILDREN_PROJECTS);
	}

	public void setChildrenProjects(List<ProjectDTO> childrenProjects) {

		// Base tree model.
		for (final ProjectDTO child : childrenProjects) {
			child.setParent(this);
		}
		setChildren(new ArrayList<ModelData>(childrenProjects));

		set(CHILDREN_PROJECTS, childrenProjects);
	}

	// Type icon html.
	public void setTypeIconHtml(String typeHtmlIcon) {
		set(TYPE_ICON_HTML, typeHtmlIcon);
	}

	public String getTypeIconHtml() {
		return get(TYPE_ICON_HTML);
	}

	// ---------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------

	private void generateCompleteName() {
		set(COMPLETE_NAME, getName() + " - " + getFullName());
	}

	/**
	 * Gets the type of this model for the given organization. If this model isn't visible for this organization,
	 * <code>null</code> is returned.
	 * 
	 * @param organizationId
	 *          The organization.
	 * @return The type of this model for the given organization, <code>null</code> otherwise.
	 */
	public ProjectModelType getVisibility(int organizationId) {

		final List<ProjectModelVisibilityDTO> visibilities;
		
		if (getVisibilities() != null) {
			visibilities = getVisibilities();
		} else if(getProjectModel() != null && getProjectModel().getVisibilities() != null) {
			visibilities = getProjectModel().getVisibilities();
		} else {
			return null;
		}

		for (final ProjectModelVisibilityDTO visibility : visibilities) {
			if (visibility.getOrganizationId().equals(organizationId)) {
				return visibility.getType();
			}
		}

		return null;
	}

	/**
	 * Gets the type of this model for the given organization. If this model isn't visible for this organization,
	 * <code>null</code> is returned.
	 * 
	 * @param organizationId
	 *          The organization id.
	 * @return The type of this model for the given organization, <code>null</code> otherwise.
	 */
	public ProjectModelType getProjectModelType(final int organizationId) {

		return getVisibility(organizationId);
	}

	public boolean isClosed() {
		return getCloseDate() != null;
	}

	public void addFunding(ProjectFundingDTO funding) {

		if (funding == null) {
			return;
		}

		List<ProjectFundingDTO> fundings = getFunding();

		if (fundings == null) {
			fundings = new ArrayList<ProjectFundingDTO>();
		}

		fundings.remove(funding);
		fundings.add(funding);

		setFunding(fundings);
	}

	public void addFunded(ProjectFundingDTO funded) {

		if (funded == null) {
			return;
		}

		List<ProjectFundingDTO> fundeds = getFunded();

		if (fundeds == null) {
			fundeds = new ArrayList<ProjectFundingDTO>();
		}

		fundeds.remove(funded);
		fundeds.add(funded);

		setFunded(fundeds);
	}

	/**
	 * Gets the following phases of the given phase.
	 * 
	 * @param phase
	 *          The phase.
	 * @return The following phases.
	 */
	public List<PhaseDTO> getSuccessors(final PhaseDTO phase) {

		if (phase == null || phase.getPhaseModel() == null) {
			return null;
		}

		final ArrayList<PhaseDTO> successors = new ArrayList<PhaseDTO>();

		// For each successor.
		for (final PhaseModelDTO successorModel : phase.getPhaseModel().getSuccessors()) {

			// Retrieves the equivalent phase in this project.
			for (final PhaseDTO p : getPhases()) {

				if (!p.getId().equals(phase.getId())) {
					if (successorModel.equals(p.getPhaseModel())) {
						successors.add(p);
					}
				}
			}
		}

		return successors;
	}

	/**
	 * Gets all the flexible elements instances of the given class in this project (phases and details page). The banner
	 * is ignored cause the elements in it are read-only.
	 * 
	 * @param clazz
	 *          The class of the searched flexible elements.
	 * @return The elements localized for the given class, or <code>null</code> if there is no element of this class.
	 */
	public <E extends FlexibleElementDTO> List<LocalizedElement<E>> getLocalizedElements(Class<E> clazz) {

		final ArrayList<LocalizedElement<E>> elements = new ArrayList<LocalizedElement<E>>();

		final List<ProjectModelDTO.LocalizedElement<E>> localizedElements = getProjectModel().getLocalizedElements(clazz);

		if (localizedElements != null) {
			for (final ProjectModelDTO.LocalizedElement<E> localized : localizedElements) {
				elements.add(new LocalizedElement<E>(localized, getPhaseFromModel(localized.getPhaseModel()), getId()));
			}
		}

		return elements;
	}

	/**
	 * Gets the phase which implements the given model for the current project.
	 * 
	 * @param model
	 *          The phase model.
	 * @return The corresponding phase.
	 */
	public PhaseDTO getPhaseFromModel(final PhaseModelDTO model) {

		if (mappedPhases == null) {
			mappedPhases = new HashMap<PhaseModelDTO, PhaseDTO>();
			for (final PhaseDTO phase : getPhases()) {
				mappedPhases.put(phase.getPhaseModel(), phase);
			}
		}

		return mappedPhases.get(model);
	}

	/**
	 * Gets the percentage of the elapsed time for the given project.
	 * 
	 * @return The percentage of the elapsed time.
	 */
	@SuppressWarnings("deprecation")
	public double getElapsedTime() {

		final double ratio;
		final Date start = getStartDate();
		final Date end = getEndDate();
		final Date close = getCloseDate();
		final Date today = new Date();
		final Date comparison;

		if (isClosed()) {
			comparison = new Date(close.getYear(), close.getMonth(), close.getDate());
		} else {
			comparison = new Date(today.getYear(), today.getMonth(), today.getDate());
		}

		// No end date
		if (end == null) {
			ratio = 0d;
		}
		// No start date but with a end date.
		else if (start == null) {

			if (DateUtils.DAY_COMPARATOR.compare(comparison, end) < 0) {
				ratio = 0d;
			} else {
				ratio = 100d;
			}
		}
		// Start date and end date.
		else {

			// The start date is after the end date → 100%.
			if (DateUtils.DAY_COMPARATOR.compare(start, end) >= 0) {
				ratio = 100d;
			}
			// The start date is after today → 0%.
			else if (DateUtils.DAY_COMPARATOR.compare(comparison, start) <= 0) {
				ratio = 0d;
			}
			// The start date is before the end date → x%.
			else {
				final Date sd = new Date(start.getYear(), start.getMonth(), start.getDate());
				final Date ed = new Date(end.getYear(), end.getMonth(), end.getDate());
				final double elapsedTime = comparison.getTime() - sd.getTime();
				final double estimatedTime = ed.getTime() - sd.getTime();
				ratio = NumberUtils.ratio(elapsedTime, estimatedTime);
			}
		}

		return NumberUtils.adjustRatio(ratio);
	}

	public void generateTypeIconHTML(Integer organizationId) {
		if (organizationId == null) {
			return;
		}
		final Image img = FundingIconProvider.getProjectTypeIcon(getProjectModelType(organizationId), IconSize.SMALL).createImage();
		setTypeIconHtml(img.getElement().getString());
	}

}
