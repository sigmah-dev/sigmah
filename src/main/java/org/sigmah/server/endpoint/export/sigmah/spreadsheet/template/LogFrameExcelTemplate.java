/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.server.endpoint.export.sigmah.spreadsheet.template;

import java.io.OutputStream;
import java.util.List;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
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
	private final float defHeight=ExportConstants.TITLE_ROW_HEIGHT; 
	private final int colWidthDesc = 35;
	private final int colWidthIndicator=25;
	private StringBuilder builder;
	
	public LogFrameExcelTemplate(final LogFrameExportData data) throws Throwable {
		this(data,new HSSFWorkbook());
	}
	
	public LogFrameExcelTemplate(final LogFrameExportData data,final HSSFWorkbook wb) throws Throwable {
		this.wb=wb;
		this.data=data;
 		sheet = wb.createSheet(data.getLocalizedVersion("logFrame"));
		utils = new ExcelUtils(wb);
 		int rowIndex = -1;
		int cellIndex = 0;
	 
		// empty row
		utils.putEmptyRow(sheet, ++rowIndex, 8.65f);
		
		//title
		utils.putMainTitle(sheet,++rowIndex,
				data.getLocalizedVersion("logFrame").toUpperCase(),data.getNumbOfCols());
 
		// empty row
		utils.putEmptyRow(sheet, ++rowIndex,ExportConstants.EMPTY_ROW_HEIGHT);

		// info
		utils.putInfoRow(sheet,++rowIndex, 
				data.getLocalizedVersion("logFrameActionTitle"), 
				data.getTitleOfAction(),data.getNumbOfCols());
		utils.putInfoRow(sheet,++rowIndex, 
				data.getLocalizedVersion("logFrameMainObjective"), 
				data.getMainObjective(),data.getNumbOfCols());

		// empty row
		utils.putEmptyRow(sheet, ++rowIndex,ExportConstants.EMPTY_ROW_HEIGHT);

		// column headers
		row = sheet.createRow(++rowIndex);
		row.setHeightInPoints(ExportConstants.TITLE_ROW_HEIGHT);
		cellIndex = 3;
		utils.putHeader(row,++cellIndex, data.getLocalizedVersion("logFrameInterventionLogic"));
		utils.putHeader(row,++cellIndex,  data.getLocalizedVersion("indicators"));
		utils.putHeader(row,++cellIndex,data.getLocalizedVersion("logFrameMeansOfVerification"));
		utils.putHeader(row,++cellIndex, data.getLocalizedVersion("logFrameRisksAndAssumptions"));

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
		int lineCount=0;
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
								
			utils.putBorderedBasicCell(sheet,rowIndex, 3,p.getContent());
			lineCount = utils.calculateLineCount(p.getContent(), 18+2*(colWidthDesc+colWidthIndicator));	 
		
			row.setHeightInPoints(lineCount*defHeight);
			
			mergeCell(rowIndex, rowIndex, 3, data.getNumbOfCols()); 				
		}
		return rowIndex;
	}
	
	private int putAcItems(int rowIndex,boolean skipFirst,List<LogFrameActivity> acList){
		int lineCount=0;
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
								
			
			utils.putBorderedBasicCell(sheet,rowIndex, 4,a.getTitle());			
			utils.putBorderedBasicCell(sheet,rowIndex, 7,null);
			
			lineCount = utils.calculateLineCount(a.getTitle(), colWidthDesc);
			row.setHeightInPoints(lineCount*defHeight);

			// indicators and their means of verifications
			rowIndex = putIndicators(a.getIndicators(),rowIndex,false,lineCount);						
		}
		
		return rowIndex;
	}
	private int putERItems(int rowIndex,boolean skipFirst,List<ExpectedResult> erList){
		int lineCount=0;
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
			
			lineCount=utils.calculateLineCount(er.getInterventionLogic(), colWidthDesc);
 			utils.putBorderedBasicCell(sheet,rowIndex, 4,er.getInterventionLogic());
			
 			lineCount=Math.max(lineCount, utils.calculateLineCount(er.getRisksAndAssumptions(), colWidthDesc));
			utils.putBorderedBasicCell(sheet,rowIndex, 7,er.getRisksAndAssumptions());

			row.setHeightInPoints(lineCount*defHeight);

			// indicators and their means of verifications
			rowIndex = putIndicators(er.getIndicators(),rowIndex,false,lineCount);				
		}
		return rowIndex;
	}
	
	private int putSOItems(int rowIndex,boolean skipFirst,List<SpecificObjective> soList){
		int lineCount=0;
 		for (final SpecificObjective so : soList) {
			
			if(!skipFirst){
				row = sheet.createRow(++rowIndex);				
			}
			skipFirst=false;
			
			builder = new StringBuilder(data.getLocalizedVersion("logFrameSpecificObjectivesCode"));
			builder.append(" ");
			builder.append(data.getFormattedCode(so.getCode()));
			putCenteredBasicCell(rowIndex, 2, builder.toString());
			
			lineCount=utils.calculateLineCount(so.getInterventionLogic(), colWidthDesc);
 			utils.putBorderedBasicCell(sheet,rowIndex, 4,so.getInterventionLogic());
			
 			lineCount=Math.max(lineCount, utils.calculateLineCount(so.getRisksAndAssumptions(), colWidthDesc));
			utils.putBorderedBasicCell(sheet,rowIndex, 7,so.getRisksAndAssumptions());
			 
			row.setHeightInPoints(lineCount*defHeight);
			
			// indicators and their means of verifications
			rowIndex = putIndicators(so.getIndicators(),rowIndex,true,lineCount);				
		}
		return rowIndex;
	}
 
	private int putIndicators(final Set<Indicator> indicators, int rowIndex,boolean mergeCodeCells,int lineCount){
		if (indicators.size() > 0) {
			int startIndex = rowIndex;			
			int indiTextLinesSum=0;
			int indiLineCount=0;
			
			for (final Indicator indicator : indicators) {
				if (startIndex != rowIndex) {
					row = sheet.createRow(rowIndex);
					
				}		
				indiLineCount=0;
				if(data.isIndicatorsSheetExist()){
					utils.createLinkCell(row.createCell(5), 
							indicator.getName(), ExportConstants.INDICATOR_SHEET_PREFIX+indicator.getName(),true);
					indiLineCount=utils.calculateLineCount(indicator.getName(), colWidthIndicator);
				}else{
					String indiName=data.getDetailedIndicatorName(indicator.getId());					
					utils.putBorderedBasicCell(sheet,rowIndex, 5, indiName);
					indiLineCount=utils.calculateLineCount(indiName, colWidthIndicator);					 		
				}
								
 				indiLineCount=Math.max(indiLineCount, 
 						utils.calculateLineCount(indicator.getSourceOfVerification(), colWidthIndicator)
 				);				
				utils.putBorderedBasicCell(sheet,rowIndex, 6,indicator.getSourceOfVerification());
				
				indiTextLinesSum+=indiLineCount;
				row.setHeightInPoints(indiLineCount*defHeight);
				rowIndex++;
			}
			rowIndex--;
			
			if(indiTextLinesSum<lineCount){
				indiLineCount+=(lineCount-indiTextLinesSum);
				row.setHeightInPoints(indiLineCount*defHeight);
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
			utils.putBorderedBasicCell(sheet,rowIndex, 5, "");
			utils.putBorderedBasicCell(sheet,rowIndex, 6, "");
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
		StringBuilder builder = new StringBuilder(typeLabel);
		builder.append(" (");
		builder.append(code);
		builder.append(")"); 
		utils.putHeader(row, 1, builder.toString());				
	}
	
	private void putGroupCell(int rowIndex,String groupType,String code,String groupLabel){
		cell = sheet.getRow(rowIndex).createCell(2);
		StringBuilder builder = new StringBuilder(groupType);
		builder.append(" (");
		builder.append(code);
		builder.append(") - ");
		builder.append(groupLabel);
		cell.setCellValue(builder.toString());
		CellRangeAddress region = new CellRangeAddress(rowIndex,rowIndex, 2,data.getNumbOfCols());
		sheet.addMergedRegion(utils.getBorderedRegion(region, sheet, wb));
		cell.setCellStyle(utils.getGroupStyle(wb));
	} 
	
	private void putCenteredBasicCell(int rowIndex, int cellIndex, String text) {
		cell = utils.putBorderedBasicCell(sheet, rowIndex, cellIndex, text);
		cell.getCellStyle().setAlignment(CellStyle.ALIGN_CENTER);
	}

	
	
	@Override
	public void write(OutputStream output) throws Throwable {
		wb.write(output);
	}

}
