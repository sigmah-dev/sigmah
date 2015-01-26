package org.sigmah.client.page.table;

import org.sigmah.client.page.table.PivotGridPanel.PivotTableRow;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;

class PivotTreeGridCellRenderer extends
		TreeGridCellRenderer<PivotGridPanel.PivotTableRow> {
	
	@Override
	public Object render(PivotGridPanel.PivotTableRow model, String property,
			ColumnData config, int rowIndex, int colIndex,
			ListStore store, Grid grid) {
		
		Object result = super.render(model, property, config, rowIndex, colIndex, store, grid);
		
		// Give the first column (containing row headers) the same style as the column headers
		config.css = config.css + " x-grid3-header";
		return result;
	}
}