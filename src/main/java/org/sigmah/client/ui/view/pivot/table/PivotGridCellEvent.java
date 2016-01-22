package org.sigmah.client.ui.view.pivot.table;

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


import com.extjs.gxt.ui.client.event.GridEvent;
import org.sigmah.shared.dto.pivot.content.PivotTableData;

public class PivotGridCellEvent extends GridEvent<PivotGridPanel.PivotTableRow> {
	
	private PivotTableData.Axis columnAxis;
	
	public PivotGridCellEvent(GridEvent<PivotGridPanel.PivotTableRow> event, PivotTableData.Axis columnAxis) {
		super(event.getGrid(), event.getEvent());
		this.setColIndex(event.getColIndex());
		this.columnAxis = columnAxis;
		this.setProperty(event.getProperty());
		this.setRecord(event.getRecord());
		if(event.getModel() != null) {
			this.setModel(event.getModel());
		} else if(event.getRecord() != null) {
			this.setModel((PivotGridPanel.PivotTableRow)event.getRecord().getModel());
		}
	}
	
	public PivotTableData.Axis getRowAxis() {
		return getModel().getRowAxis();
	}
	
	public PivotTableData.Axis getColumnAxis() {
		return columnAxis;
	}
	
	public PivotTableData.Cell getCell() {
		return getRowAxis().getCell(columnAxis);
	}
	
	public PivotTableData.Cell getOrCreateCell() {
		return getRowAxis().getOrCreateCell(columnAxis);
	}
	
	public int getIndicatorId() {
		return this.getModel().getIndicatorId(this.getProperty());
	}
}
