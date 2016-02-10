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


import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Layout Group domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.LAYOUT_GROUP_TABLE)
public class LayoutGroup extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -5138315416849070907L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.LAYOUT_GROUP_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.LAYOUT_GROUP_COLUMN_ROW_INDEX, nullable = false)
	@NotNull
	private Integer row;

	@Column(name = EntityConstants.LAYOUT_GROUP_COLUMN_COLUMN_INDEX, nullable = false)
	@NotNull
	private Integer column;

	@Column(name = EntityConstants.LAYOUT_GROUP_COLUMN_TITLE, nullable = true, length = EntityConstants.LAYOUT_GROUP_TITLE_MAX_LENGTH)
	@Size(max = EntityConstants.LAYOUT_GROUP_TITLE_MAX_LENGTH)
	private String title;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.LAYOUT_COLUMN_ID, nullable = false)
	@NotNull
	private Layout parentLayout;

	@OneToMany(mappedBy = "parentLayoutGroup", cascade = CascadeType.ALL)
	@OrderBy("sortOrder ASC, id ASC")
	private List<LayoutConstraint> constraints = new ArrayList<LayoutConstraint>();

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public LayoutGroup() {
	}

	/**
	 * Adds a constraint to the current layout group if it is not null.
	 * 
	 * @param constraint
	 *          The constraint to add.
	 */
	public void addConstraint(LayoutConstraint constraint) {

		if (constraint == null) {
			return;
		}

		constraint.setParentLayoutGroup(this);
		constraints.add(constraint);
	}

	/**
	 * Reset the identifiers of the object.
	 * 
	 * @param parentLayout
	 *          the parent Layout
	 * @param keepPrivacyGroups
	 *			<code>false</code> to set privacy group value to <code>null</code>, <code>true</code> to let it as is.
	 */
	public void resetImport(Layout parentLayout, boolean keepPrivacyGroups) {
		this.id = null;
		this.parentLayout = parentLayout;

		if (this.constraints != null) {
			for (LayoutConstraint layoutConstraint : constraints) {
				if (layoutConstraint != null) {
					layoutConstraint.resetImport(this, keepPrivacyGroups);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToString(final ToStringBuilder builder) {
		builder.append("row", row);
		builder.append("column", column);
		builder.append("title", title);
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

	public void setParentLayout(Layout parentLayout) {
		this.parentLayout = parentLayout;
	}

	public Layout getParentLayout() {
		return parentLayout;
	}

	public void setRow(Integer row) {
		this.row = row;
	}

	public Integer getRow() {
		return row;
	}

	public void setColumn(Integer column) {
		this.column = column;
	}

	public Integer getColumn() {
		return column;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setConstraints(List<LayoutConstraint> constraints) {
		this.constraints = constraints;
	}

	public List<LayoutConstraint> getConstraints() {
		return constraints;
	}

}
