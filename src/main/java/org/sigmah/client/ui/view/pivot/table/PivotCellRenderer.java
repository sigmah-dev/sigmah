package org.sigmah.client.ui.view.pivot.table;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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