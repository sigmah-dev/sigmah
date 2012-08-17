package org.sigmah.server.endpoint.export.sigmah.spreadsheet.template;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.CellRange;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.CalcUtils;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.ExportConstants;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.ExportConstants.MultiItemText;
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

public class ProjectSynthesisCalcTemplate implements ExportTemplate {

	private final Table table;
	private final SpreadsheetDocument doc;
	private final ProjectSynthesisData data;
 	
	private Row row;
	private Cell cell;
 	private CellRange cellRange;
	private String coreCellStyle;
  
	public ProjectSynthesisCalcTemplate(final ProjectSynthesisData data,final SpreadsheetDocument doc) throws Throwable {
		this.data=data;
 		this.doc=doc;
		table = doc.getSheetByIndex(0);
		table.setTableName(data.getLocalizedVersion("projectSynthesis").replace(" ", "_"));
		coreCellStyle = CalcUtils.prepareCoreStyle(doc);
 		int rowIndex = -1;
 
		//skip row
		++rowIndex;		 

		// title
		CalcUtils.putMainTitle(table,++rowIndex,data.getNumbOfCols(),
				data.getLocalizedVersion("projectSynthesis").toUpperCase());		

		//emptry row
		CalcUtils.putEmptyRow(table,++rowIndex);

		// column headers
		row = table.getRowByIndex(++rowIndex);
 		CalcUtils.putHeader(row,2,  data.getLocalizedVersion("adminFlexibleName"));
		CalcUtils.putHeader(row,3,data.getLocalizedVersion("value"));
 		row.setHeight(5, false);

		//empty row
		row = table.getRowByIndex(++rowIndex);
		row.setHeight(3.8, false);
		row.getCellByIndex(1).setCellStyleName(null);
		row.getCellByIndex(2).setCellStyleName(null);
		row.getCellByIndex(3).setCellStyleName(null);
 
		//details
		row = table.getRowByIndex(++rowIndex);
		CalcUtils.putHeader(row,1,data.getLocalizedVersion("projectDetails"));
		rowIndex=putLayout(table, 
				data.getProject().getProjectModelDTO().getProjectDetailsDTO().getLayoutDTO(), rowIndex);
		
		//empty row
		row = table.getRowByIndex(++rowIndex);
		row.setHeight(3.8, false);
		row.getCellByIndex(1).setCellStyleName(null);
		row.getCellByIndex(2).setCellStyleName(null);
		row.getCellByIndex(3).setCellStyleName(null);
		
		//run through project phases to get synthesis data
        for(final PhaseDTO phase : data.getProject().getPhasesDTO()){
        	
        	//phase name        	
        	row = table.getRowByIndex(++rowIndex);
        	CalcUtils.putHeader(row, 1, phase.getPhaseModelDTO().getName());
       		rowIndex=putLayout(table, phase.getPhaseModelDTO().getLayoutDTO(), rowIndex); 
  
        } // phases								

		table.getColumnByIndex(0).setWidth(3.8);
		table.getColumnByIndex(1).setWidth(49);
		table.getColumnByIndex(2).setWidth(115);
		table.getColumnByIndex(3).setWidth(115);		
	}
	
	private int putLayout(final Table table,final LayoutDTO layout,int rowIndex) throws Throwable{
		
		int typeStartRow=rowIndex;
		 boolean firstGroup=true;
  		//layout groups for each phase
    	for (final LayoutGroupDTO layoutGroup : layout.getLayoutGroupsDTO()) {

    		//layout group cell
    		if(!firstGroup){
    			row = table.getRowByIndex(++rowIndex);
    		}
    		firstGroup=false;	
     		CalcUtils.putGroupCell(table, 2, rowIndex, layoutGroup.getTitle());
     		CalcUtils.mergeCell(table, 2, rowIndex, data.getNumbOfCols(), rowIndex);
     		
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
                	putElement(table,++rowIndex,pair.label,pair.value);
                	
                }else /*CHECKBOX*/ if(element.getEntityName().equals("element.CheckboxElement")){
                	pair=data.getCheckboxElementPair(valueResult, element);
                	putElement(table,++rowIndex,pair.label,pair.value);                	                
                }else /* MESSAGE */ if(element.getEntityName().equals("element.MessageElement")){  
                	putElement(table,++rowIndex,
                			data.getLocalizedVersion("flexibleElementMessage"),
                			data.clearHtmlFormatting(element.getLabel()),true);                     	 
                } else /* TEXT AREA */ if(element.getEntityName().equals("element.TextAreaElement")){                	
                	pair=data.getTextAreaElementPair(valueResult, element);                	
                	putElement(table,++rowIndex,pair.label,pair.value);                	                  
                }/* TRIPLET */ if(element.getEntityName().equals("element.TripletsListElement")){
                	 if (valueResult != null && valueResult.isValueDefined()) {
                		 row = table.getRowByIndex(++rowIndex);
                		 CalcUtils.createBasicCell(table, 2, rowIndex, data.clearHtmlFormatting(element.getLabel()));
                    		
                         final MultiItemText item=data.formatTripletValues(valueResult.getValuesObject());
                         CalcUtils.createBasicCell(table, 3, rowIndex, item.text);                    		                     
                	 }                	
                 }/* CHOICE */ if(element.getEntityName().equals("element.QuestionElement")){
                	 
                	 if (valueResult != null && valueResult.isValueDefined()) {
                		 
                		 final QuestionElementDTO questionElement=(QuestionElementDTO)element;
             		 	if(questionElement.getIsMultiple()){
             		 		row = table.getRowByIndex(++rowIndex);  
             		 		CalcUtils.createBasicCell(table, 2, rowIndex, data.clearHtmlFormatting(element.getLabel()));
                              
             	        	final MultiItemText item=data.formatMultipleChoices(
                             		questionElement.getChoicesDTO(),valueResult.getValueObject());
             	        	 CalcUtils.createBasicCell(table, 3, rowIndex, item.text);                                  
                             
             		 	}else{
                		 		String value=null;
                		 		final String idChoice = (String) valueResult.getValueObject();                                                       		 	
                		 		for(QuestionChoiceElementDTO choice:questionElement.getChoicesDTO()){
                 		 			 if (idChoice.equals(String.valueOf(choice.getId()))) {
                                         value=choice.getLabel();
                                     }
                		 		}
                		 		putElement(table,++rowIndex,element.getLabel(),value);    
                 		 	}                                
                     } 
                 }
             
    		}// elements    		     		
    	}
		CalcUtils.mergeCell(table, 1, typeStartRow, 1, rowIndex);   
		
		return rowIndex;
		
	}
	
	private void putElement(Table table,int rowIndex,String label,Object value){
		putElement(table, rowIndex, label, value,false);  	
	}
	
	private void putElement(Table table,int rowIndex,String label,Object value,boolean isMessage){
		row = table.getRowByIndex(rowIndex);
		row.getCellByIndex(2).setCellStyleName(null);
		row.getCellByIndex(3).setCellStyleName(null);
		CalcUtils.createBasicCell(table, 2, rowIndex, data.clearHtmlFormatting(label));
		CalcUtils.createBasicCell(table, 3, rowIndex, value);
		if(isMessage)
			row.getCellByIndex(3).setFont(CalcUtils.getFont(11, false, true));
	}		

	@Override
	public void write(OutputStream output) throws Throwable {
		doc.save(output);
		doc.close();
	}

}