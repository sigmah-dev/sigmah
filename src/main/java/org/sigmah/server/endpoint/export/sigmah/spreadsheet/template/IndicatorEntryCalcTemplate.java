/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.server.endpoint.export.sigmah.spreadsheet.template;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.CellRange;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.CalcUtils;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.ExportConstants;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.ExportConstants.MultiItemText;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.data.IndicatorEntryData;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.IndicatorGroup;
import org.sigmah.shared.report.content.PivotTableData;

/*
 * @author sherzod
 */
public class IndicatorEntryCalcTemplate implements ExportTemplate {

 	private final SpreadsheetDocument doc;
	private final IndicatorEntryData data;
 	
	private Row row;
	private Cell cell;
 	private String coreCellStyle;
 
	public IndicatorEntryCalcTemplate(final IndicatorEntryData data,final SpreadsheetDocument exDoc) throws Throwable {
		this.data=data;
		
		Table table=null;
		String tableName=data.getLocalizedVersion("flexibleElementIndicatorsList").replace(" ", "_");
		if(exDoc==null){
			doc = SpreadsheetDocument.newSpreadsheetDocument();
			table = doc.getSheetByIndex(0);
			table.setTableName(tableName);
		}else{
			doc=exDoc;
			table=doc.appendSheet(tableName);
		}
		 
		 
		coreCellStyle = CalcUtils.prepareCoreStyle(doc);
		
		int rowIndex = -1;
		int cellIndex = 0;		

		//skip row
		++rowIndex;		 

		// title
		CalcUtils.putMainTitle(table,++rowIndex,data.getNumbOfCols(),
				data.getLocalizedVersion("flexibleElementIndicatorsList").toUpperCase());					 

		//emptry row
		CalcUtils.putEmptyRow(table,++rowIndex);

		// column headers
		row = table.getRowByIndex(++rowIndex);
		cellIndex = 0;
		CalcUtils.putHeader(row,++cellIndex, data.getLocalizedVersion("name"));
		CalcUtils.putHeader(row,++cellIndex,  data.getLocalizedVersion("code")); 
		CalcUtils.putHeader(row,++cellIndex,  data.getLocalizedVersion("targetValue"));
		CalcUtils.putHeader(row,++cellIndex,data.getLocalizedVersion("value"));
 		row.setHeight(5, false);

		//empty row
		row = table.getRowByIndex(++rowIndex);
		row.setHeight(3.8, false);
		row.getCellByIndex(1).setCellStyleName(null);
		row.getCellByIndex(2).setCellStyleName(null);
		row.getCellByIndex(3).setCellStyleName(null);
		row.getCellByIndex(4).setCellStyleName(null);
 
		for(final IndicatorGroup group:data.getIndicators().getGroups()){
			row = table.getRowByIndex(++rowIndex);
			CalcUtils.putGroupCell(table, 1, rowIndex, group.getName());
     		CalcUtils.mergeCell(table, 1, rowIndex, data.getNumbOfCols(), rowIndex);
 			for(final IndicatorDTO indicator: group.getIndicators()){
				//indicator's detail sheet
				createDetailSheet(indicator);
				row = table.getRowByIndex(++rowIndex);
				//ind name
				cell=CalcUtils.createBasicCell(table, 1, rowIndex, null);
				CalcUtils.applyLink(cell, indicator.getName(), ExportConstants.INDICATOR_SHEET_PREFIX+indicator.getName());
 				//code
			    CalcUtils.createBasicCell(table, 2, rowIndex,  indicator.getCode());
 			 	//target
				putValueCell(table,rowIndex,3,indicator.getObjective(),true);
 				//current value
				putValueCell(table,rowIndex,4,data.getFormattedValue(indicator),true);
 			}
			
		}

		table.getColumnByIndex(0).setWidth(3.8);	
		table.getColumnByIndex(1).setWidth(83);
		table.getColumnByIndex(2).setWidth(55);
		table.getColumnByIndex(3).setWidth(55);
		table.getColumnByIndex(4).setWidth(55);
		
		
 	}
	
	private void createDetailSheet(final IndicatorDTO indicator) throws Throwable{
		final boolean isQualitative=indicator.getAggregation() == IndicatorDTO.AGGREGATE_MULTINOMIAL;
		final Table tableEx = doc.appendSheet(
				CalcUtils.normalizeAsLink(ExportConstants.INDICATOR_SHEET_PREFIX+indicator.getName()));			
		int rowIndex=-1;
 		
		List<PivotTableData.Axis> leaves= 
			data.getEntryMap().get(indicator.getId()).getRootColumn().getLeaves();
		int numbOfLeaves=leaves.size();
		int numbOfCols=4;
		 
		//back to list link
		row = tableEx.getRowByIndex(++rowIndex);
		cell=tableEx.getCellByPosition(1, rowIndex); 
		CalcUtils.applyLink(cell, data.getLocalizedVersion("goToIndicatorsList"),
				data.getLocalizedVersion("flexibleElementIndicatorsList"));
		CalcUtils.mergeCell(tableEx, 1, rowIndex, data.getNumbOfCols(), rowIndex);
		 		
		//title
		CalcUtils.putMainTitle(tableEx,++rowIndex,numbOfCols,indicator.getName());	
  
		//empty row
		CalcUtils.putEmptyRow(tableEx,++rowIndex);		 
		
		//put details
		putBasicInfo(tableEx,++rowIndex, data.getLocalizedVersion("code"),
				indicator.getCode(), numbOfCols);	
		
	 	 putBasicInfo(tableEx,++rowIndex, data.getLocalizedVersion("group"),
				data.getGroupMap().get(indicator.getGroupId()), numbOfCols);	
		
 		
		//type
	    String type = null;; 
		if(isQualitative){
			//qualitative
			type=data.getLocalizedVersion("qualitative");
		}else{
			//quantitative
			type=data.getLocalizedVersion("quantitative");
		}		
		putBasicInfo(tableEx,++rowIndex, data.getLocalizedVersion("type"),
				type, numbOfCols);
		  	
		//conditional
		if(isQualitative){
			//qualitative
			
			//possible values
			row = tableEx.getRowByIndex(++rowIndex);
			
			//key
			cell=CalcUtils.putHeader(row,1,  data.getLocalizedVersion("possibleValues"));
			cell.setHorizontalAlignment(ExportConstants.ALIGH_HOR_RIGHT);	 

 			//value
			final MultiItemText itemText=data.formatPossibleValues(indicator.getLabels());
			CalcUtils.createBasicCell(tableEx, 2, rowIndex, itemText.text);
			CalcUtils.mergeCell(tableEx, 2, rowIndex, numbOfCols, rowIndex);
			
			 		
		}else{
			//quantitative
			
			//aggregation method			
			String aggrMethod=null;
			if(indicator.getAggregation() == IndicatorDTO.AGGREGATE_AVG )
				aggrMethod=data.getLocalizedVersion("average");
			else
				aggrMethod=data.getLocalizedVersion("sum");
			putBasicInfo(tableEx,++rowIndex, data.getLocalizedVersion("aggregationMethod"),
					aggrMethod, numbOfCols);
		 	//units
			putBasicInfo(tableEx,++rowIndex, data.getLocalizedVersion("units"),
					indicator.getUnits(), numbOfCols);
			
			//target value 
			putBasicInfo(tableEx,++rowIndex, data.getLocalizedVersion("targetValue"),
					indicator.getObjective(), numbOfCols);	
		}	
		
		//source of ver 
		putBasicInfo(tableEx,++rowIndex, data.getLocalizedVersion("sourceOfVerification"),
				indicator.getSourceOfVerification(), numbOfCols);
		
		//comment 
		putBasicInfo(tableEx,++rowIndex, data.getLocalizedVersion("indicatorComments"),
				indicator.getDescription(), numbOfCols);
		
		//value
 		putBasicInfo(tableEx,++rowIndex, data.getLocalizedVersion("value"),
 				data.getFormattedValue(indicator), numbOfCols);	
 		//empty row
 		CalcUtils.putEmptyRow(tableEx,++rowIndex);	
		
 		row=tableEx.getRowByIndex(rowIndex);
 		row.getCellByIndex(1).setCellStyleName(null);
		row.getCellByIndex(2).setCellStyleName(null);
		row.getCellByIndex(3).setCellStyleName(null);
		row.getCellByIndex(4).setCellStyleName(null);
		
 		//data entry
		//header
 		row = tableEx.getRowByIndex(++rowIndex);
		int cellIndex = 0;
		CalcUtils.putHeader(row,++cellIndex,  data.getLocalizedVersion("sideAndMonth"));
 		Map<String, Integer> columnIndexMap=new HashMap<String, Integer>();
		for(PivotTableData.Axis axis:leaves){
			CalcUtils.putHeader(row,++cellIndex,  axis.getLabel());
			columnIndexMap.put(axis.getLabel(),cellIndex);
		}
		
		//rows	
  		for(PivotTableData.Axis axis : data.getEntryMap().get(indicator.getId()).getRootRow().getChildren()) {
 			row = tableEx.getRowByIndex(++rowIndex);
  			CalcUtils.putHeader(row,1,  axis.getLabel());
 			//populate empty cells
 			for(int i=0;i<numbOfLeaves;i++){
 				cell=CalcUtils.createBasicCell(tableEx, i+2, rowIndex, "");
  			}
 			
 			//insert values
 			for(Map.Entry<PivotTableData.Axis, PivotTableData.Cell> entry : axis.getCells().entrySet()) { 				
                cellIndex= columnIndexMap.get(entry.getKey().getLabel());
                Object value=null;
                boolean rightAligned=false;
                if(isQualitative){
                	value=data.getLabelByIndex(indicator.getLabels(), entry.getValue().getValue());                	 
                }else{
                	value=new Long(Math.round(entry.getValue().getValue()));
                	rightAligned=true;
                }
                putValueCell(tableEx,rowIndex, cellIndex,value,rightAligned);
 			}
         }
 	 	//col width
 		tableEx.getColumnByIndex(0).setWidth(3.8);	
 		tableEx.getColumnByIndex(1).setWidth(60);
		for(int i=2;i<2+numbOfLeaves;i++){
 			tableEx.getColumnByIndex(i).setWidth(30);
		}			 
	} 
	
	private void putValueCell(Table table,int rowIndex, 
			int cellIndex, Object value,boolean rightAligned) {
		cell=CalcUtils.createBasicCell(table, cellIndex, rowIndex, value);
 		if(rightAligned)
 			cell.setHorizontalAlignment(ExportConstants.ALIGH_HOR_RIGHT);
	}
	
	private void putBasicInfo(Table table,int rowIndex,
			String key,Object value,int numbOfCols){
		row = table.getRowByIndex(rowIndex);
		row.getCellByIndex(1).setCellStyleName(null);
		row.getCellByIndex(2).setCellStyleName(null);
		cell=CalcUtils.putHeader(row, 1, key);
		cell.setHorizontalAlignment(ExportConstants.ALIGH_HOR_RIGHT);
		
		cell=CalcUtils.createBasicCell(table, 2, rowIndex, value); 
		CalcUtils.mergeCell(table, 2, rowIndex,numbOfCols, rowIndex); 
		
 	}

	@Override
	public void write(OutputStream output) throws Throwable {
		doc.save(output);
		doc.close();
	}

}