package org.sigmah.client.page.table;

import org.sigmah.client.page.table.PivotGridPanel.PivotTableRow;
import org.sigmah.shared.report.content.PivotTableData;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.i18n.client.NumberFormat;

/**
 * Renders the interior values of the Pivot table.
 * See {@link PivotTableData.Cell}
 * 
 * @author alexander
 *
 */
final class PivotCellRenderer implements
		GridCellRenderer<PivotGridPanel.PivotTableRow> {
	
	@Override
	public Object render(PivotGridPanel.PivotTableRow model, String property,
			ColumnData config, int rowIndex, int colIndex,
			ListStore<PivotGridPanel.PivotTableRow> store, Grid<PivotGridPanel.PivotTableRow> grid) {
		
		
		NumberFormat numberFormat = NumberFormat.getFormat("#,###");
		Double value = model.get(property);
		if(value == null) {
			return "";
		} else {
			if(model.getRowAxis().isTotal()) {
				return "<span class='" + PivotResources.INSTANCE.css().totalCell() + "'>" +
						numberFormat.format(value) + "</span>";
			} else {
				return numberFormat.format(value);
			}
		}
	}
}