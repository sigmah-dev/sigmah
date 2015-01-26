package org.sigmah.client.ui.view.pivot.table;

import org.sigmah.shared.dto.pivot.content.PivotTableData;

public interface PivotCellRendererProvider {

	PivotCellRenderer getRendererForColumn(PivotTableData.Axis column);
	
}
