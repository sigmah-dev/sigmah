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
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
import org.sigmah.server.servlet.exporter.data.GlobalExportData;
import org.sigmah.server.servlet.exporter.data.cells.GlobalExportDataCell;
import org.sigmah.server.servlet.exporter.data.cells.GlobalExportLinkCell;
import org.sigmah.server.servlet.exporter.data.cells.GlobalExportStringCell;
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
			final List<GlobalExportDataCell[]> dataList = data.getExportData().get(pModelName);
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
			final GlobalExportDataCell[] header = dataList.get(0);
			row = table.getRowByIndex(++rowIndex);
			for (int i = 0; i < header.length; i++) {
				int width = -1;
				if (header[i] instanceof GlobalExportStringCell) {
					GlobalExportStringCell cell = (GlobalExportStringCell)header[i];
					CalcUtils.putGlobalExportHeader(row, i, cell.getText());
					if (cell.getText() != null) {
						width = cell.getText().length() / 2;
					}
				} else if (header[i] instanceof GlobalExportLinkCell) {
					// no links on headers for the moment
					GlobalExportLinkCell cell = (GlobalExportLinkCell)header[i];
					CalcUtils.putGlobalExportHeader(row, i, cell.getText());
					if (cell.getText() != null) {
						width = cell.getText().length() / 2;
					}
				}
				if (width != -1) {
					headerWidthMap.put(i, width);
				}
			}

			// values
			for (int j = 1; j < dataList.size(); j++) {
				row = table.getRowByIndex(++rowIndex);
				final GlobalExportDataCell[] values = dataList.get(j);
				int devider = 2;
				for (int i = 0; i < header.length; i++) {

					int width = -1;

					if(values[i] instanceof GlobalExportStringCell) {

						String text = ((GlobalExportStringCell) values[i]).getText();
						CalcUtils.createBasicCell(table, i, rowIndex, text);
						if (text != null) {
							String parts[] = text.split("\n");
							if (parts.length > devider) {
								devider = parts.length;
							}

							int currentWidth = text.length() / devider;
							Integer activeWidth = contentWidthMap.get(i);
							if (activeWidth != null) {
								currentWidth = Math.max(activeWidth, currentWidth);
							}
							width = currentWidth;
						}
					} else if (values[i] instanceof GlobalExportLinkCell) {

						GlobalExportLinkCell linkCell = (GlobalExportLinkCell)values[i];

						Cell cell = table.getCellByPosition(1, rowIndex);
						CalcUtils.applyLink(cell, linkCell.getText(), linkCell.getTarget());
						width = linkCell.getText().length() / 2;
					}

					if(width != -1) {
						contentWidthMap.put(i, width);
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
