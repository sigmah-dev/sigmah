/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.server.endpoint.export.sigmah.spreadsheet;

import java.util.Date;

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

/*
 * MS Excel specific common functions 
 * 
 * @author sherzod
 */


public class ExcelUtils {
	
	private final HSSFWorkbook wb;
	private HSSFRow row = null;
	private HSSFCell cell = null;
	private final CreationHelper createHelper;
	private final DataFormat numberFormat;
 	
	public ExcelUtils(final HSSFWorkbook wb){
		this.wb=wb;
		createHelper = wb.getCreationHelper();
		numberFormat = wb.createDataFormat();
	}
	
	
	public int calculateLineCount(String text,int cellLength){
		if(text==null) return 1;
		int lineCount=text.length()/cellLength;		
		return ++lineCount;
	}
	
	public HSSFCell putBorderedBasicCell(HSSFSheet sheet,int rowIndex, int cellIndex, Object value) {
		cell = sheet.getRow(rowIndex).createCell(cellIndex);
		cell.setCellStyle(getBoderedBasicStyle(wb));
 		
		if(value==null){
			cell.setCellValue("");
		}else if(value instanceof String){
			cell.setCellValue((String)value);
		}else if(value instanceof Double){
			Double d=(Double)value;
			cell.setCellValue(d.doubleValue());
			cell.getCellStyle().setDataFormat(numberFormat.getFormat("0.00"));
		}else if(value instanceof Long){
			Long l=(Long)value;
			cell.setCellValue(l.doubleValue());
			
			cell.getCellStyle().setDataFormat(numberFormat.getFormat("#"));
		}else{ //date  
			cell.setCellValue((Date)value);
			cell.getCellStyle().setDataFormat(
			        createHelper.createDataFormat().getFormat(ExportConstants.DATE_FORMAT_PATTERN));
 		}
		cell.getCellStyle().setAlignment(CellStyle.ALIGN_LEFT);
		return cell;
	}
 
	
	public HSSFCell putHeader(HSSFRow row,int cellIndex, String header) {
		cell = row.createCell(cellIndex);
		cell.setCellValue(header);
		cell.setCellStyle(getHeaderStyle(wb));
		return cell;
	}
	
	public HSSFCell putGlobalExportHeader(HSSFRow row,int cellIndex, String header) {
		cell = row.createCell(cellIndex);
		cell.setCellValue(header);
		cell.setCellStyle(getGlobalExportHeaderStyle(wb));
		return cell;
	}
	
	public CellStyle getGlobalExportHeaderStyle(HSSFWorkbook wb) {
		CellStyle style = createBorderedStyle(wb);
 		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

		HSSFPalette palette = wb.getCustomPalette();
		palette.setColorAtIndex(HSSFColor.GREY_25_PERCENT.index,
				ExportConstants.GRAY_5_RGB[0],
				ExportConstants.GRAY_5_RGB[1],
				ExportConstants.GRAY_5_RGB[2]);
		style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFont(getItalicFont(wb, (short) 10));
		style.setWrapText(true);
		style.setIndention((short) 1);
		return style;
	}
  
	public void putMainTitle(final HSSFSheet sheet,int rowIndex,String text,int maxCols){		
		// title
		row = sheet.createRow(rowIndex);
		row.setHeightInPoints(ExportConstants.HEADER_ROW_HEIGHT);
		cell = row.createCell(1);
		cell.setCellValue(text);
		cell.setCellStyle(getTopicStyle(wb));
		sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex,
				1, maxCols));
	}
	
	public void putInfoRow(final HSSFSheet sheet,int rowIndex, String key, String value,int maxCols) {
		int cellIndex = 0;
		row = sheet.createRow(rowIndex);
		row.setHeightInPoints(ExportConstants.TITLE_ROW_HEIGHT);
		cell = row.createCell(++cellIndex);
		cell.setCellValue(key);
		cell.setCellStyle(getInfoStyle(wb, true));

		cell = row.createCell(++cellIndex);
		cell.setCellValue(value);
		cell.setCellStyle(getInfoStyle(wb, false));
		sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex,
				cellIndex, maxCols));
	}
  
	public void putEmptyRow(HSSFSheet sheet, int index, float height) {
		sheet.createRow(index).setHeightInPoints(height);
	}

	//TODO when same method implemented for calc this method
	// can be used
	public void formatPrinableSheet(HSSFSheet sheet) {

		// turn off gridlines
		/*sheet.setDisplayGridlines(false);
		sheet.setPrintGridlines(false);
		sheet.setFitToPage(true);
		sheet.setHorizontallyCenter(true);
		PrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setLandscape(true);

		// the following three statements are required only for HSSF
		sheet.setAutobreaks(true);
		printSetup.setFitHeight((short) 1);
		printSetup.setFitWidth((short) 1); */
	}

	public CellStyle getTopicStyle(HSSFWorkbook wb) {
		CellStyle style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFont(getBoldFont(wb, (short) 14));
		return style;
	}

	public CellStyle getHeaderStyle(HSSFWorkbook wb) {
		CellStyle style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

		HSSFPalette palette = wb.getCustomPalette();
		palette.setColorAtIndex(HSSFColor.GREY_25_PERCENT.index,
				ExportConstants.GRAY_10_RGB[0],
				ExportConstants.GRAY_10_RGB[1],
				ExportConstants.GRAY_10_RGB[2]);
		style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFont(getBoldFont(wb, (short) 10));
		style.setWrapText(true);
		return style;
	}

	public CellStyle getGroupStyle(HSSFWorkbook wb) {
		CellStyle style = createBorderedStyle(wb);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

		HSSFPalette palette = wb.getCustomPalette();
		palette.setColorAtIndex(HSSFColor.BROWN.index,
				ExportConstants.LIGHTORANGE_RGB[0], 
				ExportConstants.LIGHTORANGE_RGB[1],
				ExportConstants.LIGHTORANGE_RGB[2]);
		
		style.setFillForegroundColor(HSSFColor.BROWN.index);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFont(getItalicFont(wb, (short) 10));
		style.setWrapText(true);
		return style;
	}

	public Font getBoldFont(Workbook wb, short size) {
		Font font = wb.createFont();
		font.setFontHeightInPoints(size);
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		return font;
	}

	public Font getItalicFont(Workbook wb, short size) {
		Font font = wb.createFont();
		font.setFontHeightInPoints(size);
		font.setItalic(true);
		return font;
	}

	public CellStyle getInfoStyle(Workbook wb, boolean bold) {
		Font font = getBoldFont(wb, (short) 11);
		if (!bold)
			font.setBoldweight(Font.BOLDWEIGHT_NORMAL);
		CellStyle style = wb.createCellStyle();
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFont(font);
		style.setIndention((short) 1);
		style.setWrapText(true);
		return style;
	}

	public CellStyle getBoderedBasicStyle(Workbook wb) {
		CellStyle style = createBorderedStyle(wb);
		style.setIndention((short) 1);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setWrapText(true);
		return style;
	}
 
	
	public void createLinkCell(HSSFCell cell, String value, 
			String target,
			boolean bordered) {
		cell.setCellValue(value);

		CellStyle style = wb.createCellStyle();
		if (bordered)
			style=createBorderedStyle(wb);
		Font hlink_font = wb.createFont();
		hlink_font.setUnderline(Font.U_SINGLE);
		hlink_font.setColor(IndexedColors.BLUE.getIndex());
		style.setFont(hlink_font);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setIndention((short) 1);
		style.setWrapText(true);

		HSSFHyperlink link = new HSSFHyperlink(HSSFHyperlink.LINK_DOCUMENT);
		link.setAddress("'" + normalizeAsLink(target) + "'!A1");
		cell.setHyperlink(link);
   cell.setCellStyle(style);
	}
	
	public String normalizeAsLink(String linkName){
		if(linkName.length()>25){
			linkName = linkName.substring(0, 25);
		}
		return linkName;
	}

	public CellRangeAddress getBorderedRegion(CellRangeAddress region,
			HSSFSheet sheet, HSSFWorkbook wb) {
		final short solid = CellStyle.BORDER_THIN;
		HSSFRegionUtil.setBorderBottom(solid, region, sheet, wb);
		HSSFRegionUtil.setBorderTop(solid, region, sheet, wb);
		HSSFRegionUtil.setBorderLeft(solid, region, sheet, wb);
		HSSFRegionUtil.setBorderRight(solid, region, sheet, wb);
		HSSFRegionUtil.setBottomBorderColor(IndexedColors.BLACK.getIndex(),
				region, sheet, wb);
		HSSFRegionUtil.setTopBorderColor(IndexedColors.BLACK.getIndex(),
				region, sheet, wb);
		HSSFRegionUtil.setLeftBorderColor(IndexedColors.BLACK.getIndex(),
				region, sheet, wb);
		HSSFRegionUtil.setRightBorderColor(IndexedColors.BLACK.getIndex(),
				region, sheet, wb);
		return region;
	}

	public CellStyle createBorderedStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
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
