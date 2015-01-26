package org.sigmah.client.ui.view.pivot.table;

import org.sigmah.shared.dto.IndicatorDTO;

import com.google.gwt.i18n.client.NumberFormat;
import org.sigmah.client.ui.presenter.project.indicator.IndicatorNumberFormats;
import org.sigmah.shared.dto.pivot.content.PivotTableData;

public class QuantitativeCellRenderer extends PivotCellRenderer {

	private NumberFormat format;
	
	public QuantitativeCellRenderer() {
	}
	
	
	public QuantitativeCellRenderer(NumberFormat format) {
		super();
		this.format = format;
	}

	public QuantitativeCellRenderer(IndicatorDTO indicator) {
		this.format = IndicatorNumberFormats.getNumberFormat(indicator);
	}

	public NumberFormat getFormat() {
		return format;
	}
	public void setFormat(NumberFormat format) {
		this.format = format;
	}

	@Override
	protected String formatValue(PivotTableData.Axis rowAxis, double value) {
		return format.format(value);
	}

}
