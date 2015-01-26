package org.sigmah.server.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.Deleteable;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.EntityFilters;
import org.sigmah.server.domain.util.Orderable;
import org.sigmah.server.domain.util.SchemaElement;

/**
 * <p>
 * Activy domain entity.
 * </p>
 * <p>
 * After the {@link org.sigmah.server.domain.UserDatabase}, the activity is the second level of organization in
 * ActivityInfo. Each activity has its set of indicators and attributes.
 * </p>
 * <p>
 * Realized activities takes place at {@link org.sigmah.server.domain.Site} sites.
 * </p>
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.ACTIVITY_TABLE)
@org.hibernate.annotations.Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.ACTIVITY_HIDE_DELETED_CONDITION)
public class Activity extends AbstractEntityId<Integer> implements Deleteable, Orderable, SchemaElement {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 4795179200348055951L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.ACTIVITY_COLUMN_ID, unique = true, nullable = false)
	private Integer id;

	@Column(name = EntityConstants.ACTIVITY_COLUMN_NAME, nullable = false, length = EntityConstants.ACTIVITY_NAME_MAX_LENGTH)
	@NotNull
	@Size(max = EntityConstants.ACTIVITY_NAME_MAX_LENGTH)
	private String name;

	@Column(name = EntityConstants.ACTIVITY_COLUMN_CATEGORY, length = EntityConstants.ACTIVITY_CATEGORY_MAX_LENGTH)
	@Size(max = EntityConstants.ACTIVITY_CATEGORY_MAX_LENGTH)
	private String category;

	@Column(name = EntityConstants.ACTIVITY_COLUMN_REPORTING_FREQUENCY, nullable = false)
	@NotNull
	private int reportingFrequency;

	@Column(name = EntityConstants.ACTIVITY_COLUMN_ASSESSMENT, nullable = false)
	@NotNull
	private boolean assessment;

	@Column(name = EntityConstants.ACTIVITY_COLUMN_ALLOW_EDIT, nullable = false)
	@NotNull
	private boolean allowEdit;

	@Column(name = EntityConstants.COLUMN_SORT_ORDER, nullable = false)
	@NotNull
	private int sortOrder;

	@Column(name = EntityConstants.COLUMN_DATE_DELETED)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date dateDeleted;

	@Column(name = EntityConstants.ACTIVITY_COLUMN_MAP_ICON, length = EntityConstants.ACTIVITY_MAP_ICON_MAX_LENGTH, nullable = true)
	@Size(max = EntityConstants.ACTIVITY_MAP_ICON_MAX_LENGTH)
	private String mapIcon;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.LOCATION_TYPE_COLUMN_ID, nullable = false)
	@NotNull
	private LocationType locationType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.USER_DATABASE_COLUMN_ID, nullable = false)
	@NotNull
	private UserDatabase database;

	@OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@org.hibernate.annotations.OrderBy(clause = EntityConstants.COLUMN_SORT_ORDER)
	@org.hibernate.annotations.Filter(name = "hideDeleted", condition = "DateDeleted is null")
	private Set<Indicator> indicators = new HashSet<Indicator>(0);

	@OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<Site> sites = new HashSet<Site>(0);

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = EntityConstants.ATTRIBUTE_GROUP_ACTIVITY_LINK_TABLE, joinColumns = { @JoinColumn(name = EntityConstants.ACTIVITY_COLUMN_ID, nullable = false, updatable = false)
	}, inverseJoinColumns = { @JoinColumn(name = EntityConstants.ATTRIBUTE_GROUP_COLUMN_ID, nullable = false, updatable = false)
	})
	@org.hibernate.annotations.Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
	@org.hibernate.annotations.Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.ACTIVITY_HIDE_DELETED_CONDITION)
	private Set<AttributeGroup> attributeGroups = new HashSet<AttributeGroup>(0);

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public Activity() {
		// Default empty constructor.
	}

	public Activity(final Integer id, final String name) {
		this.id = id;
		this.name = name;
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
	@Transient
	public boolean isDeleted() {
		return getDateDeleted() == null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToString(final ToStringBuilder builder) {
		builder.append("name", name);
		builder.append("reportingFrequency", reportingFrequency);
		builder.append("assessment", assessment);
		builder.append("allowEdit", allowEdit);
		builder.append("sortOrder", sortOrder);
		builder.append("dateDeleted", dateDeleted);
		builder.append("mapIcon", mapIcon);
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

	public LocationType getLocationType() {
		return this.locationType;
	}

	public void setLocationType(LocationType locationType) {
		this.locationType = locationType;
	}

	public UserDatabase getDatabase() {
		return this.database;
	}

	public void setDatabase(UserDatabase database) {
		this.database = database;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getReportingFrequency() {
		return this.reportingFrequency;
	}

	public void setReportingFrequency(int reportingFrequency) {
		this.reportingFrequency = reportingFrequency;
	}

	public boolean isAssessment() {
		return this.assessment;
	}

	public void setAssessment(boolean assessment) {
		this.assessment = assessment;
	}

	public boolean isAllowEdit() {
		return this.allowEdit;
	}

	public void setAllowEdit(boolean allowEdit) {
		this.allowEdit = allowEdit;
	}

	@Override
	public int getSortOrder() {
		return this.sortOrder;
	}

	@Override
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Set<AttributeGroup> getAttributeGroups() {
		return this.attributeGroups;
	}

	public void setAttributeGroups(Set<AttributeGroup> attributeGroups) {
		this.attributeGroups = attributeGroups;
	}

	public Set<Indicator> getIndicators() {
		return this.indicators;
	}

	public void setIndicators(Set<Indicator> indicators) {
		this.indicators = indicators;
	}

	public Set<Site> getSites() {
		return this.sites;
	}

	public void setSites(Set<Site> sites) {
		this.sites = sites;
	}

	public Date getDateDeleted() {
		return this.dateDeleted;
	}

	public void setDateDeleted(Date date) {
		this.dateDeleted = date;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getMapIcon() {
		return mapIcon;
	}

	public void setMapIcon(String mapIcon) {
		this.mapIcon = mapIcon;
	}
}
