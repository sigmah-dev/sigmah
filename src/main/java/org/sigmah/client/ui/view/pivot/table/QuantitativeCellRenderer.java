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
