package org.sigmah.server.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.EntityFilters;
import org.sigmah.server.domain.util.SchemaElement;

/**
 * <p>
 * User permission domain entity.
 * </p>
 * <p>
 * Defines a given user's access to a given database.
 * </p>
 * <p>
 * Note: Owners of databases do not have UserPermission records. (Is this a good idea?) Each <code>User</code> belongs
 * to one and only one <code>Partner</code>, and permissions are split between the data that belongs to their partner (
 * <code>View, Edit</code>) and data that belongs to other partners (<code>ViewAll, EditAll</code>).
 * </p>
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.USER_PERMISSION_TABLE)
@Filters({
					@Filter(name = EntityFilters.USER_VISIBLE, condition = EntityFilters.USER_PERMISSION_USER_VISIBLE_CONDITION),
					@Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.USER_PERMISSION_HIDE_DELETED_CONDITION)
})
public class UserPermission extends AbstractEntityId<Integer> implements SchemaElement {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -4816666797315483563L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.USER_PERMISSION_COLUMN_ID, unique = true, nullable = false)
	private Integer id;

	/**
	 * {@code true} if the user has permission to view their own partner's data in the <code>UserDatabase</code>.
	 */
	@Column(name = EntityConstants.USER_PERMISSION_COLUMN_ALLOW_VIEW, nullable = false)
	@NotNull
	private boolean allowView;

	/**
	 * {@code true} if the user is allowed to view the data of other partners in the database.
	 */
	@Column(name = EntityConstants.USER_PERMISSION_COLUMN_ALLOW_VIEW_ALL, nullable = false)
	@NotNull
	private boolean allowViewAll;

	/**
	 * {@code true} if the user is allowed to create/edit data for their own partner.
	 */
	@Column(name = EntityConstants.USER_PERMISSION_COLUMN_ALLOW_EDIT, nullable = false)
	@NotNull
	private boolean allowEdit;

	/**
	 * {@code true} if the user is allowed to create/edit data for other partners.
	 */
	@Column(name = EntityConstants.USER_PERMISSION_COLUMN_ALLOW_EDIT_ALL, nullable = false)
	@NotNull
	private boolean allowEditAll;

	/**
	 * {@code true} if the user has permission to make changes to the design the <code>UserDatabase</code>
	 */
	@Column(name = EntityConstants.USER_PERMISSION_COLUMN_ALLOW_DESIGN, nullable = false)
	@NotNull
	private boolean allowDesign;

	/**
	 * {@code true} if the user can add, remove or modify other users if their belongs to the same organizational unit.
	 */
	@Column(name = EntityConstants.USER_PERMISSION_COLUMN_ALLOW_MANAGE_USERS, nullable = false)
	@NotNull
	private boolean allowManageUsers;

	/**
	 * {@code true} if the user can add, remove or modify other users, no matter if they belongs to another organizational
	 * unit.
	 */
	@Column(name = EntityConstants.USER_PERMISSION_COLUMN_ALLOW_MANAGE_ALL_USERS, nullable = false)
	@NotNull
	private boolean allowManageAllUsers;

	/**
	 * The timestamp on which the schema, as visible to the <code>user</code> was last updated.<br/>
	 * Note: owners of databases do not have a <code>UserPermission</code> record, so to establish the last update to the
	 * schema, the <code>UserDatabase</code> table also needs to be checked.
	 */
	@Column(name = EntityConstants.USER_PERMISSION_COLUMN_LAST_SCHEMA_UPDATE)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date lastSchemaUpdate;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * The Partner to which the <code>user</code> belongs.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.ORG_UNIT_COLUMN_ID, nullable = false)
	@NotNull
	private OrgUnit partner;

	/**
	 * The <code>UserDatabase</code> to which these permissions apply.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.USER_DATABASE_COLUMN_ID, nullable = false, updatable = false)
	@NotNull
	private UserDatabase database;

	/**
	 * The <code>User</code> to whom these permissions apply.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.USER_COLUMN_ID, nullable = false, updatable = false)
	@NotNull
	private User user;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public UserPermission() {
	}

	public UserPermission(final UserDatabase database, final User user) {
		this.database = database;
		this.user = user;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToString(final ToStringBuilder builder) {
		builder.append("allowView", allowView);
		builder.append("allowViewAll", allowViewAll);
		builder.append("allowEdit", allowEdit);
		builder.append("allowEditAll", allowEditAll);
		builder.append("allowDesign", allowDesign);
		builder.append("allowManageUsers", allowManageUsers);
		builder.append("allowManageAllUsers", allowManageAllUsers);
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

	public boolean isAllowView() {
		return this.allowView;
	}

	public void setAllowView(boolean allowView) {
		this.allowView = allowView;
	}

	public boolean isAllowViewAll() {
		return this.allowViewAll;
	}

	public void setAllowViewAll(boolean allowViewAll) {
		this.allowViewAll = allowViewAll;
	}

	public boolean isAllowEdit() {
		return this.allowEdit;
	}

	public void setAllowEdit(boolean allowEdit) {
		this.allowEdit = allowEdit;
	}

	public boolean isAllowEditAll() {
		return this.allowEditAll;
	}

	public void setAllowEditAll(boolean allowEditAll) {
		this.allowEditAll = allowEditAll;
	}

	public boolean isAllowDesign() {
		return this.allowDesign;
	}

	public void setAllowDesign(boolean allowDesign) {
		this.allowDesign = allowDesign;
	}

	public boolean isAllowManageUsers() {
		return allowManageUsers;
	}

	public void setAllowManageUsers(boolean allowManageUsers) {
		this.allowManageUsers = allowManageUsers;
	}

	public boolean isAllowManageAllUsers() {
		return allowManageAllUsers;
	}

	public void setAllowManageAllUsers(boolean allowManageAllUsers) {
		this.allowManageAllUsers = allowManageAllUsers;
	}

	public Date getLastSchemaUpdate() {
		return lastSchemaUpdate;
	}

	public void setLastSchemaUpdate(Date lastSchemaUpdate) {
		this.lastSchemaUpdate = lastSchemaUpdate;
	}

	@Deprecated
	public OrgUnit getPartner() {
		return this.partner;
	}

	@Deprecated
	public void setPartner(OrgUnit partner) {
		this.partner = partner;
	}

	public UserDatabase getDatabase() {
		return this.database;
	}

	public void setDatabase(UserDatabase database) {
		this.database = database;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
