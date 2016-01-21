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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.SchemaElement;

/**
 * <p>
 * Country domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.COUNTRY_TABLE)
public class Country extends AbstractEntityId<Integer> implements SchemaElement {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 6374196595434657898L;

	/**
	 * Default country id.
	 */
	public static final Integer DEFAULT_COUNTRY_ID = 1;

	/**
	 * Default country name.
	 */
	public static final String DEFAULT_COUNTRY_NAME = "France";

	/**
	 * The country's id.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.COUNTRY_COLUMN_ID, unique = true, nullable = false)
	private Integer id;

	/**
	 * A short, human-readable name of the Country.
	 */
	@Column(name = EntityConstants.COUNTRY_COLUMN_NAME, nullable = false, length = EntityConstants.NAME_MAX_LENGTH)
	@NotNull
	@Size(max = EntityConstants.NAME_MAX_LENGTH)
	private String name;

	@Column(name = EntityConstants.COUNTRY_COLUMN_ISO_CODE, length = EntityConstants.ISO2_CODE_MAX_LENGTH)
	@Size(max = EntityConstants.ISO2_CODE_MAX_LENGTH)
	private String codeISO;

	/**
	 * The geographic bounds of this Country.<br/>
	 * Bounds for the Country cannot be {@code null}.
	 */
	@Embedded
	@AttributeOverrides({
												@AttributeOverride(name = "x1", column = @Column(nullable = false)),
												@AttributeOverride(name = "y1", column = @Column(nullable = false)),
												@AttributeOverride(name = "x2", column = @Column(nullable = false)),
												@AttributeOverride(name = "y2", column = @Column(nullable = false))
	})
	private Bounds bounds;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * All the administrative levels for this Country.
	 */
	@OneToMany(mappedBy = "country", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@org.hibernate.annotations.OrderBy(clause = EntityConstants.ADMIN_LEVEL_COLUMN_ID)
	private Set<AdminLevel> adminLevels = new HashSet<AdminLevel>(0);

	@OneToMany(mappedBy = "country", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<LocationType> locationTypes = new HashSet<LocationType>(0);

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public Country() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToString(final ToStringBuilder builder) {
		builder.append("name", name);
		builder.append("codeISO", codeISO);
		builder.append("bounds", bounds);
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

	public String getCodeISO() {
		return this.codeISO;
	}

	public void setCodeISO(String codeISO) {
		this.codeISO = codeISO;
	}

	public Bounds getBounds() {
		return this.bounds;
	}

	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	public Set<AdminLevel> getAdminLevels() {
		return this.adminLevels;
	}

	public void setAdminLevels(Set<AdminLevel> adminLevels) {
		this.adminLevels = adminLevels;
	}

	public Set<LocationType> getLocationTypes() {
		return locationTypes;
	}

	public void setLocationTypes(Set<LocationType> types) {
		this.locationTypes = types;
	}

}
