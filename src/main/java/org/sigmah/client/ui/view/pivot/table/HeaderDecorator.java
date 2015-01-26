package org.sigmah.client.ui.view.pivot.table;

import org.sigmah.shared.dto.pivot.content.PivotTableData;

interface HeaderDecorator {
	
	String decorateHeader(PivotTableData.Axis axis);
	String cornerCellHtml();

}
