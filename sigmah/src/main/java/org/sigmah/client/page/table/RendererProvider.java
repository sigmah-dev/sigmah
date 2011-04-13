package org.sigmah.client.page.table;

import org.sigmah.shared.report.content.PivotTableData;

import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;

public interface RendererProvider {

	GridCellRenderer forColumn(PivotTableData.Axis axis);
	
	
}
