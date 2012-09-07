package org.sigmah.server.endpoint.export.sigmah.spreadsheet.template;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.CalcUtils;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.data.GlobalExportData;

public class GlobalExportCalcTemplate implements ExportTemplate{

	private final SpreadsheetDocument doc;
 	
	public GlobalExportCalcTemplate(final GlobalExportData data) throws Throwable{
		
		doc = SpreadsheetDocument.newSpreadsheetDocument();
		CalcUtils.prepareCoreStyle(doc);
		Row row;
  		Table table = doc.getSheetByIndex(0);			
 		int rowIndex = -1; 		
 		boolean first=true;
 		final Map<Integer,Integer> headerWidthMap=new HashMap<Integer, Integer>();
 		final Map<Integer,Integer> contentWidthMap=new HashMap<Integer, Integer>();
 		
 		for(final String pModelName: data.getExportData().keySet()){
 			final List<String[]> dataList=data.getExportData().get(pModelName);
 			if(first){
 				table.setTableName(pModelName);
 			}else{
 				table=doc.appendSheet(pModelName);
 			}
 			
 			first=false;
  			rowIndex = -1; 
 			headerWidthMap.clear();
 			contentWidthMap.clear(); 		
 			int defaultWidth=30;
 			
 			//titles
 			final String[] header=dataList.get(0);
 			row = table.getRowByIndex(++rowIndex);
 			for(int i=0;i<header.length;i++){
 				CalcUtils.putGlobalExportHeader(row, i,header[i]);
 				if(header[i]!=null){
 					headerWidthMap.put(i, header[i].length()/2);
 				} 				
 			}
  			 			 
 			//values 		
 			for(int j=1;j<dataList.size();j++){
 				row = table.getRowByIndex(++rowIndex);
 				final String[] values=dataList.get(j);
  	 			int devider=2;
 				for(int i=0;i<header.length;i++){ 					
 					CalcUtils.createBasicCell(table, i, rowIndex, values[i]);
 					if(values[i]!=null){
 						String parts[]=values[i].split("\n");
 	 					if(parts.length>devider){
 	 						devider=parts.length;	 						
 	 					}
 					 
	 					int currentWidth = values[i].length()/devider;
	 					Integer activeWidth =contentWidthMap.get(i);
	 					if(activeWidth!=null){
	 						currentWidth=Math.max(activeWidth, currentWidth);
	 					}
		 				contentWidthMap.put(i, currentWidth);
 					}
 				} 				
  			}
 			
	 		for(Integer i : headerWidthMap.keySet()){
 				Integer width=defaultWidth;
 				if(headerWidthMap.get(i)!=null){
 					width=headerWidthMap.get(i);
 				}
 				if(contentWidthMap.get(i)!=null){
 					width=Math.max(contentWidthMap.get(i), width);
 				}
 				table.getColumnByIndex(i).setWidth(width+38);
 			}
 		}
	}
	
	@Override
	public void write(OutputStream output) throws Throwable {
		doc.save(output);
		doc.close();
	}

}
