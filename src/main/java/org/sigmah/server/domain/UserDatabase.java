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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.Deleteable;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.EntityFilters;
import org.sigmah.server.domain.util.SchemaElement;

/**
 * <p>
 * User database domain entity.
 * </p>
 * <p>
 * The UserDatabase is the broadest unit of organization within ActivityInfo. Individual databases each has an owner who
 * controls completely the activities, indicators, partner organizations and the rights of other users to view, edit,
 * and design the database.
 * </p>
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.USER_DATABASE_TABLE)
@Inheritance(strategy = InheritanceType.JOINED)
@FilterDefs({
							@FilterDef(name = EntityFilters.USER_VISIBLE, parameters = { @ParamDef(name = EntityFilters.CURRENT_USER_ID, type = "int")
							}),
							@FilterDef(name = EntityFilters.HIDE_DELETED)
})
// TODO Add filtering on organisational level permissions.
@Filters({
					@Filter(name = EntityFilters.USER_VISIBLE, condition = EntityFilters.USER_DATABASE_USER_VISIBLE_CONDITION),
					@Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.USER_DATABASE_HIDE_DELETED_CONDITION)
})
public class UserDatabase extends AbstractEntityId<Integer> implements Deleteable, SchemaElement {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 7405094318163898712L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.USER_DATABASE_COLUMN_ID, unique = true, nullable = false)
	private Integer id;

	/**
	 * The date on which the activities defined by this database started. I.e. provides a minimum bound for the dates of
	 * activities.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = EntityConstants.USER_DATABASE_COLUMN_START_DATE)
	private Date startDate;

	/**
	 * The full name of the database.
	 */
	@Column(name = EntityConstants.USER_DATABASE_COLUMN_FULL_NAME, length = EntityConstants.USER_DATABASE_FULL_NAME_MAX_LENGTH)
	@Size(max = EntityConstants.USER_DATABASE_FULL_NAME_MAX_LENGTH)
	private String fullName;

	/**
	 * The short name of the database (generally an acronym).
	 */
	@Column(name = EntityConstants.USER_DATABASE_COLUMN_NAME, length = EntityConstants.NAME_MAX_LENGTH, nullable = false)
	@NotNull
	@Size(max = EntityConstants.NAME_MAX_LENGTH)
	private String name;

	/**
	 * The date on which this database was deleted by the user, or null if this database is not deleted.
	 */
	@Column(name = EntityConstants.COLUMN_DATE_DELETED)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date dateDeleted;

	/**
	 * The timestamp on which structure of the database (activities, indicators, etc) was last modified.
	 */
	@Column(name = EntityConstants.USER_DATABASE_COLUMN_LAST_SCHEMA_UPDATE, nullable = false)
	@Temporal(value = TemporalType.TIMESTAMP)
	@NotNull
	private Date lastSchemaUpdate;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * At present, each database can contain data on activities that take place in one and only one country.
	 */
	// TODO [ENTITY] nullable? many-to-many?
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.COUNTRY_COLUMN_ID, nullable = false)
	@NotNull
	private Country country;

	/**
	 * The user who owns this database.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.USER_DATABASE_COLUMN_OWNER_USER_ID, nullable = false)
	@NotNull
	private User owner;

	/**
	 * The list of partner organizations involved in this database. (Partner organizations can own activity sites).
	 */
	// TODO [ENTITY] Transform into a link to Office entity.
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = EntityConstants.ORG_UNIT_USER_DATABASE_LINK_TABLE, joinColumns = { @JoinColumn(name = EntityConstants.USER_DATABASE_COLUMN_ID, nullable = false, updatable = false)
	}, inverseJoinColumns = { @JoinColumn(name = EntityConstants.ORG_UNIT_COLUMN_ID, nullable = false, updatable = false)
	})
	private Set<OrgUnit> partners = new HashSet<OrgUnit>(0);

	/**
	 * The list of activities followed by this database.
	 */
	@OneToMany(mappedBy = "database", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@org.hibernate.annotations.OrderBy(clause = EntityConstants.COLUMN_SORT_ORDER)
	@org.hibernate.annotations.Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.USER_DATABASE_HIDE_DELETED_CONDITION)
	private Set<Activity> activities = new HashSet<Activity>(0);

	/**
	 * The list of users who have access to this database and their respective permissions. (Read, write, read all
	 * partners).
	 */
	@OneToMany(mappedBy = "database", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<UserPermission> userPermissions = new HashSet<UserPermission>(0);

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public UserDatabase() {
		// Default empty constructor.
	}

	public UserDatabase(final Integer id, final String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * @param user
	 * @return True if the given user has the right to view this database at all.
	 */
	public boolean isAllowedView(final User user) {
		if (getOwner().getId().equals(user.getId()) || getOwner().equals(user)) {
			return true;
		}

		final UserPermission permission = this.getPermissionByUser(user);
		return permission != null && permission.isAllowView();
	}

	/**
	 * @param user
	 * @return True if the given user has the right to view data from all partners in this database. False if they have
	 *         only the right to view the data from their partner organization
	 */
	public boolean isAllowedViewAll(final User user) {
		if (getOwner().getId().equals(user.getId()) || getOwner().equals(user)) {
			return true;
		}

		final UserPermission permission = this.getPermissionByUser(user);
		return permission != null && permission.isAllowViewAll();
	}

	/**
	 * @param user
	 * @return True if the given user has the right to create or modify sites on behalf of their (partner) organization
	 */
	public boolean isAllowedEdit(final User user) {
		if (getOwner().getId().equals(user.getId())) {
			return true;
		}

		final UserPermission permission = this.getPermissionByUser(user);
		return permission != null && permission.isAllowEdit();
	}

	/**
	 * @param user
	 * @return True if the given user has the right to modify the definition of the database, such as adding or removing
	 *         activities, indicators, etc
	 */
	public boolean isAllowedDesign(final User user) {
		if (getOwner().getId().equals(user.getId())) {
			return true;
		}

		final UserPermission permission = this.getPermissionByUser(user);
		return permission != null && permission.isAllowDesign();
	}

	@SuppressWarnings("deprecation")
	public boolean isAllowedManageUsers(final User user, final OrgUnit partner) {
		if (getOwner().getId().equals(user.getId())) {
			return true;
		}

		UserPermission permission = this.getPermissionByUser(user);
		if (permission == null) {
			return false;
		}
		if (!permission.isAllowManageUsers()) {
			return false;
		}
		if (!permission.isAllowManageAllUsers() && !permission.getPartner().getId().equals(partner.getId())) {
			return false;
		}

		return true;
	}

	/**
	 * @param user
	 * @return The permission descriptor for the given user, or null if this user has no rights to this database.
	 */
	public UserPermission getPermissionByUser(final User user) {
		for (final UserPermission perm : this.getUserPermissions()) {
			if (perm.getUser().getId().equals(user.getId()) || perm.getUser().equals(user)) {
				return perm;
			}
		}
		return null;
	}

	/**
	 * @param user
	 * @return True if the given user has the right to create and modify sites on behalf of all partner organizations.
	 */
	public boolean isAllowedEditAll(final User user) {
		if (getOwner().getId().equals(user.getId())) {
			return true;
		}

		final UserPermission permission = this.getPermissionByUser(user);
		return permission != null && permission.isAllowEditAll();
	}

	/**
	 * Marks this database as deleted. (Though the row is not removed from the database)
	 */
	@Override
	public void delete() {
		final Date now = new Date();
		setDateDeleted(now);
		setLastSchemaUpdate(now);
	}

	/**
	 * @return True if this database was deleted by its owner.
	 */
	@Override
	@Transient
	public boolean isDeleted() {
		return getDateDeleted() != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("startDate", startDate);
		builder.append("name", name);
		builder.append("fullName", fullName);
		builder.append("dateDeleted", dateDeleted);
		builder.append("lastSchemaUpdate", lastSchemaUpdate);
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

	public Country getCountry() {
		return this.country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getFullName() {
		return this.fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getOwner() {
		return this.owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Set<OrgUnit> getPartners() {
		return this.partners;
	}

	public void setPartners(Set<OrgUnit> partners) {
		this.partners = partners;
	}

	public Set<Activity> getActivities() {
		return this.activities;
	}

	public void setActivities(Set<Activity> activities) {
		this.activities = activities;
	}

	public Set<UserPermission> getUserPermissions() {
		return this.userPermissions;
	}

	public void setUserPermissions(Set<UserPermission> userPermissions) {
		this.userPermissions = userPermissions;
	}

	public Date getDateDeleted() {
		return this.dateDeleted;
	}

	protected void setDateDeleted(Date date) {
		this.dateDeleted = date;
	}

	public Date getLastSchemaUpdate() {
		return lastSchemaUpdate;
	}

	public void setLastSchemaUpdate(Date lastSchemaUpdate) {
		this.lastSchemaUpdate = lastSchemaUpdate;
	}

}
