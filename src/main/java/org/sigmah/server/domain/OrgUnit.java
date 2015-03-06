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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.SchemaElement;
import org.sigmah.shared.dto.referential.ContainerInformation;

/**
 * <p>
 * Organization Unit domain entity.
 * </p>
 * <p>
 * It corresponds to a headquarter or a center of the NGO related.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.ORG_UNIT_TABLE)
public class OrgUnit extends AbstractEntityId<Integer> implements SchemaElement {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -1465948408315226092L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.ORG_UNIT_COLUMN_ID, unique = true, nullable = false)
	private Integer id;

	@Column(name = EntityConstants.ORG_UNIT_COLUMN_NAME, nullable = false, length = EntityConstants.ORG_UNIT_NAME_MAX_LENGTH)
	@Size(max = EntityConstants.ORG_UNIT_NAME_MAX_LENGTH)
	private String name;

	@Column(name = EntityConstants.ORG_UNIT_COLUMN_FULL_NAME, length = EntityConstants.ORG_UNIT_FULL_NAME_MAX_LENGTH)
	@Size(max = EntityConstants.ORG_UNIT_FULL_NAME_MAX_LENGTH)
	private String fullName;

	@Column(name = EntityConstants.ORG_UNIT_COLUMN_CALENDAR_ID)
	private Integer calendarId;

	@Column(name = EntityConstants.ORG_UNIT_COLUMN_DELETED)
	@Temporal(TemporalType.TIMESTAMP)
	private Date deleted;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = EntityConstants.ORG_UNIT_USER_DATABASE_LINK_TABLE, joinColumns = { @JoinColumn(name = EntityConstants.ORG_UNIT_COLUMN_ID, nullable = false, updatable = false)
	}, inverseJoinColumns = { @JoinColumn(name = EntityConstants.USER_DATABASE_COLUMN_ID, nullable = false, updatable = false)
	})
	private Set<UserDatabase> databases = new HashSet<UserDatabase>(0);

	/**
	 * The point location of the OrgUnit, generally the city of its head office.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.ORG_UNIT_COLUMN_LOCATION)
	private Location location;

	/**
	 * The parent OrgUnit that manages this OrgUnit
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.ORG_UNIT_COLUMN_PARENT)
	// 'parent' is a reserved keyword in GXT framework (see BaseTreeModel).
	private OrgUnit parentOrgUnit;

	/**
	 * The children OrgUnits that are managed by this OrgUnit
	 */
	@OneToMany(mappedBy = "parentOrgUnit")
	@OrderBy("name ASC")
	// 'children' is a reserved keyword in GXT framework (see BaseTreeModel).
	private Set<OrgUnit> childrenOrgUnits = new HashSet<OrgUnit>(0);

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.ORG_UNIT_COLUMN_ORGANIZATION)
	private Organization organization;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.ORG_UNIT_COLUMN_ORG_UNIT_MODEL)
	private OrgUnitModel orgUnitModel;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.ORG_UNIT_COLUMN_COUNTRY, nullable = true)
	private Country officeLocationCountry;

	public OrgUnit() {
	}

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Returns a serializable object with basic information about this object.
	 * 
	 * @return Basic information about this orgunit as a ContainerInformation instance.
	 */
	public ContainerInformation toContainerInformation() {
		return new ContainerInformation(getId(), getName(), getFullName(), true);
	}
	
	/**
	 * <p>
	 * Overrides default behaviour to only display {@link #getName()} value.
	 * </p>
	 * 
	 * @see #getName()
	 */
	@Override
	public String toString() {
		return getName();
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
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullName() {
		return this.fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Set<UserDatabase> getDatabases() {
		return this.databases;
	}

	public void setDatabases(Set<UserDatabase> databases) {
		this.databases = databases;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public OrgUnit getParentOrgUnit() {
		return parentOrgUnit;
	}

	public void setParentOrgUnit(OrgUnit parent) {
		this.parentOrgUnit = parent;
	}

	public Set<OrgUnit> getChildrenOrgUnits() {
		return childrenOrgUnits;
	}

	public void setChildrenOrgUnits(Set<OrgUnit> children) {
		this.childrenOrgUnits = children;
	}

	public Integer getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(Integer calendarId) {
		this.calendarId = calendarId;
	}

	public Country getOfficeLocationCountry() {
		return officeLocationCountry;
	}

	public void setOfficeLocationCountry(Country officeLocationCountry) {
		this.officeLocationCountry = officeLocationCountry;
	}

	public void setDeleted(Date deleted) {
		this.deleted = deleted;
	}

	public Date getDeleted() {
		return deleted;
	}

	public OrgUnitModel getOrgUnitModel() {
		return orgUnitModel;
	}

	public void setOrgUnitModel(OrgUnitModel orgUnitModel) {
		this.orgUnitModel = orgUnitModel;
	}

	// Transient.
	public boolean isCanContainProjects() {
		return getOrgUnitModel() != null && getOrgUnitModel().getCanContainProjects() != null && getOrgUnitModel().getCanContainProjects();
	}

	// Transient.
	public void setCanContainProjects(boolean canContainProjects) {
	}

}
