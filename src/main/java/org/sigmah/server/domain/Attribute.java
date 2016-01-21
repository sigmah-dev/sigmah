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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.Deleteable;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.Orderable;
import org.sigmah.server.domain.util.SchemaElement;

/**
 * <p>
 * Attribute domain entity.
 * <p>
 * <p>
 * An attribute is like a boolean parameter or a caracterisation of an activity followed in Activity Info. Indeed, this
 * notion does not exist in Sigmah, only in Activity Info.
 * </p>
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.ATTRIBUTE_TABLE)
public class Attribute extends AbstractEntityId<Integer> implements Deleteable, Orderable, SchemaElement {

	/**
	 * Seriald version UID.
	 */
	private static final long serialVersionUID = -861408062042779542L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.ATTRIBUTE_COLUMN_ID, unique = true, nullable = false)
	private Integer id;

	@Column(name = EntityConstants.ATTRIBUTE_COLUMN_NAME, nullable = false, length = EntityConstants.NAME_MAX_LENGTH)
	@NotNull
	@Size(max = EntityConstants.NAME_MAX_LENGTH)
	private String name;

	@Column(name = EntityConstants.COLUMN_SORT_ORDER, nullable = false)
	@NotNull
	private int sortOrder;

	@Column(name = EntityConstants.COLUMN_DATE_DELETED)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateDeleted;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AttributeGroupId", nullable = false)
	@NotNull
	private AttributeGroup group;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public Attribute() {
		// Default empty constructor.
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
		return getDateDeleted() != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToString(final ToStringBuilder builder) {
		builder.append("name", name);
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

	@Override
	public int getSortOrder() {
		return this.sortOrder;
	}

	@Override
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Date getDateDeleted() {
		return this.dateDeleted;
	}

	public void setDateDeleted(Date date) {
		this.dateDeleted = date;
	}

	public AttributeGroup getGroup() {
		return this.group;
	}

	public void setGroup(AttributeGroup group) {
		this.group = group;
	}

}
