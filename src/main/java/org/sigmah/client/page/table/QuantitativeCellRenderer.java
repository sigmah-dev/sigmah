package org.sigmah.client.page.table;

import org.sigmah.client.page.entry.IndicatorNumberFormats;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.report.content.PivotTableData.Axis;

import com.google.gwt.i18n.client.NumberFormat;

public class QuantitativeCellRenderer extends PivotCellRenderer {

	private NumberFormat format;
	
	public QuantitativeCellRenderer() {
	}
	
	
	public QuantitativeCellRenderer(NumberFormat format) {
		super();
		this.format = format;
	}

	public QuantitativeCellRenderer(IndicatorDTO indicator) {
		this.format = IndicatorNumberFormats.forIndicator(indicator);
	}

	public NumberFormat getFormat() {
		return format;
	}
	public void setFormat(NumberFormat format) {
		this.format = format;
	}

	@Override
	protected String formatValue(Axis rowAxis, double value) {
		return format.format(value);
	}

}
