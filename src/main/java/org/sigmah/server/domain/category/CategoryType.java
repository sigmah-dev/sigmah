package org.sigmah.server.domain.category;

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


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.Organization;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.shared.dto.referential.CategoryIcon;

/**
 * <p>
 * Category Type domain entity.
 * </p>
 * 
 * @author tmi
 */
@Entity
@Table(name = EntityConstants.CATEGORY_TYPE_TABLE)
public class CategoryType extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -1069628345470292474L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.CATEGORY_TYPE_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.CATEGORY_TYPE_COLUMN_LABEL, nullable = false, length = EntityConstants.CATEGORY_TYPE_LABEL_MAX_LENGTH)
	@NotNull
	@Size(max = EntityConstants.CATEGORY_TYPE_LABEL_MAX_LENGTH)
	private String label;

	@Column(name = EntityConstants.CATEGORY_TYPE_COLUMN_ICON_NAME, nullable = false)
	@Enumerated(value = EnumType.STRING)
	@NotNull
	private CategoryIcon icon;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne
	@JoinColumn(name = EntityConstants.ORGANIZATION_COLUMN_ID)
	private Organization organization;

	@OneToMany(mappedBy = "parentType", cascade = CascadeType.ALL)
	@OrderBy("label ASC")
	private List<CategoryElement> elements = new ArrayList<CategoryElement>();

	// constructeur

	public CategoryType() {

	}

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	@Override
	protected void appendToString(ToStringBuilder builder) {
		builder.append("label", label);
		builder.append("icon", icon);
	}

	/**
	 * Reset the identifiers of the object.
	 */
	public void resetImport() {
		this.id = null;
		if (elements != null) {
			for (CategoryElement categoryElement : elements) {
				categoryElement.resetImport();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 23 * hash + Objects.hashCode(this.label);
		hash = 23 * hash + Objects.hashCode(this.icon);
		return hash;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final CategoryType other = (CategoryType) obj;
		if (!Objects.equals(this.label, other.label)) {
			return false;
		}
		return this.icon == other.icon;
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<CategoryElement> getElements() {
		return elements;
	}

	public void setElements(List<CategoryElement> elements) {
		this.elements = elements;
	}

	public CategoryIcon getIcon() {
		return icon;
	}

	public void setIcon(CategoryIcon icon) {
		this.icon = icon;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
}
