package org.sigmah.server.servlet.exporter.data;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.shared.dto.pivot.content.PivotTableData;
import org.sigmah.server.servlet.exporter.base.Exporter;
import org.sigmah.server.servlet.exporter.utils.ExportConstants.MultiItemText;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.dto.IndicatorDTO;

/**
 * @author sherzod (v1.3)
 */
public class IndicatorEntryData extends ExportData {

	private final String projectName;
	private final Map<Integer, String> groupMap = new HashMap<Integer, String>();
	private final IndicatorListResult indicators;
	private final Map<Integer, PivotTableData> entryMap = new HashMap<Integer, PivotTableData>();

	public IndicatorEntryData(final Exporter exporter, final IndicatorListResult indicators, final String projectName) {
		super(exporter, 4);
		this.indicators = indicators;
		this.projectName = projectName;
	}

	public Object getFormattedValue(IndicatorDTO dto) {
		Object formatted = null;
		if (dto.getLabelCounts() != null) {
			formatted = dto.formatMode();
		} else {
			if (dto.getCurrentValue() == null)
				dto.setCurrentValue(0.0);
			if (dto.getAggregation() == IndicatorDTO.AGGREGATE_AVG) {
				formatted = dto.getCurrentValue();
			} else {
				formatted = new Long(dto.getCurrentValue().longValue());
			}
		}
		return formatted;
	}

	public MultiItemText formatPossibleValues(List<String> list) {
		final StringBuffer builder = new StringBuffer();
		int lines = 1;
		for (String text : list) {
			builder.append(" - ");
			builder.append(text);
			builder.append("\n");
			lines++;
		}
		String value = null;
		if (lines > 1) {
			value = builder.substring(0, builder.length() - 2);
			lines--;
		}

		return new MultiItemText(value, lines);
	}

	public String getLabelByIndex(List<String> labels, Double doubleIndex) {
		String label = "";
		int index = 0;

		if (doubleIndex != null) {
			index = (int) doubleIndex.doubleValue() - 1;
		}
		if (labels.size() > index) {
			label = labels.get(index);
		}

		return label;
	}

	public Map<Integer, String> getGroupMap() {
		return groupMap;
	}

	public Map<Integer, PivotTableData> getEntryMap() {
		return entryMap;
	}

	public String getProjectName() {
		return projectName;
	}

	public IndicatorListResult getIndicators() {
		return indicators;
	}

}
