package org.sigmah.server.endpoint.export.sigmah.spreadsheet.template;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.ExcelUtils;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.ExportConstants;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.data.ProjectSynthesisData;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.command.result.ValueResultUtils;
import org.sigmah.shared.dto.PhaseDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.QuestionChoiceElementDTO;
import org.sigmah.shared.dto.element.QuestionElementDTO;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.value.ListableValue;
import org.sigmah.shared.dto.value.TripletValueDTO;

public class ProjectSynthesisExcelTemplate implements ExportTemplate {

	private final ProjectSynthesisData data;
	private final HSSFWorkbook wb;
	private HSSFRow row = null;
	private HSSFCell cell = null;
	private ExcelUtils.CellTextFormat cellTextFormat;	
	private final ExcelUtils utils;
	private CellRangeAddress region;
 	private final float defHeight=ExportConstants.TITLE_ROW_HEIGHT; 
 	private final int labelColWidth=60;
 	private final int valueColWidth=60;
	
	public ProjectSynthesisExcelTemplate(final ProjectSynthesisData data,final HSSFWorkbook wb)throws Throwable {
		this.wb=wb;
		this.data=data;
	    final HSSFSheet sheet = wb.createSheet(data.getLocalizedVersion("projectSynthesis"));
		utils = new ExcelUtils(wb);
 		int rowIndex = -1;
		int cellIndex = 0;
 	 	// formatting sheet
		//utils.formatPrinableSheet(sheet);

		// empty row
		utils.putEmptyRow(sheet, ++rowIndex, 8.65f);

		//title
		utils.putMainTitle(sheet,++rowIndex,
				data.getLocalizedVersion("projectSynthesis").toUpperCase(),
				data.getNumbOfCols());
 
		// empty row
		utils.putEmptyRow(sheet, ++rowIndex,ExportConstants.EMPTY_ROW_HEIGHT);
		
		// column headers
		row = sheet.createRow(++rowIndex);
 		cellIndex = 0;
		utils.putHeader(row,++cellIndex,  data.getLocalizedVersion("adminFlexibleContainer"));
		utils.putHeader(row,++cellIndex,data.getLocalizedVersion("adminFlexibleName"));
		utils.putHeader(row,++cellIndex, data.getLocalizedVersion("value"));
		
		// empty row
		utils.putEmptyRow(sheet, ++rowIndex,ExportConstants.EMPTY_ROW_HEIGHT);
		 
		// freeze pane
		sheet.createFreezePane(0, rowIndex);
		
		//detail
		row = sheet.createRow(++rowIndex);
    	utils.putHeader(row, 1, data.getLocalizedVersion("projectDetails"));
   		rowIndex=putLayout(sheet,
   				data.getProject().getProjectModelDTO().getProjectDetailsDTO().getLayoutDTO(), rowIndex); 
 		
		//run through project phases to get synthesis data
        for(final PhaseDTO phase : data.getProject().getPhasesDTO()){
        	
        	//phase name        	
        	row = sheet.createRow(++rowIndex);
        	utils.putHeader(row, 1, phase.getPhaseModelDTO().getName());
       		rowIndex=putLayout(sheet, phase.getPhaseModelDTO().getLayoutDTO(), rowIndex); 
  
        } // phases
        
        
		sheet.setColumnWidth(0, 256 * 2);
		sheet.setColumnWidth(1, 256 * 25);
		sheet.setColumnWidth(2, 256 * labelColWidth);
		sheet.setColumnWidth(3, 256 * valueColWidth);
		
	}
	
	private int putLayout(final HSSFSheet sheet,final LayoutDTO layout,int rowIndex) throws Throwable{
		
		int typeStartRow=rowIndex;
		 boolean firstGroup=true;
  		//layout groups for each phase
    	for (final LayoutGroupDTO layoutGroup : layout.getLayoutGroupsDTO()) {

    		//layout group cell
    		if(!firstGroup){
    			row = sheet.createRow(++rowIndex);
    		}
    		firstGroup=false;	
    		row.setHeightInPoints(ExportConstants.TITLE_ROW_HEIGHT);
    		CellRangeAddress region = new CellRangeAddress(rowIndex,rowIndex, 2,data.getNumbOfCols());
    		sheet.addMergedRegion(utils.getBorderedRegion(region, sheet, wb));
    		cell = sheet.getRow(rowIndex).createCell(2);
    		cell.setCellStyle(utils.getGroupStyle(wb));
     		cell.setCellValue(layoutGroup.getTitle());
    		
    			//elements for each layout group
    		for (final LayoutConstraintDTO constraint : layoutGroup.getLayoutConstraintsDTO()) {
    			final FlexibleElementDTO element = constraint.getFlexibleElementDTO();   
    			
                //skip if element is not exportable
    			if(!element.getExportable()) continue;

                final ValueResult valueResult = data.getValue(element.getId(),  element.getEntityName());                
            	ProjectSynthesisData.ElementPair pair=null;
            	
                 /*DEF FLEXIBLE*/
            	if(element.getEntityName().equals("element.DefaultFlexibleElement")){                	
                	pair=data.getDefElementPair(valueResult, element);
                	putElement(sheet,++rowIndex,pair.label,pair.value);
                	
                }else /*CHECKBOX*/ if(element.getEntityName().equals("element.CheckboxElement")){
                	pair=data.getCheckboxElementPair(valueResult, element);
                	putElement(sheet,++rowIndex,pair.label,pair.value);
                	utils.addDropDownList(sheet, rowIndex, rowIndex, 3, 3,
            				Arrays.asList(data.getLocalizedVersion("yes"),data.getLocalizedVersion("no")));
                }else /* MESSAGE */ if(element.getEntityName().equals("element.MessageElement")){
                	/*cellTextFormat=utils.formatCellText(element.getLabel(), 2*valueColWidth);
                	row = sheet.createRow(++rowIndex);
         		    utils.putBorderedBasicCell(sheet,rowIndex, 2,cellTextFormat.formattedText);
                    region = new CellRangeAddress(rowIndex,rowIndex, 2,data.getNumbOfCols());
              		sheet.addMergedRegion(utils.getBorderedRegion(region, sheet, wb));              		
                	row.setHeightInPoints(cellTextFormat.dividedlines*defHeight); */
                } else /* TEXT AREA */ if(element.getEntityName().equals("element.TextAreaElement")){
                	
                	pair=data.getTextAreaElementPair(valueResult, element);
                	
                  	row = sheet.createRow(++rowIndex);
                  	int textAreaLines=0;                  	
                  	cellTextFormat=utils.formatCellText(pair.label, labelColWidth);
        			textAreaLines=cellTextFormat.dividedlines;
        			utils.putBorderedBasicCell(sheet,rowIndex, 2,cellTextFormat.formattedText);        		
        			
        			cellTextFormat=utils.formatCellText(pair.value, valueColWidth);
        			textAreaLines=Math.max(textAreaLines, cellTextFormat.dividedlines);
        			utils.putBorderedBasicCell(sheet,rowIndex, 3,cellTextFormat.formattedText);

        			row.setHeightInPoints(textAreaLines*defHeight);
                }/* TRIPLET */ if(element.getEntityName().equals("element.TripletsListElement")){
                	 
                	 if (valueResult != null && valueResult.isValueDefined()) {
                			row = sheet.createRow(++rowIndex);
                			row.setHeightInPoints(ExportConstants.TITLE_ROW_HEIGHT);
                		    utils.putBorderedBasicCell(sheet,rowIndex, 2,element.getLabel());
                            region = new CellRangeAddress(rowIndex,rowIndex, 2,data.getNumbOfCols());
                      		sheet.addMergedRegion(utils.getBorderedRegion(region, sheet, wb));
                      	
                         for (ListableValue s : valueResult.getValuesObject()) {
                        	 final TripletValueDTO tripletValue=(TripletValueDTO) s;
                        	 row = sheet.createRow(++rowIndex);
                        	 StringBuilder builder=new StringBuilder(tripletValue.getName());
                        	 builder.append("(");
                        	 builder.append(tripletValue.getCode());
                        	 builder.append(")");
                        	 putElement(sheet, rowIndex, builder.toString(), tripletValue.getPeriod());
                        	 row.getCell(2).getCellStyle().setIndention((short)2);
                        	 row.getCell(3).getCellStyle().setIndention((short)2);
                          }
                       
                     } 
                 }/* CHOICE */ if(element.getEntityName().equals("element.QuestionElement")){
                	 
                	 if (valueResult != null && valueResult.isValueDefined()) {
                		 
                		 	final QuestionElementDTO questionElement=(QuestionElementDTO)element;
                		 	if(questionElement.getIsMultiple()){
                		 		row = sheet.createRow(++rowIndex);
                	        	row.setHeightInPoints(ExportConstants.TITLE_ROW_HEIGHT);
                	        	utils.putBorderedBasicCell(sheet,rowIndex, 2,element.getLabel());
                	        	int choiceStart=rowIndex;
                                final List<Long> selectedChoicesId =
                                	ValueResultUtils.splitValuesAsLong(valueResult.getValueObject());
                                boolean firstChoice=true;
                                for(QuestionChoiceElementDTO choice:questionElement.getChoicesDTO()){
                                	String prefix="[-] ";
                                	 for (Long id : selectedChoicesId) {
                                		 if (id == choice.getId()) {
                                             prefix="[+] ";
                                         }
                                	 }
                                	 if(!firstChoice){
                                		row = sheet.createRow(++rowIndex);
                         	        	row.setHeightInPoints(ExportConstants.TITLE_ROW_HEIGHT);
                                	 }
                                	 firstChoice=false;
                                	utils.putBorderedBasicCell(sheet,rowIndex, 3,prefix+choice.getLabel()); 
                		 		}
                                
                                region = new CellRangeAddress(choiceStart, rowIndex, 2, 2);
                        		sheet.addMergedRegion(utils.getBorderedRegion(region, sheet, wb));  
                                
                		 	}else{
                		 		String value=null;
                		 		final String idChoice = (String) valueResult.getValueObject();                                                       		 	
                		 		String[] choices=new String[questionElement.getChoicesDTO().size()];
                		 		int index=0;
                		 		for(QuestionChoiceElementDTO choice:questionElement.getChoicesDTO()){
                		 			choices[index++]=choice.getLabel();
                		 			 if (idChoice.equals(String.valueOf(choice.getId()))) {
                                         value=choice.getLabel();
                                     }
                		 		}
                		 		row = sheet.createRow(++rowIndex);
                		 		putElement(sheet, rowIndex, element.getLabel(), value);
                            	utils.addDropDownList(sheet, rowIndex, rowIndex, 3, 3,
                        				Arrays.asList(choices));                                	

                		 	}                                
                     } 
                 }
             
    		}// elements    		     		
    	}
    	
    	region = new CellRangeAddress(typeStartRow, rowIndex, 1, 1);
		sheet.addMergedRegion(utils.getBorderedRegion(region, sheet, wb));   
	
	return rowIndex;	
	}
	
	
	private void putElement(HSSFSheet sheet,int rowIndex,String label,String value){
		row = sheet.createRow(rowIndex);
		row.setHeightInPoints(ExportConstants.TITLE_ROW_HEIGHT);
    	utils.putBorderedBasicCell(sheet,rowIndex, 2,label);
    	utils.putBorderedBasicCell(sheet,rowIndex, 3,value);
	}
	
	
	
	@Override
	public void write(OutputStream output) throws Throwable {
		wb.write(output);
		
	}

}
