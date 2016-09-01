package org.sigmah.server.domain;

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
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.layout.Layout;
import org.sigmah.server.domain.layout.LayoutConstraint;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.domain.logframe.LogFrameModel;
import org.sigmah.server.domain.profile.Profile;
import org.sigmah.server.domain.util.Deleteable;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.EntityFilters;
import org.sigmah.shared.dto.referential.ProjectModelStatus;
import org.sigmah.shared.dto.referential.ProjectModelType;

/**
 * <p>
 * Project model domain entity.
 * </p>
 * <p>
 * Note: entity corresponding client-side DTO should inherits {@code com.extjs.gxt.ui.client.data.BaseModelData}.
 * </p>
 *
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.PROJECT_MODEL_TABLE)
@FilterDefs({ @FilterDef(name = EntityFilters.HIDE_DELETED)
})
@Filters({ @Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.PROJECT_MODEL_HIDE_DELETED_CONDITION)
})
public class ProjectModel extends AbstractEntityId<Integer> implements Deleteable, HasMaintenance {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -1266259112071917788L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.PROJECT_MODEL_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.PROJECT_MODEL_COLUMN_NAME, nullable = false, length = EntityConstants.PROJECT_MODEL_NAME_MAX_LENGTH)
	@NotNull
	@Size(max = EntityConstants.PROJECT_MODEL_NAME_MAX_LENGTH)
	private String name;

	/**
	 * The date on which this project model was deleted by the user, or null if this project model is not deleted.
	 */
	@Column(name = EntityConstants.PROJECT_MODEL_COLUMN_DATE_DELETED)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date dateDeleted;

	@Column(name = EntityConstants.PROJECT_MODEL_COLUMN_STATUS, nullable = false)
	@Enumerated(EnumType.STRING)
	@NotNull
	private ProjectModelStatus status;

	/**
     * The date on which this project model maintenance started or is going to start.
     */
    @Column(name = EntityConstants.PROJECT_MODEL_COLUMN_DATE_MAINTENANCE, nullable = true)
    @Temporal(value = TemporalType.TIMESTAMP)
	private Date dateMaintenance;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	// Trick: using '@ManyToOne' to avoid automatic load of the object (see '@OneToOne' lazy issue).
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.PROJECT_MODEL_COLUMN_PHASE_MODEL_ID, nullable = true)
	private PhaseModel rootPhaseModel;

	@OneToOne(mappedBy = "projectModel", cascade = CascadeType.ALL)
	private ProjectBanner projectBanner;

	@OneToOne(mappedBy = "projectModel", cascade = CascadeType.ALL)
	private ProjectDetails projectDetails;

	@OneToOne(mappedBy = "projectModel", cascade = CascadeType.ALL)
	private LogFrameModel logFrameModel;

	@OneToMany(mappedBy = "parentProjectModel", cascade = CascadeType.ALL)
    @OrderBy("displayOrder ASC")
	private List<PhaseModel> phaseModels = new ArrayList<>();

	@OneToMany(mappedBy = "model", cascade = CascadeType.ALL)
	private List<ProjectModelVisibility> visibilities;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = EntityConstants.PROJECT_MODEL_COLUMN_DEFAULT_TEAM_MEMBER_PROFILES_LINK_TABLE,
			joinColumns = @JoinColumn(name = EntityConstants.PROJECT_MODEL_COLUMN_ID, referencedColumnName = EntityConstants.PROJECT_MODEL_COLUMN_ID),
			inverseJoinColumns = @JoinColumn(name = EntityConstants.PROFILE_COLUMN_ID, referencedColumnName = EntityConstants.PROFILE_COLUMN_ID),
			uniqueConstraints = @UniqueConstraint(columnNames = {
					EntityConstants.PROJECT_MODEL_COLUMN_ID,
					EntityConstants.PROFILE_COLUMN_ID
			})
	)
	private List<Profile> defaultTeamMemberProfiles = new ArrayList<>();

	@OneToMany(mappedBy = "projectModel", cascade = CascadeType.ALL)
	private List<FrameworkFulfillment> frameworkFulfillments = new ArrayList<>();

	public ProjectModel() {
	}

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Adds the given {@code phaseModel} to the current project model.
	 *
	 * @param phaseModel
	 *          The phase model. Does nothing if {@code null}.
	 */
	public void addPhase(final PhaseModel phaseModel) {
		if (phaseModel != null && phaseModels != null && !phaseModels.contains(phaseModel)) {
			phaseModel.setParentProjectModel(this);
			phaseModels.add(phaseModel);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete() {
		setDateDeleted(new Date());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDeleted() {
		return getDateDeleted() != null;
	}

	/**
	 * Gets the type of this model for the given organization. If this model isn't visible for this organization,
	 * <code>null</code> is returned.
	 *
	 * @param organization
	 *          The organization.
	 * @return The type of this model for the given organization, <code>null</code> otherwise.
	 */
	public ProjectModelType getVisibility(final Organization organization) {

		if (organization == null || visibilities == null) {
			return null;
		}

		for (final ProjectModelVisibility visibility : visibilities) {
			if (visibility.getOrganization().getId().equals(organization.getId())) {
				return visibility.getType();
			}
		}
		return null;
	}

	/**
	 * Reset the following identifiers of the object:
	 * <ul>
	 * <li>{@code rootPhaseModel}</li>
	 * <li>{@code phaseModels}</li>
	 * <li>{@code projectBanner}</li>
	 * <li>{@code projectDetails}</li>
	 * <li>{@code logFrameModel}</li>
	 * </ul>
	 */
	public void resetImport() {
		resetImport(false);
	}

	/**
	 * Reset the following identifiers of the object:
	 * <ul>
	 * <li>{@code rootPhaseModel}</li>
	 * <li>{@code phaseModels}</li>
	 * <li>{@code projectBanner}</li>
	 * <li>{@code projectDetails}</li>
	 * <li>{@code logFrameModel}</li>
	 * </ul>
	 *
	 * @param keepPrivacyGroups <code>true</code> to not reset privacy groups.
	 */
	public void resetImport(boolean keepPrivacyGroups) {

		this.id = null;

		if (rootPhaseModel != null) {
			rootPhaseModel.resetImport(this, keepPrivacyGroups);
		}

		if (phaseModels != null) {
			for (final PhaseModel phase : phaseModels) {
				phase.resetImport(null, keepPrivacyGroups);
			}
		}

		if (projectBanner != null) {
			projectBanner.resetImport(this, keepPrivacyGroups);
		}

		if (projectDetails != null) {
			projectDetails.resetImport(this, keepPrivacyGroups);
		}

		if (logFrameModel != null) {
			logFrameModel.resetImport();
		}
	}
	
	/**
	 * Returns the first flexible element matching the given type.
	 * 
	 * @param <E>
	 *			Type of the flexible element to search.
	 * @param elementType
	 *			Class of the flexible element to search.
	 * @return The first flexible element matching the given type or <code>null</code> if none was found.
	 */
	public <E extends FlexibleElement> E getFirstElementOfType(final Class<E> elementType) {
		for (final Layout layout : getAllLayouts()) {
			for (final LayoutGroup group : layout.getGroups()) {
				for (final LayoutConstraint constraint : group.getConstraints()) {
					final FlexibleElement flexibleElement = constraint.getElement();
					if (flexibleElement != null && elementType.isAssignableFrom(flexibleElement.getClass())) {
						return elementType.cast(flexibleElement);
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns a new collection of every layout in this model.
	 * 
	 * @return A new collection of every layout in this model.
	 */
	public Collection<Layout> getAllLayouts() {
		final ArrayList<Layout> layouts = new ArrayList<>();
		
		if (projectBanner != null && projectBanner.getLayout() != null) {
			layouts.add(projectBanner.getLayout());
		}
		
		if (projectDetails != null && projectDetails.getLayout() != null) {
			layouts.add(projectDetails.getLayout());
		}
		
		if (phaseModels != null) {
			for (final PhaseModel phase : phaseModels) {
				if (phase != null && phase.getLayout() != null) {
					layouts.add(phase.getLayout());
				}
			}
		}
		
		return layouts;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("name", name);
		builder.append("status", status);
		builder.append("dateDeleted", dateDeleted);
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PhaseModel getRootPhaseModel() {
		return rootPhaseModel;
	}

	public void setRootPhaseModel(PhaseModel rootPhaseModel) {
		this.rootPhaseModel = rootPhaseModel;
	}

	public List<PhaseModel> getPhaseModels() {
		return phaseModels;
	}

	public void setPhaseModels(List<PhaseModel> phaseModels) {
		this.phaseModels = phaseModels;
	}

	public ProjectBanner getProjectBanner() {
		return projectBanner;
	}

	public void setProjectBanner(ProjectBanner projectBanner) {
		this.projectBanner = projectBanner;
	}

	public ProjectDetails getProjectDetails() {
		return projectDetails;
	}

	public void setProjectDetails(ProjectDetails projectDetails) {
		this.projectDetails = projectDetails;
	}

	public List<ProjectModelVisibility> getVisibilities() {
		return visibilities;
	}

	public void setVisibilities(List<ProjectModelVisibility> visibilities) {
		this.visibilities = visibilities;
	}

	public LogFrameModel getLogFrameModel() {
		return logFrameModel;
	}

	public void setLogFrameModel(LogFrameModel logFrameModel) {
		this.logFrameModel = logFrameModel;
	}

	@Override
	public void setStatus(ProjectModelStatus status) {
		this.status = status;
	}

	@Override
	public ProjectModelStatus getStatus() {
		return status;
	}

	public Date getDateDeleted() {
		return this.dateDeleted;
	}

	protected void setDateDeleted(Date date) {
		this.dateDeleted = date;
	}

	@Override
    public Date getDateMaintenance() {
		return dateMaintenance;
	}

	@Override
    public void setDateMaintenance(Date dateMaintenance) {
		this.dateMaintenance = dateMaintenance;
	}

	public boolean isUnderMaintenance() {
		return dateMaintenance != null && dateMaintenance.before(new Date());
	}

	public List<Profile> getDefaultTeamMemberProfiles() {
		return defaultTeamMemberProfiles;
	}

	public void setDefaultTeamMemberProfiles(List<Profile> defaultTeamMemberProfiles) {
		this.defaultTeamMemberProfiles = defaultTeamMemberProfiles;
	}

	public List<FrameworkFulfillment> getFrameworkFulfillments() {
		return frameworkFulfillments;
	}

	public void setFrameworkFulfillments(List<FrameworkFulfillment> frameworkFulfillments) {
		this.frameworkFulfillments = frameworkFulfillments;
	}
}
