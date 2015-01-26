package org.sigmah.client.ui.view.pivot.table;


import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import org.sigmah.shared.dto.pivot.content.PivotTableData;

public interface RendererProvider {

	GridCellRenderer forColumn(PivotTableData.Axis axis);
	
}
