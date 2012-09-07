/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.server.endpoint.export.sigmah.spreadsheet.template;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.ExcelUtils;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.ExportConstants;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.data.GlobalExportData;

/*
 * Template to export project models and their project to a excel document 
 * @author sherzod
 */
public class GlobalExportExcelTemplate implements ExportTemplate {

 	private final HSSFWorkbook wb;  
  	
 	public GlobalExportExcelTemplate(final GlobalExportData data){
 		this.wb=new HSSFWorkbook();
 		final ExcelUtils utils = new ExcelUtils(wb);
 		final float defHeight=ExportConstants.TITLE_ROW_HEIGHT;  
 		int rowIndex = -1;
 		HSSFSheet sheet;
 		HSSFRow row;
 		int defaultWidth=20;
 		
 		final Map<Integer,Integer> headerWidthMap=new HashMap<Integer, Integer>();
 		final Map<Integer,Integer> contentWidthMap=new HashMap<Integer, Integer>();
 		
 		for(final String pModelName: data.getExportData().keySet()){
 			List<String[]> dataList=data.getExportData().get(pModelName);
 			sheet=wb.createSheet(pModelName);
 			rowIndex = -1;
 			 
 			headerWidthMap.clear();
 			contentWidthMap.clear();
 			
 			//titles
 			final String[] header=dataList.get(0);
 			row=sheet.createRow(++rowIndex);
 			for(int i=0;i<header.length;i++){
 				utils.putGlobalExportHeader(row, i, header[i]);
 				if(header[i]!=null){
 	 				headerWidthMap.put(i, header[i].length()/2);
 				}
 				
 			}
 			row.setHeightInPoints(2*defHeight); 			
 			
 			//values 		
 			for(int j=1;j<dataList.size();j++){
 				row=sheet.createRow(++rowIndex);
 				final String[] values=dataList.get(j);
 				int devider=2;
 				for(int i=0;i<header.length;i++){ 					
 					utils.putBorderedBasicCell(sheet, rowIndex, i, values[i]);
 					
 					if(values[i]!=null){
 						String parts[]=values[i].split("\n");
 	 					if(parts.length>devider){
 	 						devider=parts.length;	 						
 	 					} 					 
 					
	 					int currentWidth = values[i].length()/devider;
	 					Integer oldWidth =contentWidthMap.get(i);
	 					if(oldWidth!=null){
	 						currentWidth=Math.max(oldWidth, currentWidth);
	 					}
		 				contentWidthMap.put(i, currentWidth);
 					}
 				} 				
 				row.setHeightInPoints(devider*defHeight);
 			}
 			
 			
 			// set width
 			for(Integer i : headerWidthMap.keySet()){
 				Integer width=defaultWidth;
 				if(headerWidthMap.get(i)!=null){
 					width=headerWidthMap.get(i);
 				}
 				if(contentWidthMap.get(i)!=null){
 					width=Math.max(contentWidthMap.get(i), width);
 				}
  				sheet.setColumnWidth(i, 256 * (width+15));
 			}
 		}
 		
 		
 	}

	@Override
	public void write(OutputStream output) throws Throwable {
		wb.write(output);
	}

}
