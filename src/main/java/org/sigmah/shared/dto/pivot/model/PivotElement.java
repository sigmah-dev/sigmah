package org.sigmah.shared.dto.pivot.model;

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
import org.sigmah.shared.dto.pivot.content.PivotContent;

import org.sigmah.shared.dto.referential.DimensionType;

/**
 * Abstract base class that exposes properties common to the PivotTable and PivotChart elements
 * 
 * @author Alex Bertram (v1.3)
 */
public abstract class PivotElement<ContentT extends PivotContent> extends ReportElement<ContentT> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 4325435264779198578L;

	private boolean showEmptyCells = false;

	/**
	 * Returns the set of all dimensions that figure in the pivot table/chart, whether in the row, column, horizantal
	 * axis, etc.
	 * 
	 * @return The set of all dimensions.
	 */
	public abstract Set<Dimension> allDimensions();

	/**
	 * Returns the set of all dimension <i>types</i> that figures in the pivot table/chart.
	 * 
	 * @return The set of all {@link DimensionType}.
	 * @see #allDimensions()
	 */
	public Set<DimensionType> allDimensionTypes() {
		Set<DimensionType> set = new HashSet<DimensionType>();

		for (Dimension dimension : allDimensions()) {
			set.add(dimension.getType());
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContentT getContent() {
		return content;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setContent(ContentT content) {
		this.content = content;
	}

	public boolean isShowEmptyCells() {
		return showEmptyCells;
	}

	public void setShowEmptyCells(boolean showEmptyCells) {
		this.showEmptyCells = showEmptyCells;
	}

}
