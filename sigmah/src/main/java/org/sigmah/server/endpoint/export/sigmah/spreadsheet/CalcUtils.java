package org.sigmah.server.endpoint.export.sigmah.spreadsheet;

import java.util.HashMap;
import java.util.Map;

import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfParagraphProperties;
import org.odftoolkit.odfdom.dom.style.props.OdfStyleProperty;
import org.odftoolkit.odfdom.dom.style.props.OdfTableCellProperties;
import org.odftoolkit.odfdom.dom.style.props.OdfTextProperties;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.style.Border;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.style.StyleTypeDefinitions.CellBordersType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FontStyle;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.CellRange;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;

public class CalcUtils {

	private CalcUtils(){}
	
	private static String coreStyleName=null;
	
	private static Map<OdfStyleProperty,String> styleMap;
	
	static{
		styleMap=new HashMap<OdfStyleProperty, String>();
		
		styleMap.put(OdfTableCellProperties.Border,"0.035cm solid #000000");
		styleMap.put(OdfTableCellProperties.WrapOption, "wrap");
		styleMap.put(OdfTableCellProperties.BackgroundColor, "#ffffff");
		styleMap.put(OdfTableCellProperties.VerticalAlign, "middle");
		styleMap.put(OdfParagraphProperties.TextAlign, "left");
		styleMap.put(OdfParagraphProperties.MarginBottom, "0.15cm");
		styleMap.put(OdfParagraphProperties.MarginTop, "0.15cm");
		styleMap.put(OdfTableCellProperties.PaddingTop, "0.15cm");
		styleMap.put(OdfTableCellProperties.PaddingBottom, "0.15cm");
		styleMap.put(OdfTableCellProperties.PaddingLeft, "0.2cm");
		styleMap.put(OdfTableCellProperties.PaddingRight, "0.2cm");
		styleMap.put(OdfTextProperties.FontWeight, "Regular");
		styleMap.put(OdfTextProperties.FontSize, "10pt");
	}
public static String prepareCoreStyle(final SpreadsheetDocument doc) throws Throwable{
		
		OdfOfficeAutomaticStyles styles = doc.getContentDom().getOrCreateAutomaticStyles();
		OdfStyle style = styles.newStyle(OdfStyleFamily.TableCell);
	
		for(OdfStyleProperty property:styleMap.keySet()){
			style.setProperty(property,styleMap.get(property));
		}		 
		coreStyleName = style.getStyleNameAttribute();
		return coreStyleName;
	}

public static String getTripletElementStyle(final SpreadsheetDocument doc) throws Throwable{
	
	OdfOfficeAutomaticStyles styles = doc.getContentDom().getOrCreateAutomaticStyles();
	OdfStyle style = styles.newStyle(OdfStyleFamily.TableCell);

	for(OdfStyleProperty property:styleMap.keySet()){
		style.setProperty(property,styleMap.get(property));
	}		 
	style.setProperty(OdfTableCellProperties.PaddingLeft, "0.4cm");
 	return style.getStyleNameAttribute();
}

public  static Cell putHeader(final Row row,int cellIndex, String header) {

	Cell cell = row.getCellByIndex(cellIndex);
	cell.setStringValue(header);
	cell.setBorders(CellBordersType.ALL_FOUR, getBlackBorder());
	cell.setCellBackgroundColor(ExportConstants.CALC_COL_GRAY);
	cell.setFont(getBoldFont(10));
	cell.setVerticalAlignment(ExportConstants.ALIGN_VER_MIDDLE);
	cell.setHorizontalAlignment(ExportConstants.ALIGH_HOR_CENTER);
	cell.setTextWrapped(true);
	return cell;
}

  public  static void mergeCell(final Table table,int startCol, int startRow, int endCol, int endRow){
	 CellRange cellRange = table.getCellRangeByPosition(
			startCol,
			startRow, 
			endCol, 
			endRow);
	cellRange.merge();
}
  
  public  static void putGroupCell(final Table table,int colIndex,int rowIndex,String value){
	  	final Cell cell = createBasicCell(table,colIndex, rowIndex,value);
		cell.setCellBackgroundColor(ExportConstants.CALC_COL_ORANGE);
		cell.setFont(getFont(10, false, true));
		cell.setHorizontalAlignment(ExportConstants.ALIGH_HOR_LEFT);	
  }
  
  public  static Cell createBasicCell(final Table table,int colIndex, int rowIndex, String value) {
	  	final Cell cell = table.getCellByPosition(colIndex, rowIndex);
	  	if(value!=null)
	  		cell.setStringValue(value);
		cell.setCellStyleName(coreStyleName);
		
		return cell;
	}

	public static  Border getBlackBorder() {
		return new Border(Color.BLACK, 1,
				StyleTypeDefinitions.SupportedLinearMeasure.PT);
	}

	public static  Font getBoldFont(int size) {
		return getFont(size, true, false);
	}

	public static  Font getFont(int size, boolean bold, boolean italic) {
		FontStyle style = StyleTypeDefinitions.FontStyle.REGULAR;
		if (bold)
			style = StyleTypeDefinitions.FontStyle.BOLD;
		if (italic)
			style = StyleTypeDefinitions.FontStyle.ITALIC;
		return new Font("Arial", style, size, Color.BLACK);
	}

	public static  void putEmptyRow(final Table table,int rowIndex) {
		Row row = table.getRowByIndex(rowIndex);
		row.setHeight(3.8, false);
	}

	public static void applyLink(final Cell cell,String linkName,String target) throws Throwable{
		target=target.replace(" ", "_");
		java.net.URI uri=new java.net.URI("#"+target);
		cell.addParagraph(linkName).applyHyperlink(uri);
	}
	
	public static void putMainTitle(final Table table,int rowIndex,int maxCols,String title){
		Row row = table.getRowByIndex(rowIndex);
		Cell cell = row.getCellByIndex(1);
		cell.setStringValue(title);
		cell.setTextWrapped(true);
		cell.setFont(CalcUtils.getBoldFont(14));
		cell.setVerticalAlignment(ExportConstants.ALIGN_VER_MIDDLE);
		cell.setHorizontalAlignment(ExportConstants.ALIGH_HOR_CENTER);
		CellRange cellRange = table.getCellRangeByPosition(1, rowIndex,
				maxCols, rowIndex);
		cellRange.merge();
		row.setHeight(7, false);
	}
}
