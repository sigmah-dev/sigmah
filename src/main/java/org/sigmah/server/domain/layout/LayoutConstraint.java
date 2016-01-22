package org.sigmah.server.domain.layout;

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


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Layout Constraint domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.LAYOUT_CONSTRAINT_TABLE)
public class LayoutConstraint extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -5150783265586227961L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.LAYOUT_CONSTRAINT_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.LAYOUT_CONSTRAINT_COLUMN_SORT_ORDER, nullable = true)
	private Integer sortOrder;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.LAYOUT_GROUP_COLUMN_ID, nullable = false)
	@NotNull
	private LayoutGroup parentLayoutGroup;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_flexible_element", nullable = false)
	@NotNull
	private FlexibleElement element;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Reset the identifiers of the object.
	 * 
	 * @param parentLayoutGroup
	 *          the parent LayoutGroup
	 * @param keepPrivacyGroups
	 *			<code>false</code> to set privacy group value to <code>null</code>, <code>true</code> to let it as is.
	 */
	public void resetImport(LayoutGroup parentLayoutGroup, boolean keepPrivacyGroups) {
		this.id = null;
		this.parentLayoutGroup = parentLayoutGroup;

		if (this.element != null) {
			this.element.resetImport(keepPrivacyGroups);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToString(final ToStringBuilder builder) {
		builder.append("sortOrder", sortOrder);
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

	public void setParentLayoutGroup(LayoutGroup parentLayoutGroup) {
		this.parentLayoutGroup = parentLayoutGroup;
	}

	public LayoutGroup getParentLayoutGroup() {
		return parentLayoutGroup;
	}

	public void setElement(FlexibleElement element) {
		this.element = element;
	}

	public FlexibleElement getElement() {
		return element;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

}
