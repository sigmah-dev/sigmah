package org.sigmah.server.domain;

import java.util.Date;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.Deleteable;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.EntityFilters;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

/**
 * <p>
 * Org Unit Model domain entity.
 * </p>
 * <p>
 * Defines the model for an {@code OrgUnit}.
 * </p>
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.ORG_UNIT_MODEL_TABLE)
@FilterDefs({ @FilterDef(name = EntityFilters.HIDE_DELETED)
})
@Filters({ @Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.ORG_UNIT_HIDE_DELETED_CONDITION)
})
public class OrgUnitModel extends AbstractEntityId<Integer> implements Deleteable, HasMaintenance {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -722132644240828016L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.ORG_UNIT_MODEL_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.ORG_UNIT_MODEL_COLUMN_NAME, nullable = false, length = EntityConstants.ORG_UNIT_MODEL_NAME_MAX_LENGTH)
	@NotNull
	@Size(max = EntityConstants.ORG_UNIT_MODEL_NAME_MAX_LENGTH)
	private String name;

	@Column(name = EntityConstants.ORG_UNIT_MODEL_COLUMN_HAS_BUDGET)
	private Boolean hasBudget = false;

	@Column(name = EntityConstants.ORG_UNIT_MODEL_COLUMN_TITLE, nullable = false, length = EntityConstants.ORG_UNIT_MODEL_TITLE_MAX_LENGTH)
	@NotNull
	@Size(max = EntityConstants.ORG_UNIT_MODEL_TITLE_MAX_LENGTH)
	private String title;

	@Column(name = EntityConstants.ORG_UNIT_MODEL_COLUMN_CAN_CONTAIN_PROJECTS, nullable = false)
	@NotNull
	private Boolean canContainProjects = true;

	/**
	 * The date on which this project model was deleted by the user, or null if this project model is not deleted.
	 */
	@Column(name = EntityConstants.ORG_UNIT_MODEL_COLUMN_DATE_DELETED)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date dateDeleted;

	@Column(name = EntityConstants.ORG_UNIT_MODEL_COLUMN_STATUS, nullable = false)
	@Enumerated(EnumType.STRING)
	@NotNull
	private ProjectModelStatus status = ProjectModelStatus.DRAFT;
	
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

	@OneToOne(mappedBy = "orgUnitModel", cascade = CascadeType.ALL)
	private OrgUnitBanner banner;

	@OneToOne(mappedBy = "orgUnitModel", cascade = CascadeType.ALL)
	private OrgUnitDetails details;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.ORGANIZATION_COLUMN_ID)
	private Organization organization;

	public OrgUnitModel() {
	}

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

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
	@Transient
	public boolean isDeleted() {
		return getDateDeleted() != null;
	}

	/**
	 * Reset the identifiers of the object.
	 */
	public void resetImport() {
		resetImport(false);
	}
	
	/**
	 * Reset the identifiers of the object.
	 * 
	 * @param keepPrivacyGroups <code>true</code> to not reset the privacy groups.
	 */
	public void resetImport(boolean keepPrivacyGroups) {
		this.id = null;
		if (this.banner != null) {
			this.banner.resetImport(this, keepPrivacyGroups);
		}
		if (this.details != null) {
			this.details.resetImport(this, keepPrivacyGroups);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToString(final ToStringBuilder builder) {
		builder.append("name", name);
		builder.append("hasBudget", hasBudget);
		builder.append("title", title);
		builder.append("canContainProjects", canContainProjects);
		builder.append("dateDeleted", dateDeleted);
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public Integer getId() {
		return this.id;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public OrgUnitBanner getBanner() {
		return banner;
	}

	public void setBanner(OrgUnitBanner banner) {
		this.banner = banner;
	}

	public OrgUnitDetails getDetails() {
		return details;
	}

	public void setDetails(OrgUnitDetails details) {
		this.details = details;
	}

	public Boolean getHasBudget() {
		return hasBudget;
	}

	public void setHasBudget(Boolean hasBudget) {
		this.hasBudget = hasBudget;
	}

	public Boolean getCanContainProjects() {
		return canContainProjects;
	}

	public void setCanContainProjects(Boolean canContainProjects) {
		this.canContainProjects = canContainProjects;
	}

	@Override
	public ProjectModelStatus getStatus() {
		return status;
	}

	@Override
	public void setStatus(ProjectModelStatus status) {
		this.status = status;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
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
	
}
