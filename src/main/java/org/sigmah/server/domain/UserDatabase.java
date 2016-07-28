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
