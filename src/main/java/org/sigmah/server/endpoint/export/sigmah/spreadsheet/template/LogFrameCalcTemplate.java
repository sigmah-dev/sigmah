/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.server.endpoint.export.sigmah.spreadsheet.template;

import java.io.OutputStream;
import java.util.List;
import java.util.Set;

import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfParagraphProperties;
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
import org.odftoolkit.simple.style.StyleTypeDefinitions.HorizontalAlignmentType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.VerticalAlignmentType;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.CellRange;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.CalcUtils;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.ExportConstants;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.data.LogFrameExportData;
import org.sigmah.shared.domain.Indicator;
import org.sigmah.shared.domain.logframe.ExpectedResult;
import org.sigmah.shared.domain.logframe.LogFrameActivity;
import org.sigmah.shared.domain.logframe.LogFrameGroup;
import org.sigmah.shared.domain.logframe.Prerequisite;
import org.sigmah.shared.domain.logframe.SpecificObjective;

/*
 * Open document spreadsheet template for log logframe
 * 
 * @author sherzod
 */
public class LogFrameCalcTemplate implements ExportTemplate {

	private final LogFrameExportData data;
	
	private Row row;
	private Cell cell;
 	private CellRange cellRange;
	private final Table table;
	private final SpreadsheetDocument doc;

	private StringBuilder builder;
	private final Color gray = Color.valueOf(ExportConstants.GRAY_10_HEX);
 	private final Color lightOrange = Color.valueOf(ExportConstants.LIGHTORANGE_HEX);
	private String coreCellStyle;

	public LogFrameCalcTemplate(final LogFrameExportData data,final SpreadsheetDocument exDoc) throws Throwable {
		this.data=data;
		if(exDoc==null){
			doc = SpreadsheetDocument.newSpreadsheetDocument();
			table = doc.getSheetByIndex(0);
			table.setTableName(data.getLocalizedVersion("logFrame").replace(" ", "_"));
		}else{
			doc=exDoc;
			table=doc.appendSheet(data.getLocalizedVersion("logFrame").replace(" ", "_"));
		}
		
		setUpCoreStyle();
		
		int rowIndex = -1;
		int cellIndex = 0;
		

		//skip row
		++rowIndex;		 

		// title
		row = table.getRowByIndex(++rowIndex);
		cell = row.getCellByIndex(1);
		cell.setStringValue(data.getLocalizedVersion("logFrame").toUpperCase());
		cell.setTextWrapped(true);
		cell.setFont(getBoldFont(14));
		cell.setVerticalAlignment(ExportConstants.ALIGN_VER_MIDDLE);
		cell.setHorizontalAlignment(ExportConstants.ALIGH_HOR_CENTER);
		cellRange = table.getCellRangeByPosition(1, rowIndex,
				data.getNumbOfCols(), rowIndex);
		cellRange.merge();
		row.setHeight(7, false);

		putEmptyRow(++rowIndex);

		// info		
		putInfoRow(++rowIndex, data.getLocalizedVersion("logFrameActionTitle"), data.getTitleOfAction());
		putInfoRow(++rowIndex, data.getLocalizedVersion("logFrameMainObjective"), data.getMainObjective());

		putEmptyRow(++rowIndex);

		// column headers
		row = table.getRowByIndex(++rowIndex);
		cellIndex = 3;
		putHeader(++cellIndex, data.getLocalizedVersion("logFrameInterventionLogic"));
		putHeader(++cellIndex,  data.getLocalizedVersion("indicators"));
		putHeader(++cellIndex,data.getLocalizedVersion("logFrameMeansOfVerification"));
		putHeader(++cellIndex, data.getLocalizedVersion("logFrameRisksAndAssumptions"));
		row.setHeight(6, false);

		//empty row
		row = table.getRowByIndex(++rowIndex);
		row.setHeight(3.8, false);
		row.getCellByIndex(4).setCellStyleName(null);
		row.getCellByIndex(5).setCellStyleName(null);
		row.getCellByIndex(6).setCellStyleName(null);
		row.getCellByIndex(7).setCellStyleName(null);

		//TODO consider to implement freeze pane
		
	
		boolean titleIsSet = false;
		boolean hasElement=false;
		int typeStartRow = rowIndex + 1;
		
		//SO
		if (data.getEnableSpecificObjectivesGroups()) {
			hasElement=data.getSoMap().keySet().size()>0;
			
			for (final LogFrameGroup soGroup : data.getSoMap().keySet()) {
				++rowIndex;

				// type (only once) SO,ER,A
				if (!titleIsSet) {
					putTypeCell(rowIndex, data.getLocalizedVersion("logFrameSpecificObjectives"), 
							data.getLocalizedVersion("logFrameSpecificObjectivesCode"));					
					titleIsSet = true;
				}

				// groups
				putGroupCell(rowIndex,data.getLocalizedVersion("logFrameGroup") ,
						data.getLocalizedVersion("logFrameSpecificObjectivesCode"), soGroup.getLabel());				
				 
				// items per group
				rowIndex=putSOItems(rowIndex, false,  data.getSoMap().get(soGroup));			 
			}
			// merge type cell
			if(hasElement){
				mergeCell(1, typeStartRow, 1, rowIndex);
			}			
		}else{
			hasElement=data.getSoMainList().size()>0;
			if(hasElement){
				++rowIndex;
				putTypeCell(rowIndex, data.getLocalizedVersion("logFrameSpecificObjectives"), 
					data.getLocalizedVersion("logFrameSpecificObjectivesCode"));
				rowIndex = putSOItems(rowIndex, true,data.getSoMainList());			
				mergeCell(1, typeStartRow, 1, rowIndex);
			}
		}
		
		
		//ER
		if(data.getEnableExpectedResultsGroups()){
			hasElement=data.getErMap().keySet().size()>0;
			 titleIsSet = false;
			typeStartRow = rowIndex + 1;
			
			for (final LogFrameGroup erGroup : data.getErMap().keySet()) {
				++rowIndex;

				// type (only once) SO,ER,A
				if (!titleIsSet) {
					putTypeCell(rowIndex, data.getLocalizedVersion("logFrameExceptedResults"), 
							data.getLocalizedVersion("logFrameExceptedResultsCode"));					
					titleIsSet = true;
				}

				// groups
				putGroupCell(rowIndex,data.getLocalizedVersion("logFrameGroup") ,
						data.getLocalizedVersion("logFrameExceptedResultsCode"), erGroup.getLabel());
								 
				// items per group
				rowIndex=putERItems(rowIndex, false, data.getErMap().get(erGroup));				
			}
			// merge type cell
			if(data.getErMap().keySet().size()>0){
				mergeCell(1, typeStartRow, 1, rowIndex);
			}			
		}else{
			hasElement=data.getErMainList().size()>0;
			if(hasElement){
 				typeStartRow = rowIndex + 1;
				++rowIndex;
				putTypeCell(rowIndex, data.getLocalizedVersion("logFrameExceptedResults"), 
					data.getLocalizedVersion("logFrameExceptedResultsCode"));
				rowIndex = putERItems(rowIndex, true,data.getErMainList());
				mergeCell(1, typeStartRow, 1, rowIndex);
			}
		}
				 
		
		//Activities
		if(data.getEnableActivitiesGroups()){
			hasElement=data.getAcMap().keySet().size()>0;
			 
			titleIsSet = false;
			typeStartRow = rowIndex + 1;
			for (final LogFrameGroup aGroup : data.getAcMap().keySet()) {
				++rowIndex;

				// type (only once) SO,ER,A
				if (!titleIsSet) {
					putTypeCell(rowIndex, data.getLocalizedVersion("logFrameActivities"), 
							data.getLocalizedVersion("logFrameActivitiesCode"));					
					titleIsSet = true;
				}

				// groups
				putGroupCell(rowIndex,data.getLocalizedVersion("logFrameGroup"),
						data.getLocalizedVersion("logFrameActivitiesCode"), aGroup.getLabel());						
				 
				// items per group
				rowIndex=putAcItems(rowIndex, false, data.getAcMap().get(aGroup));
			}
			// merge type cell
			if(data.getAcMap().keySet().size()>0){
				mergeCell(1, typeStartRow, 1, rowIndex);
			}			
		}else{
			hasElement=data.getAcMainList().size()>0;
			if(hasElement){
 				typeStartRow = rowIndex + 1;
				++rowIndex;
				putTypeCell(rowIndex,data.getLocalizedVersion("logFrameActivities"), 
					data.getLocalizedVersion("logFrameActivitiesCode"));
				rowIndex = putAcItems(rowIndex, true,data.getAcMainList());
				mergeCell(1, typeStartRow, 1, rowIndex);
			}
		}
		
		
		//Prerequisites
		if(data.getEnablePrerequisitesGroups()){
			hasElement=data.getPrMap().keySet().size()>0;
			 
			titleIsSet = false;
			typeStartRow = rowIndex + 1;
			for (final LogFrameGroup pGroup : data.getPrMap().keySet()) {
				++rowIndex;

				// type (only once) SO,ER,A
				if (!titleIsSet) {
					putTypeCell(rowIndex, data.getLocalizedVersion("logFramePrerequisites"), 
							data.getLocalizedVersion("logFramePrerequisitesCode"));					
					titleIsSet = true;
				}

				// groups
				putGroupCell(rowIndex,data.getLocalizedVersion("logFrameGroup"),
						data.getLocalizedVersion("logFramePrerequisitesCode"), pGroup.getLabel());
								 
				// items per group
				rowIndex=putPrItems(rowIndex, false, data.getPrMap().get(pGroup));			
			}
			// merge type cell
			if(data.getPrMap().keySet().size()>0){
				mergeCell(1, typeStartRow, 1, rowIndex);
			}			
		}else{
			hasElement=data.getPrMainList().size()>0;
			if(hasElement){
 				typeStartRow = rowIndex + 1;
				++rowIndex;
				putTypeCell(rowIndex,data.getLocalizedVersion("logFramePrerequisites"), 
					data.getLocalizedVersion("logFramePrerequisitesCode"));
				rowIndex = putPrItems(rowIndex, true,data.getPrMainList());
				mergeCell(1, typeStartRow, 1, rowIndex);
			}
		}

		table.getColumnByIndex(0).setWidth(3.8);
		table.getColumnByIndex(1).setWidth(37.3);
		table.getColumnByIndex(2).setWidth(24);
		table.getColumnByIndex(3).setWidth(24);
		table.getColumnByIndex(4).setWidth(68);
		table.getColumnByIndex(5).setWidth(49);
		table.getColumnByIndex(6).setWidth(49);
		table.getColumnByIndex(7).setWidth(68);			
	}
	
	private int putPrItems(int rowIndex,boolean skipFirst,List<Prerequisite> prList){
		for (final Prerequisite p : prList) {
			if(!skipFirst){
				++rowIndex;			
			}
			skipFirst=false;
			builder = new StringBuilder(data.getLocalizedVersion("logFramePrerequisitesCode"));
			builder.append(" ");
			builder.append(p.getCode());
			builder.append(".");				
			cell = createBasicCell(2, rowIndex,builder.toString());
			cell.setHorizontalAlignment(ExportConstants.ALIGH_HOR_CENTER);
			
			 
			cell = createBasicCell(3, rowIndex,p.getContent());
				
			cellRange = table.getCellRangeByPosition(3, rowIndex,
					data.getNumbOfCols(),rowIndex);
			cellRange.merge();	
		}
		return rowIndex;
	}
	
	private int putAcItems(int rowIndex,boolean skipFirst,List<LogFrameActivity> acList)
	throws Throwable{
		for (final LogFrameActivity a : acList) {
			if(!skipFirst){
				++rowIndex;			
			}
			skipFirst=false;
			builder = new StringBuilder(data.getLocalizedVersion("logFrameActivitiesCode"));
			builder.append(" (");
			builder.append(data.getLocalizedVersion("logFrameExceptedResultsCode"));
			builder.append(" ");
			builder.append(data.getFormattedCode(a.getParentExpectedResult().getParentSpecificObjective().getCode()));
			builder.append(a.getParentExpectedResult().getCode());
			builder.append(".");
			builder.append(")");				
			cell = createBasicCell(2, rowIndex,builder.toString());
			cell.setHorizontalAlignment(ExportConstants.ALIGH_HOR_CENTER);
			
			builder = new StringBuilder(data.getLocalizedVersion("logFrameActivitiesCode"));
			builder.append(" ");
			builder.append(data.getFormattedCode(a.getParentExpectedResult().getParentSpecificObjective().getCode()));
			builder.append(a.getParentExpectedResult().getCode());
			builder.append(".");
			builder.append(a.getCode());
			builder.append(".");
			cell = createBasicCell(3, rowIndex,builder.toString());
			cell.setHorizontalAlignment(ExportConstants.ALIGH_HOR_CENTER);
			
			createBasicCell(4, rowIndex, a.getTitle());
			createBasicCell(7, rowIndex,"");

			// indicators and their means of verifications
			rowIndex = putIndicators(a.getIndicators(),rowIndex,false);
		}
		return rowIndex;
	}
	
	private int putERItems(int rowIndex,boolean skipFirst,List<ExpectedResult> erList)
	throws Throwable{
		for (final ExpectedResult er : erList) {
			if(!skipFirst){
				++rowIndex;			
			}
			skipFirst=false;
			
			builder = new StringBuilder(data.getLocalizedVersion("logFrameExceptedResultsCode"));
			builder.append(" (");
			builder.append(data.getLocalizedVersion("logFrameSpecificObjectivesCode"));
			builder.append(" ");
			builder.append(data.getFormattedCode(er.getParentSpecificObjective().getCode()));
			builder.append(")");					
			cell = createBasicCell(2, rowIndex,builder.toString());
			cell.setHorizontalAlignment(ExportConstants.ALIGH_HOR_CENTER);
			
			builder = new StringBuilder(data.getLocalizedVersion("logFrameExceptedResultsCode"));
			builder.append(" ");
			builder.append(data.getFormattedCode(er.getParentSpecificObjective().getCode()));
			builder.append(er.getCode());
			builder.append(".");
			cell = createBasicCell(3, rowIndex,builder.toString());
			cell.setHorizontalAlignment(ExportConstants.ALIGH_HOR_CENTER);
			
			createBasicCell(4, rowIndex, er.getInterventionLogic());
			createBasicCell(7, rowIndex,er.getRisksAndAssumptions());

			// indicators and their means of verifications
			rowIndex = putIndicators(er.getIndicators(),rowIndex,false);
		}
		return rowIndex;
	}
	
	private int putSOItems(int rowIndex,boolean skipFirst,List<SpecificObjective> soList)
	throws Throwable{
		for (final SpecificObjective so :soList) {
			if(!skipFirst){
				++rowIndex;			
			}
			skipFirst=false;			
			
			builder = new StringBuilder(data.getLocalizedVersion("logFrameSpecificObjectivesCode"));
			builder.append(" ");
			builder.append(data.getFormattedCode(so.getCode()));
			
			cell = createBasicCell(2, rowIndex,builder.toString());
			cell.setHorizontalAlignment(ExportConstants.ALIGH_HOR_CENTER);
			createBasicCell(4, rowIndex, so.getInterventionLogic());
			createBasicCell(7, rowIndex,so.getRisksAndAssumptions());

			// indicators and their means of verifications
			rowIndex = putIndicators(so.getIndicators(),rowIndex,true);
		}
		return rowIndex;
	}
	
	private void mergeCell(int startCol, int startRow, int endCol, int endRow){
		cellRange = table.getCellRangeByPosition(
				startCol,
				startRow, 
				endCol, 
				endRow);
		cellRange.merge();
	}
	
	private void setUpCoreStyle() throws Throwable{
		
		OdfOfficeAutomaticStyles styles = doc.getContentDom().getOrCreateAutomaticStyles();
		OdfStyle style = styles.newStyle(OdfStyleFamily.TableCell);
	
		style.setProperty(OdfTableCellProperties.Border,"0.035cm solid #000000");
		style.setProperty(OdfTableCellProperties.WrapOption, "wrap");
		style.setProperty(OdfTableCellProperties.BackgroundColor, "#ffffff");
		style.setProperty(OdfTableCellProperties.VerticalAlign, "middle");
		style.setProperty(OdfParagraphProperties.TextAlign, "left");
		style.setProperty(OdfParagraphProperties.MarginBottom, "0.2cm");
		style.setProperty(OdfParagraphProperties.MarginTop, "0.2cm");
		style.setProperty(OdfTableCellProperties.PaddingTop, "0.2cm");
		style.setProperty(OdfTableCellProperties.PaddingBottom, "0.2cm");
		style.setProperty(OdfTableCellProperties.PaddingLeft, "0.2cm");
		style.setProperty(OdfTextProperties.FontWeight, "Regular");
		style.setProperty(OdfTextProperties.FontSize, "10pt");
		
		coreCellStyle = style.getStyleNameAttribute();
	}
	
	private int putIndicators(final Set<Indicator> indicators, int rowIndex,boolean mergeCodeCells) 
		throws Throwable{
		if (indicators.size() > 0) {
			int startIndex = rowIndex;
			for (final Indicator indicator : indicators) {
				if(data.isIndicatorsSheetExist()){
					cell=createBasicCell(5, rowIndex, null);
					CalcUtils.applyLink(cell, indicator.getName(),ExportConstants.INDICATOR_SHEET_PREFIX+ indicator.getName());
				}else{
					cell = createBasicCell(5, rowIndex, data.getDetailedIndicatorName(indicator.getId()));
				}
				cell = createBasicCell(6, rowIndex, indicator.getSourceOfVerification());
				rowIndex++;
			}
			rowIndex--;

			for (int i = startIndex; i <= rowIndex; i++) {
				cell = cellRange.getCellByPosition(7, i);
				cell.setBorders(CellBordersType.RIGHT, getBorder());
			}

			if(mergeCodeCells){
				cellRange = table.getCellRangeByPosition(2, startIndex, 3,rowIndex);
				cellRange.merge();
			}else{
				cellRange = table.getCellRangeByPosition(2, startIndex, 2,rowIndex);
				cellRange.merge();
				cellRange = table.getCellRangeByPosition(3, startIndex, 3,rowIndex);
				cellRange.merge();				
			}			
			cellRange = table.getCellRangeByPosition(4, startIndex, 4,rowIndex);
			cellRange.merge();
			cellRange = table.getCellRangeByPosition(7, startIndex, 7,rowIndex);
			cellRange.merge();

		} else {
			cell = createBasicCell(5, rowIndex, null);
			cell = createBasicCell(6, rowIndex, null);
			if(mergeCodeCells){
				cellRange = table.getCellRangeByPosition(2, rowIndex, 3,rowIndex);
				cellRange.merge();
			}else{
				cellRange = table.getCellRangeByPosition(2, rowIndex, 2,rowIndex);
				cellRange.merge();
				cellRange = table.getCellRangeByPosition(3, rowIndex, 3,rowIndex);
				cellRange.merge();				
			}	

		}
		
		return rowIndex;
	}
	private void putTypeCell(int rowIndex,String label,String code){
		StringBuilder builder = new StringBuilder(label);
		builder.append(" (");
		builder.append(code);
		builder.append(")");
		
		cell = createBasicCell(1, rowIndex,builder.toString());
		cell.setCellBackgroundColor(gray);
		cell.setFont(getBoldFont(10));
		cell.setHorizontalAlignment(ExportConstants.ALIGH_HOR_CENTER);
	}
	
	private void putGroupCell(int rowIndex,String groupType,String code,String groupLabel){
		StringBuilder builder = new StringBuilder(groupType);
		builder.append(" (");
		builder.append(code);
		builder.append(") - ");
		builder.append(groupLabel);
		
		cell = createBasicCell(2, rowIndex, builder.toString());
		cell.setCellBackgroundColor(lightOrange);
		cell.setFont(getFont(10, false, true));
		cell.setHorizontalAlignment(ExportConstants.ALIGH_HOR_LEFT);
		cellRange = table.getCellRangeByPosition(2, rowIndex,
				data.getNumbOfCols(), rowIndex);
		cellRange.merge();
	}
	
	private Cell createBasicCell(int colIndex, int rowIndex, String value) {
		cell = table.getCellByPosition(colIndex, rowIndex);
		cell.setStringValue(value);
		cell.setCellStyleName(coreCellStyle);
		return cell;
	}
 

	private void putHeader(int cellIndex, String header) {

		cell = row.getCellByIndex(cellIndex);
		cell.setStringValue(header);
		cell.setBorders(CellBordersType.ALL_FOUR, getBorder());
		cell.setCellBackgroundColor(gray);
		cell.setFont(getBoldFont(10));
		cell.setVerticalAlignment(ExportConstants.ALIGN_VER_MIDDLE);
		cell.setHorizontalAlignment(ExportConstants.ALIGH_HOR_CENTER);
		cell.setTextWrapped(true);
	}

	private Border getBorder() {
		return new Border(Color.BLACK, 1,
				StyleTypeDefinitions.SupportedLinearMeasure.PT);
	}

	private void putEmptyRow(int rowIndex) {
		row = table.getRowByIndex(rowIndex);
		row.setHeight(3.8, false);
	}

	private void putInfoRow(int rowIndex, String key, String value) {
		String space = " ";
		row = table.getRowByIndex(rowIndex);
		cell = row.getCellByIndex(1);
		cell.setStringValue(space + key);
		cell.setFont(getBoldFont(11));
		cell.setTextWrapped(true);
		cell.setVerticalAlignment(ExportConstants.ALIGN_VER_MIDDLE);
		cell.setHorizontalAlignment(ExportConstants.ALIGH_HOR_LEFT);

		cell = row.getCellByIndex(2);
		if(value!=null)
			cell.setStringValue(space + value);
		cell.setFont(getRegFont(11));
		cell.setVerticalAlignment(ExportConstants.ALIGN_VER_MIDDLE);
		cell.setHorizontalAlignment(ExportConstants.ALIGH_HOR_LEFT);
		cellRange = table.getCellRangeByPosition(2, rowIndex,
				data.getNumbOfCols(), rowIndex);
		cellRange.merge();
		row.setHeight(6, false);
	}

	private Font getFont(int size, boolean bold, boolean italic) {
		FontStyle style = StyleTypeDefinitions.FontStyle.REGULAR;
		if (bold)
			style = StyleTypeDefinitions.FontStyle.BOLD;
		if (italic)
			style = StyleTypeDefinitions.FontStyle.ITALIC;
		return new Font("Arial", style, size, Color.BLACK);
	}

	private Font getBoldFont(int size) {
		return getFont(size, true, false);
	}

	private Font getRegFont(int size) {
		return getFont(size, false, false);
	}

	@Override
	public void write(OutputStream output) throws Throwable {
		doc.save(output);
		doc.close();
	}

}
