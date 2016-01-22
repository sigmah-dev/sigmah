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

import java.util.Collections;
import java.util.List;

import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.pivot.content.PivotTableData;

public class QualitativeCellRenderer extends PivotCellRenderer {

	private List<String> labels;

	public QualitativeCellRenderer(IndicatorDTO indicator) {
		this.labels = indicator.getLabels();
		if(labels == null) {
			labels = Collections.emptyList();
		}
	}

	public QualitativeCellRenderer() {
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	@Override
	protected String formatValue(PivotTableData.Axis rowAxis, double value) {
		int code = (int) value;
		if(code >= 1 && code <= labels.size()) {
			return labels.get(code-1);
		}
		return "";
	}
}
