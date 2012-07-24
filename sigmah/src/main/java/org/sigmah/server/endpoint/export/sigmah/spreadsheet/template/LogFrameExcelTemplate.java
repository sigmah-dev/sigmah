package org.sigmah.server.endpoint.export.sigmah.spreadsheet.template;

import java.io.OutputStream;
import java.util.List;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.ExcelUtils;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.ExportConstants;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.data.LogFrameExportData;
import org.sigmah.shared.domain.Indicator;
import org.sigmah.shared.domain.logframe.ExpectedResult;
import org.sigmah.shared.domain.logframe.LogFrameActivity;
import org.sigmah.shared.domain.logframe.LogFrameGroup;
import org.sigmah.shared.domain.logframe.Prerequisite;
import org.sigmah.shared.domain.logframe.SpecificObjective;

/*
 * Excel template for log frame
 * 
 * @author sherzod
 */
public class LogFrameExcelTemplate implements ExportTemplate {

	private final LogFrameExportData data;
	private final HSSFWorkbook wb;
	private HSSFRow row = null;
	private HSSFCell cell = null;
	private final HSSFSheet sheet;
	private final ExcelUtils utils;
	private CellRangeAddress region;
	private ExcelUtils.CellTextFormat cellTextFormat;	
	private final float defHeight;
	private final int colWidthDesc = 35;
	private final int colWidthIndicator=25;
	private StringBuilder builder;
	
	public LogFrameExcelTemplate(final LogFrameExportData data) throws Throwable {
		this.data=data;
		wb = new HSSFWorkbook();
		sheet = wb.createSheet();
		utils = new ExcelUtils();
		defHeight=sheet.getDefaultRowHeightInPoints();
		int rowIndex = -1;
		int cellIndex = 0;
		
	 	// formatting sheet
		utils.formatPrinableSheet(sheet);

		// empty row
		utils.putEmptyRow(sheet, ++rowIndex, 8.65f);

		// title
		row = sheet.createRow(++rowIndex);
		row.setHeightInPoints(ExportConstants.HEADER_ROW_HEIGHT);
		cell = row.createCell(++cellIndex);
		cell.setCellValue(data.getLocalizedVersion("logFrame").toUpperCase());
		cell.setCellStyle(utils.getTopicStyle(wb));
		sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex,
				cellIndex, LogFrameExportData.NUMBER_OF_COLS));

		// empty row
		utils.putEmptyRow(sheet, ++rowIndex,ExportConstants.EMPTY_ROW_HEIGHT);

		// info
		putInfoRow(++rowIndex, data.getLocalizedVersion("logFrameActionTitle"), data.getTitleOfAction());
		putInfoRow(++rowIndex, data.getLocalizedVersion("logFrameMainObjective"), data.getMainObjective());

		// empty row
		utils.putEmptyRow(sheet, ++rowIndex,ExportConstants.EMPTY_ROW_HEIGHT);

		// column headers
		row = sheet.createRow(++rowIndex);
		row.setHeightInPoints(ExportConstants.TITLE_ROW_HEIGHT);
		cellIndex = 3;
		putHeader(++cellIndex, data.getLocalizedVersion("logFrameInterventionLogic"));
		putHeader(++cellIndex,  data.getLocalizedVersion("indicators"));
		putHeader(++cellIndex,data.getLocalizedVersion("logFrameMeansOfVerification"));
		putHeader(++cellIndex, data.getLocalizedVersion("logFrameRisksAndAssumptions"));

		// empty row
		utils.putEmptyRow(sheet, ++rowIndex,ExportConstants.EMPTY_ROW_HEIGHT);

		// freeze pane
		sheet.createFreezePane(0, rowIndex);

 		boolean titleIsSet = false;
 		boolean hasElement=false;
		int typeStartRow = rowIndex + 1;		
		
		
		// SO		
		if (data.getEnableSpecificObjectivesGroups()) {
			hasElement=data.getSoMap().keySet().size()>0;
			for (final LogFrameGroup soGroup : data.getSoMap().keySet()) {

				row = sheet.createRow(++rowIndex);
				row.setHeightInPoints(ExportConstants.TITLE_ROW_HEIGHT);

				// type (only once) SO,ER,A
				if (!titleIsSet) {
					putTypeCell(data.getLocalizedVersion("logFrameSpecificObjectives"), 
							data.getLocalizedVersion("logFrameSpecificObjectivesCode"));
					titleIsSet = true;
				}

				// groups
				putGroupCell(rowIndex,data.getLocalizedVersion("logFrameGroup") ,
						data.getLocalizedVersion("logFrameSpecificObjectivesCode"), soGroup.getLabel());
				
				// items per group
				rowIndex = putSOItems(rowIndex,false, data.getSoMap().get(soGroup));			
			}
			// merge type cell
			if(hasElement){
				mergeCell(typeStartRow, rowIndex, 1, 1);				 
			}
		}else{
			hasElement=data.getSoMainList().size()>0;
			if(hasElement){
				row = sheet.createRow(++rowIndex);
				putTypeCell(data.getLocalizedVersion("logFrameSpecificObjectives"), 
						data.getLocalizedVersion("logFrameSpecificObjectivesCode"));
				rowIndex = putSOItems(rowIndex, true,data.getSoMainList());			
				mergeCell(typeStartRow, rowIndex, 1, 1);	
			}
		}

		
		//ER		
		if(data.getEnableExpectedResultsGroups()){
			hasElement=data.getErMap().keySet().size()>0;
			typeStartRow = rowIndex + 1;
			titleIsSet = false;					
			for (final LogFrameGroup erGroup : data.getErMap().keySet()) {

				row = sheet.createRow(++rowIndex);
				row.setHeightInPoints(ExportConstants.TITLE_ROW_HEIGHT);

				// type (only once) SO,ER,A
				if (!titleIsSet) {
					putTypeCell(data.getLocalizedVersion("logFrameExceptedResults"), 
							data.getLocalizedVersion("logFrameExceptedResultsCode"));
					titleIsSet = true;
				}

				// groups
				putGroupCell(rowIndex,data.getLocalizedVersion("logFrameGroup"),
						data.getLocalizedVersion("logFrameExceptedResultsCode"), erGroup.getLabel());
				
				// items per group
				rowIndex = putERItems(rowIndex, false, data.getErMap().get(erGroup));
				 
			}
			// merge type cell
			if(hasElement){
				mergeCell(typeStartRow, rowIndex, 1, 1);	
			}
		}else{
			hasElement=data.getErMainList().size()>0;
			if(hasElement){
				typeStartRow = rowIndex + 1;
				row = sheet.createRow(++rowIndex);
				putTypeCell(data.getLocalizedVersion("logFrameExceptedResults"), 
						data.getLocalizedVersion("logFrameExceptedResultsCode"));
				rowIndex = putERItems(rowIndex, true,data.getErMainList());			 
				mergeCell(typeStartRow, rowIndex, 1, 1);	
			}
		}
		 
		
		//Activities		
		if(data.getEnableActivitiesGroups()){
			hasElement=data.getAcMap().keySet().size()>0;
			titleIsSet = false;
			typeStartRow = rowIndex + 1;
			
			for (final LogFrameGroup aGroup : data.getAcMap().keySet()) {

				row = sheet.createRow(++rowIndex);
				row.setHeightInPoints(ExportConstants.TITLE_ROW_HEIGHT);

				// type (only once) SO,ER,A
				if (!titleIsSet) {
					putTypeCell(data.getLocalizedVersion("logFrameActivities"), 
							data.getLocalizedVersion("logFrameActivitiesCode"));
					titleIsSet = true;
				}

				// groups
				putGroupCell(rowIndex,data.getLocalizedVersion("logFrameGroup"),
						data.getLocalizedVersion("logFrameActivitiesCode"), aGroup.getLabel());
				
				// items per group
				rowIndex =putAcItems(rowIndex, false, data.getAcMap().get(aGroup));				 
			}
			// merge type cell
			if(hasElement){
				mergeCell(typeStartRow, rowIndex, 1, 1);	
			}
		}else{
			hasElement=data.getAcMainList().size()>0;
			if(hasElement){
				typeStartRow = rowIndex + 1;
				row = sheet.createRow(++rowIndex);
				putTypeCell(data.getLocalizedVersion("logFrameActivities"), 
						data.getLocalizedVersion("logFrameActivitiesCode"));
				rowIndex = putAcItems(rowIndex, true,data.getAcMainList());
				mergeCell(typeStartRow, rowIndex, 1, 1);
			}
		}
		
		
		//Prerequisites
		if(data.getEnablePrerequisitesGroups()){
			hasElement=data.getPrMap().keySet().size()>0;
			titleIsSet = false;
			typeStartRow = rowIndex + 1;
			
			for (final LogFrameGroup pGroup : data.getPrMap().keySet()) {

				row = sheet.createRow(++rowIndex);
				row.setHeightInPoints(ExportConstants.TITLE_ROW_HEIGHT);

				// type (only once) SO,ER,A
				if (!titleIsSet) {
					putTypeCell(data.getLocalizedVersion("logFramePrerequisites"), 
							data.getLocalizedVersion("logFramePrerequisitesCode"));
					titleIsSet = true;
				}

				// groups
				putGroupCell(rowIndex, data.getLocalizedVersion("logFrameGroup"),
						data.getLocalizedVersion("logFramePrerequisitesCode"), pGroup.getLabel());
				
				// items per group
				rowIndex=putPrItems(rowIndex, false,data.getPrMap().get(pGroup));
				 
			}
			// merge type cell
			if(hasElement){
				mergeCell(typeStartRow, rowIndex, 1, 1);	
			}
		}else{
			hasElement=data.getPrMainList().size()>0;
			if(hasElement){
				typeStartRow = rowIndex + 1;
				row = sheet.createRow(++rowIndex);
				putTypeCell(data.getLocalizedVersion("logFramePrerequisites"), 
						data.getLocalizedVersion("logFramePrerequisitesCode"));
				rowIndex = putPrItems(rowIndex, true,data.getPrMainList());
				mergeCell(typeStartRow, rowIndex, 1, 1);	
			}
		}
		
 		
		sheet.setColumnWidth(0, 256 * 2);
		sheet.setColumnWidth(1, 256 * 20);
		sheet.setColumnWidth(2, 256 * 12);
		sheet.setColumnWidth(3, 256 * 12);
		sheet.setColumnWidth(4, 256 * colWidthDesc);
		sheet.setColumnWidth(5, 256 * colWidthIndicator);
		sheet.setColumnWidth(6, 256 * colWidthIndicator);
		sheet.setColumnWidth(7, 256 * colWidthDesc);
	}

	private void mergeCell(int startRow,int endRow,int startCol,int endCol){
		region = new CellRangeAddress(startRow, endRow, startCol, endCol);
		sheet.addMergedRegion(utils.getBorderedRegion(region, sheet, wb));
	}
	
	private int putPrItems(int rowIndex,boolean skipFirst,List<Prerequisite> prList){
		int textAreaLines=0;
		for (final Prerequisite p : prList) {


			if(!skipFirst){
				row = sheet.createRow(++rowIndex);				
			}
			skipFirst=false;
			
			builder = new StringBuilder(data.getLocalizedVersion("logFramePrerequisitesCode"));
			builder.append(" ");
			builder.append(p.getCode());
			builder.append(".");
			putCenteredBasicCell(rowIndex, 2, builder.toString());					
		
			cellTextFormat=utils.formatCellText(p.getContent(), 18+2*(colWidthDesc+colWidthIndicator));
			textAreaLines=cellTextFormat.dividedlines;
			putBasicCell(rowIndex, 3,cellTextFormat.formattedText);
				 
			row.setHeightInPoints(textAreaLines*defHeight);
			
			mergeCell(rowIndex, rowIndex, 3, LogFrameExportData.NUMBER_OF_COLS); 				
		}
		return rowIndex;
	}
	
	private int putAcItems(int rowIndex,boolean skipFirst,List<LogFrameActivity> acList){
		int textAreaLines=0;
		for (final LogFrameActivity a : acList) {


			if(!skipFirst){
				row = sheet.createRow(++rowIndex);				
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
			putCenteredBasicCell(rowIndex, 2, builder.toString());
			
			builder = new StringBuilder(data.getLocalizedVersion("logFrameActivitiesCode"));
			builder.append(" ");
			builder.append(data.getFormattedCode(a.getParentExpectedResult().getParentSpecificObjective().getCode()));
			builder.append(a.getParentExpectedResult().getCode());
			builder.append(".");
			builder.append(a.getCode());
			builder.append(".");
			putCenteredBasicCell(rowIndex, 3, builder.toString());
								
			cellTextFormat=utils.formatCellText(a.getTitle(), colWidthDesc);
			textAreaLines=cellTextFormat.dividedlines;
			putBasicCell(rowIndex, 4,cellTextFormat.formattedText);
			
			putBasicCell(rowIndex, 7,null);
			
			row.setHeightInPoints(textAreaLines*defHeight);

			// indicators and their means of verifications
			rowIndex = putIndicators(a.getIndicators(),rowIndex,false,textAreaLines);						
		}
		
		return rowIndex;
	}
	private int putERItems(int rowIndex,boolean skipFirst,List<ExpectedResult> erList){
		int textAreaLines=0;
		for (final ExpectedResult er : erList) {

			if(!skipFirst){
				row = sheet.createRow(++rowIndex);				
			}
			skipFirst=false;
				
			builder = new StringBuilder(data.getLocalizedVersion("logFrameExceptedResultsCode"));
			builder.append(" (");
			builder.append(data.getLocalizedVersion("logFrameSpecificObjectivesCode"));
			builder.append(" ");
			builder.append(data.getFormattedCode(er.getParentSpecificObjective().getCode()));
			builder.append(")");
			putCenteredBasicCell(rowIndex, 2, builder.toString());
			
			builder = new StringBuilder(data.getLocalizedVersion("logFrameExceptedResultsCode"));
			builder.append(" ");
			builder.append(data.getFormattedCode(er.getParentSpecificObjective().getCode()));
			builder.append(er.getCode());
			builder.append(".");
			putCenteredBasicCell(rowIndex, 3, builder.toString());					 
			
			cellTextFormat=utils.formatCellText(er.getInterventionLogic(), colWidthDesc);
			textAreaLines=cellTextFormat.dividedlines;
			putBasicCell(rowIndex, 4,cellTextFormat.formattedText);
			
			cellTextFormat=utils.formatCellText(er.getRisksAndAssumptions(), colWidthDesc);
			textAreaLines=Math.max(textAreaLines, cellTextFormat.dividedlines);
			putBasicCell(rowIndex, 7,cellTextFormat.formattedText);

			row.setHeightInPoints(textAreaLines*defHeight);

			// indicators and their means of verifications
			rowIndex = putIndicators(er.getIndicators(),rowIndex,false,textAreaLines);				
		}
		return rowIndex;
	}
	
	private int putSOItems(int rowIndex,boolean skipFirst,List<SpecificObjective> soList){
		int textAreaLines=0;
 		for (final SpecificObjective so : soList) {
			
			if(!skipFirst){
				row = sheet.createRow(++rowIndex);				
			}
			skipFirst=false;
			
			builder = new StringBuilder(data.getLocalizedVersion("logFrameSpecificObjectivesCode"));
			builder.append(" ");
			builder.append(data.getFormattedCode(so.getCode()));
			putCenteredBasicCell(rowIndex, 2, builder.toString());
			
			cellTextFormat=utils.formatCellText(so.getInterventionLogic(), colWidthDesc);
			textAreaLines=cellTextFormat.dividedlines;
			putBasicCell(rowIndex, 4,cellTextFormat.formattedText);
			
			cellTextFormat=utils.formatCellText(so.getRisksAndAssumptions(), colWidthDesc);
			textAreaLines=Math.max(textAreaLines, cellTextFormat.dividedlines);
			putBasicCell(rowIndex, 7,cellTextFormat.formattedText);

			row.setHeightInPoints(textAreaLines*defHeight);
			
			// indicators and their means of verifications
			rowIndex = putIndicators(so.getIndicators(),rowIndex,true,textAreaLines);				
		}
		return rowIndex;
	}
 
	private int putIndicators(final Set<Indicator> indicators, int rowIndex,boolean mergeCodeCells,int textAreaLines){
		if (indicators.size() > 0) {
			int startIndex = rowIndex;			
			int indiTextLinesSum=0;
			int indiTextLines=0;
			
			for (final Indicator indicator : indicators) {
				if (startIndex != rowIndex) {
					row = sheet.createRow(rowIndex);
					
				}								
				cellTextFormat=utils.formatCellText(
						data.getDetailedIndicatorName(indicator.getId()), 
						colWidthIndicator);				
				indiTextLines=cellTextFormat.dividedlines;
				putBasicCell(rowIndex, 5, cellTextFormat.formattedText);
				
				cellTextFormat=utils.formatCellText(indicator.getSourceOfVerification(), colWidthIndicator);
				indiTextLines=Math.max(indiTextLines, cellTextFormat.dividedlines);				
				putBasicCell(rowIndex, 6,cellTextFormat.formattedText);
				
				indiTextLinesSum+=indiTextLines;
				row.setHeightInPoints(indiTextLines*defHeight);
				rowIndex++;
			}
			rowIndex--;
			
			if(indiTextLinesSum<textAreaLines){
				indiTextLines+=(textAreaLines-indiTextLinesSum);
				row.setHeightInPoints(indiTextLines*defHeight);
			}

			if(mergeCodeCells){
				region = new CellRangeAddress(startIndex, rowIndex, 2, 3);	
				sheet.addMergedRegion(utils.getBorderedRegion(region, sheet, wb));
			}else{
				region = new CellRangeAddress(startIndex, rowIndex, 2, 2);
				sheet.addMergedRegion(utils.getBorderedRegion(region, sheet, wb));
				
				region = new CellRangeAddress(startIndex, rowIndex, 3, 3);
				sheet.addMergedRegion(utils.getBorderedRegion(region, sheet, wb));
			}			
			
			
			region = new CellRangeAddress(startIndex, rowIndex, 4, 4);
			sheet.addMergedRegion(utils.getBorderedRegion(region, sheet, wb));
			
			region = new CellRangeAddress(startIndex, rowIndex, 7, 7);
			sheet.addMergedRegion(utils.getBorderedRegion(region, sheet, wb));

		} else {
			putBasicCell(rowIndex, 5, "");
			putBasicCell(rowIndex, 6, "");
			if(mergeCodeCells){
				region = new CellRangeAddress(rowIndex, rowIndex, 2, 3);
				sheet.addMergedRegion(utils.getBorderedRegion(region,sheet, wb));				
			}else{
				region = new CellRangeAddress(rowIndex, rowIndex, 2, 2);
				sheet.addMergedRegion(utils.getBorderedRegion(region,sheet, wb));
				
				region = new CellRangeAddress(rowIndex, rowIndex, 3, 3);
				sheet.addMergedRegion(utils.getBorderedRegion(region,sheet, wb));
			}
			
			
		}
		
		return rowIndex;
	}
	
	private void putTypeCell(String typeLabel,String code){
		cell = row.createCell(1);
		StringBuilder builder = new StringBuilder(typeLabel);
		builder.append(" (");
		builder.append(code);
		builder.append(")");
		cell.setCellValue(builder.toString());
		cell.setCellStyle(utils.getHeaderStyle(wb));
	}
	
	private void putGroupCell(int rowIndex,String groupType,String code,String groupLabel){
		cell = sheet.getRow(rowIndex).createCell(2);
		StringBuilder builder = new StringBuilder(groupType);
		builder.append(" (");
		builder.append(code);
		builder.append(") - ");
		builder.append(groupLabel);
		cell.setCellValue(builder.toString());
		CellRangeAddress region = new CellRangeAddress(rowIndex,rowIndex, 2,LogFrameExportData.NUMBER_OF_COLS);
		sheet.addMergedRegion(utils.getBorderedRegion(region, sheet, wb));
		cell.setCellStyle(utils.getGroupStyle(wb));
	} 

	private void putBasicCell(int rowIndex, int cellIndex, String text) {
		cell = sheet.getRow(rowIndex).createCell(cellIndex);
		cell.setCellValue(text);
		cell.setCellStyle(utils.getBasicStyle(wb,false));
	}
	
	private void putCenteredBasicCell(int rowIndex, int cellIndex, String text) {
		cell = sheet.getRow(rowIndex).createCell(cellIndex);
		cell.setCellValue(text);
		cell.setCellStyle(utils.getBasicStyle(wb,true));
	}

	private void putHeader(int cellIndex, String header) {
		cell = row.createCell(cellIndex);
		cell.setCellValue(header);
		cell.setCellStyle(utils.getHeaderStyle(wb));
	}

	private void putInfoRow(int rowIndex, String key, String value) {
		int cellIndex = 0;
		row = sheet.createRow(rowIndex);
		row.setHeightInPoints(ExportConstants.TITLE_ROW_HEIGHT);
		cell = row.createCell(++cellIndex);
		cell.setCellValue(key);
		cell.setCellStyle(utils.getInfoStyle(wb, true));

		cell = row.createCell(++cellIndex);
		cell.setCellValue(value);
		cell.setCellStyle(utils.getInfoStyle(wb, false));
		sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex,
				cellIndex, LogFrameExportData.NUMBER_OF_COLS));
	}
		

	@Override
	public void write(OutputStream output) throws Throwable {
		wb.write(output);
	}

}
