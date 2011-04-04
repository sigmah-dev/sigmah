package org.sigmah.client.page.table;

import org.sigmah.client.page.table.PivotGridPanel.PivotTableRow;
import org.sigmah.shared.report.content.PivotTableData;

import com.extjs.gxt.ui.client.event.GridEvent;

public class PivotGridCellEvent extends GridEvent<PivotTableRow> {
	
	private PivotTableData.Axis columnAxis;
	
	public PivotGridCellEvent(GridEvent<PivotGridPanel.PivotTableRow> event, PivotTableData.Axis columnAxis) {
		super(event.getGrid(), event.getEvent());
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
}
