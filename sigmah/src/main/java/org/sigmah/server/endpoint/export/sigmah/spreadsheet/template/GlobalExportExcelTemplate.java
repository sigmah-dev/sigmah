package org.sigmah.server.endpoint.export.sigmah.spreadsheet.template;

import java.io.OutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.ExcelUtils;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.ExportConstants;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.data.GlobalExportData;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.data.ProjectSynthesisData;

import com.google.gwt.dom.client.LIElement;

public class GlobalExportExcelTemplate implements ExportTemplate {

	private final GlobalExportData data;
	private final HSSFWorkbook wb; 
 	private final ExcelUtils utils;
  	private final float defHeight=ExportConstants.TITLE_ROW_HEIGHT;  	 
  	private final int colWidth=20;
  	
 	public GlobalExportExcelTemplate(final GlobalExportData data){
 		this.wb=new HSSFWorkbook();
		this.data=data;
 		utils = new ExcelUtils(wb);
 		int rowIndex = -1;
 		HSSFSheet sheet;
 		HSSFRow row;
 		for(final String pModelName: data.getExportData().keySet()){
 			List<String[]> dataList=data.getExportData().get(pModelName);
 			sheet=wb.createSheet(pModelName);
 			rowIndex = -1;
 			int maxLineCount=1;
 			int lineCount=1;
 			
 			//titles
 			final String[] header=dataList.get(0);
 			row=sheet.createRow(++rowIndex);
 			for(int i=0;i<header.length;i++){
 				utils.putBorderedBasicCell(sheet, rowIndex, i, header[i]);
 				lineCount=utils.calculateLineCount(header[i], colWidth);
				if(maxLineCount < lineCount){
					maxLineCount=lineCount;
				}
 			}
 			row.setHeightInPoints(maxLineCount*defHeight);
 			
 			
 			//values 		
 			for(int j=1;j<dataList.size();j++){
 				row=sheet.createRow(++rowIndex);
 				final String[] values=dataList.get(j);
 				 maxLineCount=1;
 	 			 lineCount=1;
 				for(int i=0;i<header.length;i++){
 					utils.putBorderedBasicCell(sheet, rowIndex, i, values[i]);
 					lineCount=utils.calculateLineCount(values[i], colWidth);
 					if(maxLineCount < lineCount){
 						maxLineCount=lineCount;
 					}
 				} 				
 				row.setHeightInPoints(maxLineCount*defHeight);
 			}
 			
 			
 			// set width
 			for(int i=0;i<header.length;i++){
 				sheet.setColumnWidth(i, 256 * colWidth);
 			}
 		}
 		
 		
 	}

	@Override
	public void write(OutputStream output) throws Throwable {
		wb.write(output);
	}

}
