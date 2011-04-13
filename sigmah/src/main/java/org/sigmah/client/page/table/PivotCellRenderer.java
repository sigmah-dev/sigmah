package org.sigmah.client.page.table;

import java.util.Map;

import org.sigmah.client.page.table.PivotGridPanel.PivotTableRow;
import org.sigmah.shared.dto.IndicatorDTO;
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