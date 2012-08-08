package org.sigmah.server.endpoint.export.sigmah.spreadsheet.template;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.ExcelUtils;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.ExportConstants;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.data.IndicatorEntryData;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.IndicatorGroup;
import org.sigmah.shared.report.content.PivotTableData;

public class IndicatorEntryExcelTemplate implements ExportTemplate {

	private final IndicatorEntryData data;
	private final HSSFWorkbook wb;
	private HSSFRow row = null;
	private HSSFCell cell = null;
	
	private final ExcelUtils utils;
	private CellRangeAddress region;
	private ExcelUtils.CellTextFormat cellTextFormat;	
	private final float defHeight=17; 
	
	public IndicatorEntryExcelTemplate(final IndicatorEntryData data){
		this(data,new HSSFWorkbook());
	}
	
	public IndicatorEntryExcelTemplate(final IndicatorEntryData data,final HSSFWorkbook wb){
		this.data=data;
		this.wb=wb;
	    final HSSFSheet sheet = wb.createSheet(data.getLocalizedVersion("flexibleElementIndicatorsList"));
		utils = new ExcelUtils(wb);
 		int rowIndex = -1;
		int cellIndex = 0;
 	 	// formatting sheet
		//utils.formatPrinableSheet(sheet);

		// empty row
		utils.putEmptyRow(sheet, ++rowIndex, 8.65f);

		//title
		utils.putMainTitle(sheet,++rowIndex,
				data.getLocalizedVersion("flexibleElementIndicatorsList").toUpperCase(),
				data.getNumbOfCols());
 
		// empty row
		utils.putEmptyRow(sheet, ++rowIndex,ExportConstants.EMPTY_ROW_HEIGHT);

		// column headers
		row = sheet.createRow(++rowIndex);
		row.setHeightInPoints(ExportConstants.TITLE_ROW_HEIGHT);
		cellIndex = 0;
		utils.putHeader(row,++cellIndex, data.getLocalizedVersion("name"));
		utils.putHeader(row,++cellIndex,  data.getLocalizedVersion("code"));
		utils.putHeader(row,++cellIndex,data.getLocalizedVersion("targetValue"));
		utils.putHeader(row,++cellIndex, data.getLocalizedVersion("value"));
		
		// empty row
		utils.putEmptyRow(sheet, ++rowIndex, 8.65f);
		
		// freeze pane
		sheet.createFreezePane(0, rowIndex);				
		
		for(final IndicatorGroup group:data.getIndicators().getGroups()){
			row = sheet.createRow(++rowIndex);
			row.setHeightInPoints(ExportConstants.TITLE_ROW_HEIGHT);
			putGroupCell(sheet,rowIndex, group.getName());
			for(final IndicatorDTO indicator: group.getIndicators()){
				//indicator's detail sheet
				createDetailSheet(indicator);
				row = sheet.createRow(++rowIndex);
				row.setHeightInPoints(ExportConstants.TITLE_ROW_HEIGHT);
				//ind name
				utils.createLinkCell(row.createCell(1), indicator.getName(), indicator.getName(),true);
				//code
				utils.putBorderedBasicCell(sheet,rowIndex, 2, indicator.getCode());
				String targetVal="";
				if(indicator.getObjective()!=null) targetVal+=indicator.getObjective();
				//target
				putRightAlignedCell(sheet,rowIndex, 3, targetVal);
				//current value
				putRightAlignedCell(sheet,rowIndex, 4, data.getFormattedValue(indicator));				    
			}
			
		}
		 
		sheet.setColumnWidth(0, 256 * 2);
		sheet.setColumnWidth(1, 256 * 45);
		sheet.setColumnWidth(2, 256 * 27);
		sheet.setColumnWidth(3, 256 * 27);
		sheet.setColumnWidth(4, 256 * 27);
	 
	}
	
	private void createDetailSheet(final IndicatorDTO indicator){
		final boolean isQualitative=indicator.getAggregation() == IndicatorDTO.AGGREGATE_MULTINOMIAL;
		final HSSFSheet sheetEx = wb.createSheet(utils.normalizeAsLink(indicator.getName()));			
		int rowIndex=-1;
		int startRowIndex=0;
		
		List<PivotTableData.Axis> leaves= 
			data.getEntryMap().get(indicator.getId()).getRootColumn().getLeaves();
		int numbOfLeaves=leaves.size();
		int numbOfCols=4;
		 
		//back to list link
		row = sheetEx.createRow(++rowIndex);
		utils.createLinkCell(row.createCell(1),
				data.getLocalizedVersion("backToList"),
				data.getLocalizedVersion("flexibleElementIndicatorsList"),false);
		sheetEx.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex,
				1, numbOfCols));
		
		//title
		utils.putMainTitle(sheetEx,++rowIndex,indicator.getName(),numbOfCols);
 
		//empty row
		utils.putEmptyRow(sheetEx, ++rowIndex,ExportConstants.EMPTY_ROW_HEIGHT);
		sheetEx.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex,
				1, numbOfCols));
		
		//put details
		putBasicInfo(sheetEx,++rowIndex, data.getLocalizedVersion("code"),
				indicator.getCode(), numbOfCols);	
		
		putBasicInfo(sheetEx,++rowIndex, data.getLocalizedVersion("group"),
				data.getGroupMap().get(indicator.getGroupId()), numbOfCols);			
		utils.addDropDownList(sheetEx, rowIndex, rowIndex, 2, numbOfCols, data.getGroupMap().values());
		
		//type
	    String type = null;; 
		if(isQualitative){
			//qualitative
			type=data.getLocalizedVersion("qualitative");
		}else{
			//quantitative
			type=data.getLocalizedVersion("quantitative");
		}		
		putBasicInfo(sheetEx,++rowIndex, data.getLocalizedVersion("type"),
				type, numbOfCols);	
		utils.addDropDownList(sheetEx, rowIndex, rowIndex, 2, numbOfCols,
				Arrays.asList(data.getLocalizedVersion("quantitative"),data.getLocalizedVersion("qualitative")));
		
		//conditional
		if(isQualitative){
			//qualitative
			
			//possible values
	 		row = sheetEx.createRow(++rowIndex);
			row.setHeightInPoints(defHeight);
			
			//key
			startRowIndex=rowIndex;
			cell=utils.putHeader(row, 1, data.getLocalizedVersion("possibleValues"));
			cell.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);		 

			//value
			boolean first=true;
			for(String label:indicator.getLabels()){
				if(!first){
					row = sheetEx.createRow(++rowIndex);
					row.setHeightInPoints(defHeight);				
				}
				first=false;
				utils.putBorderedBasicCell(sheetEx,rowIndex, 2, label);
				region = new CellRangeAddress(rowIndex, rowIndex,2, numbOfCols);						
				sheetEx.addMergedRegion(utils.getBorderedRegion(region, sheetEx, wb));
			}	
			region = new CellRangeAddress(startRowIndex, rowIndex, 1, 1);
			sheetEx.addMergedRegion(utils.getBorderedRegion(region, sheetEx, wb));
		}else{
			//quantitative
			
			//aggregation method			
			String aggrMethod=null;
			if(indicator.getAggregation() == IndicatorDTO.AGGREGATE_AVG )
				aggrMethod=data.getLocalizedVersion("average");
			else
				aggrMethod=data.getLocalizedVersion("sum");
			putBasicInfo(sheetEx,++rowIndex, data.getLocalizedVersion("aggregationMethod"),
					aggrMethod, numbOfCols);	
			utils.addDropDownList(sheetEx, rowIndex, rowIndex, 2, numbOfCols,
					Arrays.asList(data.getLocalizedVersion("sum"),data.getLocalizedVersion("average")));
			
			//units
			putBasicInfo(sheetEx,++rowIndex, data.getLocalizedVersion("units"),
					indicator.getUnits(), numbOfCols);
			
			//target value
			String targetVal="";
			if(indicator.getObjective()!=null) targetVal+=indicator.getObjective();
			putBasicInfo(sheetEx,++rowIndex, data.getLocalizedVersion("targetValue"),
					targetVal, numbOfCols);	
		}	
		
		//source of ver
 		cellTextFormat=utils.formatCellText(indicator.getSourceOfVerification(), 3*18);
 		putBasicInfo(sheetEx,++rowIndex, data.getLocalizedVersion("sourceOfVerification"),
				cellTextFormat.formattedText, numbOfCols);
		row.setHeightInPoints(cellTextFormat.dividedlines*defHeight);
		
		//comment
 		cellTextFormat=utils.formatCellText(indicator.getDescription(), 3*18);
 		putBasicInfo(sheetEx,++rowIndex, data.getLocalizedVersion("indicatorComments"),
				cellTextFormat.formattedText, numbOfCols);
		row.setHeightInPoints(cellTextFormat.dividedlines*defHeight);
		
		//value
 		putBasicInfo(sheetEx,++rowIndex, data.getLocalizedVersion("value"),
 				data.getFormattedValue(indicator), numbOfCols);	
 		//empty row
		utils.putEmptyRow(sheetEx, ++rowIndex,ExportConstants.EMPTY_ROW_HEIGHT);
		sheetEx.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex,
				1, numbOfCols));
		
 		//data entry
		//header
 		row = sheetEx.createRow(++rowIndex);
		row.setHeightInPoints(defHeight);
		int cellIndex = 0;
		utils.putHeader(row,++cellIndex, data.getLocalizedVersion("sideAndMonth"));
		Map<String, Integer> columnIndexMap=new HashMap<String, Integer>();
		for(PivotTableData.Axis axis:leaves){
			utils.putHeader(row,++cellIndex, axis.getLabel());
			columnIndexMap.put(axis.getLabel(),cellIndex);
		}
		
		//rows	
		startRowIndex=rowIndex+1;
 		for(PivotTableData.Axis axis : data.getEntryMap().get(indicator.getId()).getRootRow().getChildren()) {
 			row = sheetEx.createRow(++rowIndex);
 			row.setHeightInPoints(defHeight);
 			utils.putHeader(row,1, axis.getLabel());
 			
 			//populate empty cells
 			for(int i=0;i<numbOfLeaves;i++){
 				cell=utils.putBorderedBasicCell(sheetEx, rowIndex, i+2, "");
 			}
 			
 			//insert values
 			for(Map.Entry<PivotTableData.Axis, PivotTableData.Cell> entry : axis.getCells().entrySet()) { 				
                cellIndex= columnIndexMap.get(entry.getKey().getLabel());
                String value="";
                boolean rightAligned=false;
                if(isQualitative){
                	value=data.getLabelByIndex(indicator.getLabels(), entry.getValue().getValue());                	 
                }else{
                	value=String.valueOf(Math.round(entry.getValue().getValue()));
                	rightAligned=true;
                }
                putValueCell(sheetEx,rowIndex, cellIndex,value,rightAligned);
 			}
         }
 		
 		if(isQualitative){
 			utils.addDropDownList(sheetEx, startRowIndex, rowIndex, 2, numbOfLeaves+1, indicator.getLabels());
 		}
 		
		//col width
		sheetEx.setColumnWidth(0, 256 * 2);
		sheetEx.autoSizeColumn(1);
		for(int i=2;i<2+numbOfLeaves;i++){
			sheetEx.setColumnWidth(i, 256 * 16);
		}			 
	} 
	
	
	
	private void putBasicInfo(HSSFSheet sheet,int rowIndex,String key,String value,int numbOfCols){
		row = sheet.createRow(rowIndex);
		row.setHeightInPoints(defHeight);
		
		//key 
		cell=utils.putHeader(row, 1, key);
		cell.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);

		//value
		utils.putBorderedBasicCell(sheet,rowIndex, 2, value);
		region = new CellRangeAddress(rowIndex, rowIndex,
				2, numbOfCols);
		sheet.addMergedRegion(utils.getBorderedRegion(region, sheet, wb));
	}
 
	private void putRightAlignedCell(HSSFSheet sheet,int rowIndex, int cellIndex, String text) {
		cell=utils.putBorderedBasicCell(sheet, rowIndex, cellIndex, text);
		cell.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);
	}
	
	private void putValueCell(HSSFSheet sheet,int rowIndex, 
			int cellIndex, String value,boolean rightAligned) {
		cell=utils.putBorderedBasicCell(sheet, rowIndex, cellIndex, value);
		if(rightAligned)
			cell.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);
	}
 
	private void putGroupCell(HSSFSheet sheet,int rowIndex,String name){
		cell = sheet.getRow(rowIndex).createCell(1);		 
		cell.setCellValue(name);
		CellRangeAddress region = new CellRangeAddress(rowIndex,rowIndex, 1,data.getNumbOfCols());
		sheet.addMergedRegion(utils.getBorderedRegion(region, sheet, wb));
		cell.setCellStyle(utils.getGroupStyle(wb));
	} 
 
	
	@Override
	public void write(OutputStream output) throws Throwable {
		wb.write(output);
	}

}
