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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import org.sigmah.shared.dto.pivot.content.PivotContent;

public class PivotTableElement extends PivotElement<PivotContent> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -4951153184446858800L;

	private List<Dimension> columnDimensions = new ArrayList<Dimension>();
	private List<Dimension> rowDimensions = new ArrayList<Dimension>();

	public PivotTableElement() {

	}

	@XmlElement(name = "dimension")
	@XmlElementWrapper(name = "columns")
	public List<Dimension> getColumnDimensions() {
		return columnDimensions;
	}

	public void setColumnDimensions(List<Dimension> columnDimensions) {
		this.columnDimensions = columnDimensions;
	}

	public void addColDimension(Dimension dim) {
		this.columnDimensions.add(dim);
	}

	public void addColDimensions(Collection<Dimension> dims) {
		this.columnDimensions.addAll(dims);
	}

	@XmlElement(name = "dimension")
	@XmlElementWrapper(name = "rows")
	public List<Dimension> getRowDimensions() {
		return rowDimensions;
	}

	public void setRowDimensions(List<Dimension> rowDimensions) {
		this.rowDimensions = rowDimensions;
	}

	public void addRowDimension(Dimension dim) {
		this.rowDimensions.add(dim);
	}

	public void addRowDimensions(Collection<Dimension> dims) {
		this.rowDimensions.addAll(dims);
	}

	@Override
	public Set<Dimension> allDimensions() {
		Set<Dimension> set = new HashSet<Dimension>();
		set.addAll(columnDimensions);
		set.addAll(rowDimensions);

		return set;
	}

}
