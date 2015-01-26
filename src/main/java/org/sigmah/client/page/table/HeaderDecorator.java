package org.sigmah.client.page.table;

import org.sigmah.shared.report.content.PivotTableData.Axis;

interface HeaderDecorator {
	
	String decorateHeader(Axis axis);
	String cornerCellHtml();

}
