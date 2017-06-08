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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Layout domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.LAYOUT_TABLE)
public class Layout extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 3567671639080023704L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.LAYOUT_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.LAYOUT_COLUMN_ROWS_COUNT, nullable = false)
	@NotNull
	private Integer rowsCount;

	@Column(name = EntityConstants.LAYOUT_COLUMN_COLUMNS_COUT, nullable = false)
	@NotNull
	private Integer columnsCount;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@OneToMany(mappedBy = "parentLayout", cascade = CascadeType.ALL)
	private List<LayoutGroup> groups = new ArrayList<LayoutGroup>();

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public Layout() {
		// Default empty constructor.
	}

	/**
	 * Creates a {@link LayoutGroup} for each cell generated from the given numbers of rows and columns.
	 * 
	 * @param rows
	 *          The number of rows in the layout.
	 * @param cols
	 *          The number of columns in the layout.
	 */
	public Layout(final int rows, final int cols) {
		rowsCount = rows;
		columnsCount = cols;

		for (int row = 0; row < rowsCount; row++) {
			for (int col = 0; col < columnsCount; col++) {

				final LayoutGroup group = new LayoutGroup();
				group.setRow(row);
				group.setColumn(col);
				group.setHasIterations(false);
				group.setTitle("Group " + groups.size());

				group.setParentLayout(this);
				groups.add(group);
			}
		}
	}

	/**
	 * Adds a constraint to position an element in a current layout's group.
	 * 
	 * @param row
	 *          The row of the group.
	 * @param col
	 *          The column of the group
	 * @param elem
	 *          The element constrained.
	 * @param order
	 *          The constraint.
	 */
	public void addConstraint(int row, int col, FlexibleElement elem, int order) {

		// Checks cell index constraints.
		if (row < 0 || row > rowsCount || col < 0 || col > columnsCount) {
			return;
		}

		// Creates the constraint.
		final LayoutConstraint constraint = new LayoutConstraint();
		constraint.setElement(elem);
		constraint.setSortOrder(order);

		// Adds it to the correct group.
		for (final LayoutGroup group : groups) {
			if (group.getRow() == row && group.getColumn() == col) {
				group.addConstraint(constraint);
				return;
			}
		}
	}

	/**
	 * Reset the identifiers of the object.
	 * 
	 * @param keepPrivacyGroups
	 *			<code>false</code> to set privacy group value to <code>null</code>, <code>true</code> to let it as is.
	 */
	public void resetImport(boolean keepPrivacyGroups) {
		this.id = null;
		if (this.groups != null) {
			for (LayoutGroup layoutGroup : groups) {
				if (layoutGroup != null) {
					layoutGroup.resetImport(this, keepPrivacyGroups);
				}
			}
		}
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

	public void setRowsCount(Integer rowsCount) {
		this.rowsCount = rowsCount;
	}

	public Integer getRowsCount() {
		return rowsCount;
	}

	public void setColumnsCount(Integer columnsCount) {
		this.columnsCount = columnsCount;
	}

	public Integer getColumnsCount() {
		return columnsCount;
	}

	public void setGroups(List<LayoutGroup> groups) {
		this.groups = groups;
	}

	public List<LayoutGroup> getGroups() {
		return groups;
	}

}
