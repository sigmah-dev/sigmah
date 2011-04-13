package org.sigmah.client.page.table;

import org.sigmah.shared.report.content.PivotTableData;

public interface PivotCellRendererProvider {

	PivotCellRenderer getRendererForColumn(PivotTableData.Axis column);
	
}
