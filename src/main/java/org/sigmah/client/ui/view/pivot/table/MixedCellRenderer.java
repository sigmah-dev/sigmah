package org.sigmah.client.ui.view.pivot.table;

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
