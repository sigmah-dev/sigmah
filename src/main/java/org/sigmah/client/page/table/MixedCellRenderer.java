package org.sigmah.client.page.table;

import java.util.Map;

import org.sigmah.client.page.entry.IndicatorNumberFormats;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.report.content.EntityCategory;
import org.sigmah.shared.report.content.PivotTableData;
import org.sigmah.shared.report.content.PivotTableData.Axis;
import org.sigmah.shared.report.model.DimensionType;

import com.google.gwt.core.client.GWT;

class MixedCellRenderer extends PivotCellRenderer {

	private Map<Integer, IndicatorDTO> indicators;
	private QualitativeCellRenderer qualitativeCellRenderer = new QualitativeCellRenderer();
	private QuantitativeCellRenderer quantitativeCellRenderer = new QuantitativeCellRenderer();
	
	
	public MixedCellRenderer(Map<Integer, IndicatorDTO> indicators) {
		this.indicators = indicators;
	}

	@Override
	protected String formatValue(Axis rowAxis, double value) {
		IndicatorDTO indicator = indicatorForRow(rowAxis);
		if(indicator == null) {
			GWT.log("indicator in mixed cell renderer was null!");
			return "!";
		}
		if(indicator.isQualitative()) {
			qualitativeCellRenderer.setLabels(indicator.getLabels());
			return qualitativeCellRenderer.formatValue(rowAxis, value);
		} else {
			quantitativeCellRenderer.setFormat(IndicatorNumberFormats.forIndicator(indicator));
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
