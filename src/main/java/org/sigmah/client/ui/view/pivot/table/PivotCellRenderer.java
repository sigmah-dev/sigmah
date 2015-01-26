package org.sigmah.client.ui.view.pivot.table;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import org.sigmah.shared.dto.pivot.content.PivotTableData;

/**
 * Renders the interior values of the Pivot table.
 * See {@link PivotTableData.Cell}
 * 
 * @author alexander
 *
 */
abstract class PivotCellRenderer implements
		GridCellRenderer<PivotGridPanel.PivotTableRow> {
	
	
	public PivotCellRenderer() {
	}

	@Override
	public Object render(PivotGridPanel.PivotTableRow model, String property,
			ColumnData config, int rowIndex, int colIndex,
			ListStore<PivotGridPanel.PivotTableRow> store, Grid<PivotGridPanel.PivotTableRow> grid) {
		
		
		Double value = model.get(property);
		if(value == null) {
			return "";
		} else {
			String formattedValue = formatValue(model.getRowAxis(), value);
			if(model.getRowAxis().isTotal()) {
				return "<span class='" + PivotResources.INSTANCE.css().totalCell() + "'>" +
						formattedValue + "</span>";
			} else {
				return formattedValue;
			}
		}
	}

	protected abstract String formatValue(PivotTableData.Axis rowAxis, double value);
}