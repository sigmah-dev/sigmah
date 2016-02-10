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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Admin level domain entity.
 * </p>
 * <p>
 * An AdminLevel corresponds to a hierarchy of LocationType related to a Country.
 * </p>
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.ADMIN_LEVEL_TABLE)
public class AdminLevel extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 6740752628579376666L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.ADMIN_LEVEL_COLUMN_ID, unique = true, nullable = false)
	private Integer id;

	@Column(name = EntityConstants.ADMIN_LEVEL_COLUMN_NAME, nullable = false, length = EntityConstants.ADMIN_LEVEL_NAME_MAX_LENGTH)
	@NotNull
	@Size(max = EntityConstants.ADMIN_LEVEL_NAME_MAX_LENGTH)
	private String name;

	@Column(name = EntityConstants.ADMIN_LEVEL_COLUMN_ALLOW_ADD, nullable = false)
	@NotNull
	private boolean allowAdd;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.COUNTRY_COLUMN_ID, nullable = false)
	@NotNull
	private Country country;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.ADMIN_LEVEL_COLUMN_PARENT)
	private AdminLevel parent;

	@OneToMany(mappedBy = "level", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<AdminEntity> entities = new HashSet<AdminEntity>(0);

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<AdminLevel> childLevels = new HashSet<AdminLevel>(0);

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public AdminLevel() {
		// Default empty constructor.
	}

	public AdminLevel(int adminLevelId, Country country, String name, boolean allowAdd) {
		this(adminLevelId, country, null, name, allowAdd, null, null);
	}

	public AdminLevel(int adminLevelId, Country country, AdminLevel adminLevel, String name, boolean allowAdd, Set<AdminEntity> entities, Set<AdminLevel> childLevels) {
		this.id = adminLevelId;
		this.country = country;
		this.parent = adminLevel;
		this.name = name;
		this.allowAdd = allowAdd;
		this.entities = entities == null ? new HashSet<AdminEntity>(0) : entities;
		this.childLevels = childLevels == null ? new HashSet<AdminLevel>(0) : childLevels;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToString(final ToStringBuilder builder) {
		builder.append("name", name);
		builder.append("allowAdd", allowAdd);
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

	public AdminLevel getParent() {
		return this.parent;
	}

	public void setParent(AdminLevel adminLevel) {
		this.parent = adminLevel;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAllowAdd() {
		return this.allowAdd;
	}

	public void setAllowAdd(boolean allowAdd) {
		this.allowAdd = allowAdd;
	}

	public Set<AdminEntity> getEntities() {
		return this.entities;
	}

	public void setEntities(Set<AdminEntity> entities) {
		this.entities = entities;
	}

	public Set<AdminLevel> getChildLevels() {
		return this.childLevels;
	}

	public void setChildLevels(Set<AdminLevel> childLevels) {
		this.childLevels = childLevels;
	}

}
