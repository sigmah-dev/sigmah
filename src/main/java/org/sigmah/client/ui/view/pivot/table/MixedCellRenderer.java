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

import java.util.Map;

import org.sigmah.shared.dto.IndicatorDTO;

import com.google.gwt.core.client.GWT;
import org.sigmah.client.ui.presenter.project.indicator.IndicatorNumberFormats;
import org.sigmah.shared.dto.pivot.content.EntityCategory;
import org.sigmah.shared.dto.pivot.content.PivotTableData;
import org.sigmah.shared.dto.referential.DimensionType;

class MixedCellRenderer extends PivotCellRenderer {

	private Map<Integer, IndicatorDTO> indicators;
	private QualitativeCellRenderer qualitativeCellRenderer = new QualitativeCellRenderer();
	private QuantitativeCellRenderer quantitativeCellRenderer = new QuantitativeCellRenderer();
	
	
	public MixedCellRenderer(Map<Integer, IndicatorDTO> indicators) {
		this.indicators = indicators;
	}

	@Override
	protected String formatValue(PivotTableData.Axis rowAxis, double value) {
		IndicatorDTO indicator = indicatorForRow(rowAxis);
		if(indicator == null) {
			GWT.log("indicator in mixed cell renderer was null!");
			return "!";
		}
		if(indicator.isQualitative()) {
			qualitativeCellRenderer.setLabels(indicator.getLabels());
			return qualitativeCellRenderer.formatValue(rowAxis, value);
		} else {
			quantitativeCellRenderer.setFormat(IndicatorNumberFormats.getNumberFormat(indicator));
			return quantitativeCellRenderer.formatValue(rowAxis, value);
		}
	}

	private IndicatorDTO indicatorForRow(PivotTableData.Axis rowAxis) {
		while(rowAxis != null) {
			if(rowAxis.getDimension() != null && rowAxis.getDimension().getType() == DimensionType.Indicator) {
				int indicatorId = ((EntityCategory)rowAxis.getCategory()).getId();
				return indicators.get(indicatorId);
			}
			rowAxis = rowAxis.getParent();
		}
		return null;
	}
}
