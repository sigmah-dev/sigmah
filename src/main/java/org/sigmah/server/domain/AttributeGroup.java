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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.OrderBy;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.Deleteable;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.EntityFilters;
import org.sigmah.server.domain.util.Orderable;
import org.sigmah.server.domain.util.SchemaElement;

/**
 * <p>
 * Attribute group domain entity.
 * </p>
 * <p>
 * An attributegroup is a group of attributes.
 * </p>
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.ATTRIBUTE_GROUP_TABLE)
@Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.ATTRIBUTE_GROUP_HIDE_DELETED_CONDITION)
public class AttributeGroup extends AbstractEntityId<Integer> implements Deleteable, Orderable, SchemaElement {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1348645779346918621L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.ATTRIBUTE_GROUP_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.ATTRIBUTE_GROUP_COLUMN_NAME, length = EntityConstants.ATTRIBUTE_GROUP_NAME_MAX_LENGTH)
	@Size(max = EntityConstants.ATTRIBUTE_GROUP_NAME_MAX_LENGTH)
	private String name;

	@Column(name = EntityConstants.COLUMN_SORT_ORDER, nullable = false)
	@NotNull
	private int sortOrder;

	@Column(name = EntityConstants.ATTRIBUTE_GROUP_MULTIPLE_ALLOWED, nullable = false)
	@NotNull
	private boolean multipleAllowed;

	@Column(name = EntityConstants.ATTRIBUTE_GROUP_COLUMN_CATEGORY, length = EntityConstants.ATTRIBUTE_GROUP_CATEGORY_MAX_LENGTH, nullable = true)
	@Size(max = EntityConstants.ATTRIBUTE_GROUP_CATEGORY_MAX_LENGTH)
	private String category;

	@Column(name = EntityConstants.COLUMN_DATE_DELETED)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date dateDeleted;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "group")
	@Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
	@OrderBy(clause = EntityConstants.COLUMN_SORT_ORDER)
	@Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.ATTRIBUTE_GROUP_HIDE_DELETED_CONDITION)
	private Set<Attribute> attributes = new HashSet<Attribute>(0);

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = EntityConstants.ATTRIBUTE_GROUP_ACTIVITY_LINK_TABLE, joinColumns = { @JoinColumn(name = EntityConstants.ATTRIBUTE_GROUP_COLUMN_ID, nullable = false, updatable = false)
	}, inverseJoinColumns = { @JoinColumn(name = EntityConstants.ACTIVITY_COLUMN_ID, nullable = false, updatable = false)
	})
	private Set<Activity> activities = new HashSet<Activity>(0);

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public AttributeGroup() {
		// Default empty constructor.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete() {
		this.setDateDeleted(new Date());
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
		builder.append("multipleAllowed", multipleAllowed);
		builder.append("category", category);
		builder.append("sortOrder", sortOrder);
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
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Activity> getActivities() {
		return this.activities;
	}

	public void setActivities(Set<Activity> activities) {
		this.activities = activities;
	}

	public Set<Attribute> getAttributes() {
		return this.attributes;
	}

	public void setAttributes(Set<Attribute> attributes) {
		this.attributes = attributes;
	}

	@Override
	public int getSortOrder() {
		return sortOrder;
	}

	@Override
	public void setSortOrder(int order) {
		this.sortOrder = order;
	}

	public boolean isMultipleAllowed() {
		return multipleAllowed;
	}

	public void setMultipleAllowed(boolean allowed) {
		this.multipleAllowed = allowed;
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

}
