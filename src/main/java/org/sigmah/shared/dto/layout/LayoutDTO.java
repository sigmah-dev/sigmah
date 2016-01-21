package org.sigmah.shared.dto.layout;

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

import java.util.List;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

/**
 * LayoutDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class LayoutDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8520711106031085130L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "layout.Layout";

	// DTO attributes keys.
	public static final String ROWS_COUNT = "rowsCount";
	public static final String COLUMNS_COUNT = "columnsCount";
	public static final String GROUPS = "groups";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(ROWS_COUNT, getRowsCount());
		builder.append(COLUMNS_COUNT, getColumnsCount());
	}

	// Rows count
	public Integer getRowsCount() {
		return (Integer) get(ROWS_COUNT);
	}

	public void setRowsCount(Integer rowsCount) {
		set(ROWS_COUNT, rowsCount);
	}

	// Columns count
	public Integer getColumnsCount() {
		return (Integer) get(COLUMNS_COUNT);
	}

	public void setColumnsCount(Integer columnsCount) {
		set(COLUMNS_COUNT, columnsCount);
	}

	// Reference to layout groups list
	public List<LayoutGroupDTO> getGroups() {
		return get(GROUPS);
	}

	public void setGroups(List<LayoutGroupDTO> groups) {
		set(GROUPS, groups);
	}

	public Widget getWidget() {
		return new Grid(getRowsCount(), getColumnsCount());
	}

}
