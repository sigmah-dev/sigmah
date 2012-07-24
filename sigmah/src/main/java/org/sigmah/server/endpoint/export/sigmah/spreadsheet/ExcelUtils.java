package org.sigmah.server.endpoint.export.sigmah.spreadsheet;

import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.HSSFRegionUtil;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.google.inject.Singleton;

/*
 * MS Excel specific common functions 
 * 
 * @author sherzod
 */
public class ExcelUtils {
	
	public static class CellTextFormat {
		public final String formattedText;
		public final int dividedlines;

		CellTextFormat(String formattedText, int devidedlines) {
			this.formattedText = formattedText;
			this.dividedlines = devidedlines;
		}
	}

	public CellTextFormat formatCellText(String text, int maxCellCharCount) {
		StringBuilder builder = new StringBuilder();
		int rows = 0;
		if(text!=null && text.length()>0){
			char[] chars = text.toCharArray();
			int i = 0;		
			int lastIndex = 0;
			StringBuilder lastPart = null;
			for (char ch : chars) {
				lastPart = new StringBuilder();
				boolean devidedWord = false;
				if (++i == maxCellCharCount) {
					lastIndex = builder.length() - 1;
					// reverse trip
					lastPart = new StringBuilder();
					boolean spaceFound = false;
					for (int j = 0; j < 10; j++) {
						char c = builder.charAt(lastIndex - j);
						if (c == ' ') {
							builder = builder.delete(lastIndex - j, lastIndex + 1);
							spaceFound = true;
							break;
						}
						lastPart.append(c);
					}
	
					lastPart = lastPart.reverse();
					if (!spaceFound) {
						lastPart = new StringBuilder();
						devidedWord = true;
					}
					i = lastPart.length() + 1;
					builder.append("\n");
					rows++;
				}
				builder.append(lastPart);
				if (devidedWord && ch == ' ')
					continue;
				builder.append(ch);
			}
		}
		return new CellTextFormat(builder.toString(), ++rows);
	}

	public void putEmptyRow(HSSFSheet sheet, int index, float height) {
		sheet.createRow(index).setHeightInPoints(height);
	}

	public void formatPrinableSheet(HSSFSheet sheet) {

		// turn off gridlines
		sheet.setDisplayGridlines(false);
		sheet.setPrintGridlines(false);
		sheet.setFitToPage(true);
		sheet.setHorizontallyCenter(true);
		PrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setLandscape(true);

		// the following three statements are required only for HSSF
		sheet.setAutobreaks(true);
		printSetup.setFitHeight((short) 1);
		printSetup.setFitWidth((short) 1);
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

	public CellStyle getBasicStyle(Workbook wb, boolean isCentered) {
		CellStyle style = createBorderedStyle(wb);
		if (isCentered) {
			style.setAlignment(CellStyle.ALIGN_CENTER);
		} else {
			style.setIndention((short) 1);
		}
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setWrapText(true);
		return style;
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
