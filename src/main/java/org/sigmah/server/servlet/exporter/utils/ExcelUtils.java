package org.sigmah.server.servlet.exporter.utils;

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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.HSSFRegionUtil;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * MS Excel specific common functions
 * 
 * @author sherzod (v1.3)
 */
public class ExcelUtils {

	private final HSSFWorkbook wb;
	private final CreationHelper createHelper;
	private final DataFormat numberFormat;
	
	private final CellStyle borderedBasicStyle;
	private final CellStyle borderedDoubleStyle;
	private final CellStyle borderedLongStyle;
	private final CellStyle borderedDateStyle;
	
	private final CellStyle headerStyle;
	
	private final CellStyle globalExportHeaderStyle;
	
	private final CellStyle topicStyle;
	
	private final CellStyle infoStyle;
	private final CellStyle boldInfoStyle;
	
	private final CellStyle linkStyle;
	private final CellStyle borderedLinkStyle;
	
	private final CellStyle groupStyle;
	
	private final Map<String, Font> fonts;

	public ExcelUtils(final HSSFWorkbook wb) {
		this.wb = wb;
		createHelper = wb.getCreationHelper();
		numberFormat = wb.createDataFormat();
		fonts = new HashMap<>();
		
		// ---------------------------------------------------------------------
		// Styles
		// ---------------------------------------------------------------------
		
		borderedBasicStyle = createBorderedBasicStyle(wb);
		
		borderedDoubleStyle = createBorderedBasicStyle(wb);
		borderedDoubleStyle.setDataFormat(numberFormat.getFormat("0.00"));
		
		borderedLongStyle = createBorderedBasicStyle(wb);
		borderedLongStyle.setDataFormat(numberFormat.getFormat("#"));
		
		borderedDateStyle = createBorderedBasicStyle(wb);
		borderedDateStyle.setDataFormat(createHelper.createDataFormat().getFormat(ExportConstants.DATE_FORMAT_PATTERN));
		
		headerStyle = createHeaderStyle(wb);
		
		globalExportHeaderStyle = createGlobalExportHeaderStyle(wb);
		
		topicStyle = createTopicStyle(wb);
		
		infoStyle = createInfoStyle(wb, false);
		boldInfoStyle = createInfoStyle(wb, true);
		
		groupStyle = createGroupStyle(wb);
		
		linkStyle = createLinkStyle(false);
		borderedLinkStyle = createLinkStyle(true);
	}

	public int calculateLineCount(String text, int cellLength) {
		if (text == null)
			return 1;
		int lineCount = text.length() / cellLength;
		return ++lineCount;
	}

	public HSSFCell putBorderedBasicCell(HSSFSheet sheet, int rowIndex, int cellIndex, Object value) {
		final HSSFCell cell = sheet.getRow(rowIndex).createCell(cellIndex);
		cell.setCellStyle(borderedBasicStyle);

		if (value instanceof String) {
			cell.setCellValue((String) value);
		}
		else if (value instanceof Double) {
			final double doubleValue = (Double) value;
			cell.setCellValue(doubleValue);
			cell.setCellStyle(borderedDoubleStyle);
		}
		else if (value instanceof Long) {
			final long longValue = (Long) value;
			cell.setCellValue(longValue);
			cell.setCellStyle(borderedLongStyle);
		}
		else if (value instanceof Date) {
			cell.setCellValue((Date) value);
			cell.setCellStyle(borderedDateStyle);
		}
		else {
			cell.setCellValue("");
		}
		return cell;
	}

	public HSSFCell putHeader(HSSFRow row, int cellIndex, String header) {
		final HSSFCell cell = row.createCell(cellIndex);
		cell.setCellValue(header);
		cell.setCellStyle(headerStyle);
		return cell;
	}

	public HSSFCell putGlobalExportHeader(HSSFRow row, int cellIndex, String header) {
		final HSSFCell cell = row.createCell(cellIndex);
		cell.setCellValue(header);
		cell.setCellStyle(globalExportHeaderStyle);
		return cell;
	}

	private CellStyle createGlobalExportHeaderStyle(HSSFWorkbook wb) {
		final CellStyle style = createBorderedStyle(wb);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

		final HSSFPalette palette = wb.getCustomPalette();
		palette.setColorAtIndex(HSSFColor.GREY_25_PERCENT.index, ExportConstants.GRAY_5_RGB[0], ExportConstants.GRAY_5_RGB[1], ExportConstants.GRAY_5_RGB[2]);
		style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFont(getItalicFont(wb, (short) 10));
		style.setWrapText(true);
		style.setIndention((short) 1);
		return style;
	}

	public void putMainTitle(final HSSFSheet sheet, int rowIndex, String text, int maxCols) {
		final HSSFRow row = sheet.createRow(rowIndex);
		row.setHeightInPoints(ExportConstants.HEADER_ROW_HEIGHT);
		
		final HSSFCell cell = row.createCell(1);
		cell.setCellValue(text);
		cell.setCellStyle(topicStyle);
		
		sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 1, maxCols));
	}

	public void putInfoRow(final HSSFSheet sheet, int rowIndex, String key, String value, int maxCols) {
		int cellIndex = 0;
		final HSSFRow row = sheet.createRow(rowIndex);
		row.setHeightInPoints(ExportConstants.TITLE_ROW_HEIGHT);
		
		final HSSFCell keyCell = row.createCell(++cellIndex);
		keyCell.setCellValue(key);
		keyCell.setCellStyle(boldInfoStyle);

		final HSSFCell valueCell = row.createCell(++cellIndex);
		valueCell.setCellValue(value);
		valueCell.setCellStyle(infoStyle);
		
		sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, cellIndex, maxCols));
	}

	public void putEmptyRow(HSSFSheet sheet, int index, float height) {
		sheet.createRow(index).setHeightInPoints(height);
	}

	private CellStyle createTopicStyle(HSSFWorkbook wb) {
		final CellStyle style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFont(getBoldFont(wb, (short) 14));
		return style;
	}

	private CellStyle createHeaderStyle(HSSFWorkbook wb) {
		final CellStyle style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

		HSSFPalette palette = wb.getCustomPalette();
		palette.setColorAtIndex(HSSFColor.GREY_25_PERCENT.index, ExportConstants.GRAY_10_RGB[0], ExportConstants.GRAY_10_RGB[1], ExportConstants.GRAY_10_RGB[2]);
		style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFont(getBoldFont(wb, (short) 10));
		style.setWrapText(true);
		return style;
	}

	public CellStyle getGroupStyle(HSSFWorkbook wb) {
		return groupStyle;
	}

	private CellStyle createGroupStyle(HSSFWorkbook wb) {
		final CellStyle style = createBorderedStyle(wb);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

		HSSFPalette palette = wb.getCustomPalette();
		palette.setColorAtIndex(HSSFColor.BROWN.index, ExportConstants.LIGHTORANGE_RGB[0], ExportConstants.LIGHTORANGE_RGB[1], ExportConstants.LIGHTORANGE_RGB[2]);

		style.setFillForegroundColor(HSSFColor.BROWN.index);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFont(getItalicFont(wb, (short) 10));
		style.setWrapText(true);
		return style;
	}

	public Font getBoldFont(Workbook wb, short size) {
		final String key = "bold" + size;
		
		Font font = fonts.get(key);
		if (font == null) {
			font = wb.createFont();
			font.setFontHeightInPoints(size);
			font.setBoldweight(Font.BOLDWEIGHT_BOLD);
			
			fonts.put(key, font);
		}
		return font;
	}

	public Font getItalicFont(Workbook wb, short size) {
		final String key = "italic" + size;
		
		Font font = fonts.get(key);
		if (font == null) {
			font = wb.createFont();
			font.setFontHeightInPoints(size);
			font.setItalic(true);
			
			fonts.put(key, font);
		}
		return font;
	}
	
	private CellStyle createInfoStyle(Workbook wb, boolean bold) {
		final Font font = getBoldFont(wb, (short) 11);
		if (!bold) {
			font.setBoldweight(Font.BOLDWEIGHT_NORMAL);
		}
		final CellStyle style = wb.createCellStyle();
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFont(font);
		style.setIndention((short) 1);
		style.setWrapText(true);
		return style;
	}

	private CellStyle createBorderedBasicStyle(Workbook wb) {
		final CellStyle style = createBorderedStyle(wb);
		style.setIndention((short) 1);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setWrapText(true);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		return style;
	}

	public void createLinkCell(HSSFCell cell, String value, String target, boolean bordered) {
		cell.setCellValue(value);

		final HSSFHyperlink link = new HSSFHyperlink(HSSFHyperlink.LINK_DOCUMENT);
		link.setAddress("'" + normalizeAsLink(target) + "'!A1");
		cell.setHyperlink(link);
		
		if (bordered) {
			cell.setCellStyle(borderedLinkStyle);
		} else {
			cell.setCellStyle(linkStyle);
		}
	}
	
	private CellStyle createLinkStyle(boolean bordered) {
		final Font hlinkFont = wb.createFont();
		hlinkFont.setUnderline(Font.U_SINGLE);
		hlinkFont.setColor(IndexedColors.BLUE.getIndex());
		
		final CellStyle style;
		if (bordered) {
			style = createBorderedStyle(wb);
		} else {
			style = wb.createCellStyle();
		}
		
		style.setFont(hlinkFont);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setIndention((short) 1);
		style.setWrapText(true);
		
		return style;
	}

	/**
	 * Escape characters which can't be included in a sheet's name.
	 * 
	 * @param linkName
	 *			Name of a sheet.
	 * @return The normalized name.
	 */
	public String normalizeAsLink(String linkName) {
		linkName = linkName.replaceAll("('|\\?|\\/|\\[|\\]|\\:)", "_");
		linkName = linkName.replace("\\", "_");
		linkName = linkName.replace("*", "_");
		if (linkName.length() > 25) {
			linkName = linkName.substring(0, 25);
		}
		return linkName;
	}

	public CellRangeAddress getBorderedRegion(CellRangeAddress region, HSSFSheet sheet, HSSFWorkbook wb) {
		final short solid = CellStyle.BORDER_THIN;
		HSSFRegionUtil.setBorderBottom(solid, region, sheet, wb);
		HSSFRegionUtil.setBorderTop(solid, region, sheet, wb);
		HSSFRegionUtil.setBorderLeft(solid, region, sheet, wb);
		HSSFRegionUtil.setBorderRight(solid, region, sheet, wb);
		HSSFRegionUtil.setBottomBorderColor(IndexedColors.BLACK.getIndex(), region, sheet, wb);
		HSSFRegionUtil.setTopBorderColor(IndexedColors.BLACK.getIndex(), region, sheet, wb);
		HSSFRegionUtil.setLeftBorderColor(IndexedColors.BLACK.getIndex(), region, sheet, wb);
		HSSFRegionUtil.setRightBorderColor(IndexedColors.BLACK.getIndex(), region, sheet, wb);
		return region;
	}

	private CellStyle createBorderedStyle(Workbook wb) {
		final CellStyle style = wb.createCellStyle();
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		return style;
	}

}
