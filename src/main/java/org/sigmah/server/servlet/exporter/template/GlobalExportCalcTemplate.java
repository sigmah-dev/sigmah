package org.sigmah.server.servlet.exporter.template;

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

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
import org.sigmah.server.servlet.exporter.data.GlobalExportData;
import org.sigmah.server.servlet.exporter.utils.CalcUtils;

public class GlobalExportCalcTemplate implements ExportTemplate {

	private final SpreadsheetDocument doc;

	public GlobalExportCalcTemplate(final GlobalExportData data) throws Throwable {

		doc = SpreadsheetDocument.newSpreadsheetDocument();
		CalcUtils.prepareCoreStyle(doc);
		Row row;
		Table table = doc.getSheetByIndex(0);
		int rowIndex = -1;
		boolean first = true;
		final Map<Integer, Integer> headerWidthMap = new HashMap<Integer, Integer>();
		final Map<Integer, Integer> contentWidthMap = new HashMap<Integer, Integer>();

		for (final String pModelName : data.getExportData().keySet()) {
			final List<String[]> dataList = data.getExportData().get(pModelName);
			if (first) {
				table.setTableName(pModelName);
			} else {
				table = doc.appendSheet(pModelName);
			}

			first = false;
			rowIndex = -1;
			headerWidthMap.clear();
			contentWidthMap.clear();
			int defaultWidth = 30;

			// titles
			final String[] header = dataList.get(0);
			row = table.getRowByIndex(++rowIndex);
			for (int i = 0; i < header.length; i++) {
				CalcUtils.putGlobalExportHeader(row, i, header[i]);
				if (header[i] != null) {
					headerWidthMap.put(i, header[i].length() / 2);
				}
			}

			// values
			for (int j = 1; j < dataList.size(); j++) {
				row = table.getRowByIndex(++rowIndex);
				final String[] values = dataList.get(j);
				int devider = 2;
				for (int i = 0; i < header.length; i++) {
					CalcUtils.createBasicCell(table, i, rowIndex, values[i]);
					if (values[i] != null) {
						String parts[] = values[i].split("\n");
						if (parts.length > devider) {
							devider = parts.length;
						}

						int currentWidth = values[i].length() / devider;
						Integer activeWidth = contentWidthMap.get(i);
						if (activeWidth != null) {
							currentWidth = Math.max(activeWidth, currentWidth);
						}
						contentWidthMap.put(i, currentWidth);
					}
				}
			}

			for (Integer i : headerWidthMap.keySet()) {
				Integer width = defaultWidth;
				if (headerWidthMap.get(i) != null) {
					width = headerWidthMap.get(i);
				}
				if (contentWidthMap.get(i) != null) {
					width = Math.max(contentWidthMap.get(i), width);
				}
				table.getColumnByIndex(i).setWidth(width + 38);
			}
		}
	}

	@Override
	public void write(OutputStream output) throws Throwable {
		doc.save(output);
		doc.close();
	}

}
