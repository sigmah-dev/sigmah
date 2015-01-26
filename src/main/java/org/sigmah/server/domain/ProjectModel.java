package org.sigmah.server.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.logframe.LogFrameModel;
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
public class ProjectModel extends AbstractEntityId<Integer> implements Deleteable {

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
	private List<PhaseModel> phaseModels = new ArrayList<PhaseModel>();

	@OneToMany(mappedBy = "model", cascade = CascadeType.ALL)
	private List<ProjectModelVisibility> visibilities;

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

		this.id = null;

		if (rootPhaseModel != null) {
			rootPhaseModel.resetImport(this);
		}

		if (phaseModels != null) {
			for (final PhaseModel phase : phaseModels) {
				phase.resetImport(null);
			}
		}

		if (projectBanner != null) {
			projectBanner.resetImport(this);
		}

		if (projectDetails != null) {
			projectDetails.resetImport(this);
		}

		if (logFrameModel != null) {
			logFrameModel.resetImport();
		}
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

	public void setStatus(ProjectModelStatus status) {
		this.status = status;
	}

	public ProjectModelStatus getStatus() {
		return status;
	}

	public Date getDateDeleted() {
		return this.dateDeleted;
	}

	protected void setDateDeleted(Date date) {
		this.dateDeleted = date;
	}

}
