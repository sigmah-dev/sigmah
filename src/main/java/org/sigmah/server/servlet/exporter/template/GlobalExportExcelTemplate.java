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

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.sigmah.server.servlet.exporter.data.GlobalExportData;
import org.sigmah.server.servlet.exporter.data.cells.ExportDataCell;
import org.sigmah.server.servlet.exporter.data.cells.ExportLinkCell;
import org.sigmah.server.servlet.exporter.data.cells.ExportStringCell;
import org.sigmah.server.servlet.exporter.utils.ExcelUtils;
import org.sigmah.server.servlet.exporter.utils.ExportConstants;

/**
 * Template to export project models and their project to a excel document
 * 
 * @author sherzod (v1.3)
 */
public class GlobalExportExcelTemplate implements ExportTemplate {

	private final HSSFWorkbook wb;

	public GlobalExportExcelTemplate(final GlobalExportData data) {
		this.wb = new HSSFWorkbook();
		final ExcelUtils utils = new ExcelUtils(wb);
		final float defHeight = ExportConstants.TITLE_ROW_HEIGHT;
		int rowIndex = -1;
		HSSFSheet sheet;
		HSSFRow row;
		int defaultWidth = 20;

		final Map<Integer, Integer> headerWidthMap = new HashMap<Integer, Integer>();
		final Map<Integer, Integer> contentWidthMap = new HashMap<Integer, Integer>();

		for (final String pModelName : data.getExportData().keySet()) {
			List<ExportDataCell[]> dataList = data.getExportData().get(pModelName);
			sheet = wb.createSheet(pModelName);
			rowIndex = -1;

			headerWidthMap.clear();
			contentWidthMap.clear();

			// titles
			final ExportDataCell[] header = dataList.get(0);
			row = sheet.createRow(++rowIndex);
			for (int i = 0; i < header.length; i++) {
				int width = -1;
				if (header[i] instanceof ExportStringCell) {
					ExportStringCell cell = (ExportStringCell)header[i];
					utils.putGlobalExportHeader(row, i, cell.getText());
					if(cell.getText() != null) {
						width = cell.getText().length() / 2;
					}
				} else if (header[i] instanceof ExportLinkCell) {
					// no links on headers for the moment
					ExportLinkCell cell = (ExportLinkCell)header[i];
					utils.putGlobalExportHeader(row, i, cell.getText());
					if(cell.getText() != null) {
						width = cell.getText().length() / 2;
					}
				}
				if (width != -1) {
					headerWidthMap.put(i, width);
				}

			}
			row.setHeightInPoints(2 * defHeight);

			// values
			for (int j = 1; j < dataList.size(); j++) {
				row = sheet.createRow(++rowIndex);
				final ExportDataCell[] values = dataList.get(j);
				int devider = 2;
				for (int i = 0; i < header.length; i++) {

					int width = -1;

					if(values[i] instanceof ExportStringCell) {

						String text = ((ExportStringCell)values[i]).getText();

						utils.putBorderedBasicCell(sheet, rowIndex, i, text);

						if (text != null) {
							String parts[] = text.split("\n");
							if (parts.length > devider) {
								devider = parts.length;
							}

							int currentWidth = text.length() / devider;
							Integer oldWidth = contentWidthMap.get(i);
							if (oldWidth != null) {
								currentWidth = Math.max(oldWidth, currentWidth);
							}
							width = currentWidth;
						}
					} else if (values[i] instanceof ExportLinkCell) {
						ExportLinkCell linkCell = (ExportLinkCell)values[i];
						utils.createLinkCell(row.createCell(i), linkCell.getText(), linkCell.getTarget(), true);
						width = linkCell.getText().length() / 2;
					}

					if(width != -1) {
						contentWidthMap.put(i, width);
					}
				}
				row.setHeightInPoints(devider * defHeight);
			}

			// set width
			for (Integer i : headerWidthMap.keySet()) {
				Integer width = defaultWidth;
				if (headerWidthMap.get(i) != null) {
					width = headerWidthMap.get(i);
				}
				if (contentWidthMap.get(i) != null) {
					width = Math.max(contentWidthMap.get(i), width);
				}
				sheet.setColumnWidth(i, 256 * (width + 15));
			}
		}

	}

	@Override
	public void write(OutputStream output) throws Throwable {
		wb.write(output);
	}

}
