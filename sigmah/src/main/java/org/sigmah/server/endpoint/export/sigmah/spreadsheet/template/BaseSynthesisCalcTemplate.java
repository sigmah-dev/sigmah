/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.server.endpoint.export.sigmah.spreadsheet.template;

import java.io.OutputStream;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.CellRange;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.CalcUtils;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.GlobalExportDataProvider;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.GlobalExportDataProvider.ValueLabel;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.data.BaseSynthesisData;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.PhaseModel;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.element.FlexibleElement;
import org.sigmah.shared.domain.layout.Layout;
import org.sigmah.shared.domain.layout.LayoutConstraint;
import org.sigmah.shared.domain.layout.LayoutGroup;


/*
 * Base calc template for project/orgunit calc templates
 * @author sherzod
 */
public class BaseSynthesisCalcTemplate implements ExportTemplate {

	private final Table table;
	private final SpreadsheetDocument doc;
	private final BaseSynthesisData data;
	private final Class clazz;
	
	private Row row;
	private Cell cell;
 	private CellRange cellRange;
	private String coreCellStyle;
  
	public BaseSynthesisCalcTemplate(
			final BaseSynthesisData data,
			final SpreadsheetDocument doc,
			final Class clazz) throws Throwable {
		this.data=data;
 		this.doc=doc;
		table = doc.getSheetByIndex(0);
		this.clazz=clazz;
		
		String title=data.getLocalizedVersion("projectSynthesis");
		if(clazz.equals(OrgUnit.class)) 
			title=data.getLocalizedVersion("orgUnitSynthesis");
		
		table.setTableName(title.replace(" ", "_"));
		coreCellStyle = CalcUtils.prepareCoreStyle(doc);
 		int rowIndex = -1;
 
		//skip row
		++rowIndex;		 

		// title
		CalcUtils.putMainTitle(table,++rowIndex,data.getNumbOfCols(),
				title.toUpperCase());		

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
		
		//Project synthesis
		if(clazz.equals(Project.class)){
			rowIndex=putLayout(table, 
					data.getProject().getProjectModel().getProjectDetails().getLayout(), rowIndex);
			
			//empty row
			row = table.getRowByIndex(++rowIndex);
			row.setHeight(3.8, false);
			row.getCellByIndex(1).setCellStyleName(null);
			row.getCellByIndex(2).setCellStyleName(null);
			row.getCellByIndex(3).setCellStyleName(null);
			
			//run through project phases to get synthesis data
	        for(final PhaseModel phaseModel : data.getProject().getProjectModel().getPhases()){
	        	
	        	//phase name        	
	        	row = table.getRowByIndex(++rowIndex);
	        	CalcUtils.putHeader(row, 1, phaseModel.getName());
	       		rowIndex=putLayout(table, phaseModel.getLayout(), rowIndex); 
	  
	        }  
		}else{
			//Org Unit synthesis
			rowIndex=putLayout(table,
	   				data.getOrgUnit().getOrgUnitModel().getDetails().getLayout(), rowIndex); 
		}		               

		table.getColumnByIndex(0).setWidth(3.8);
		table.getColumnByIndex(1).setWidth(49);
		table.getColumnByIndex(2).setWidth(115);
		table.getColumnByIndex(3).setWidth(115);		
	}
	
	private int putLayout(final Table table,final Layout layout,int rowIndex) throws Throwable{
		
		int typeStartRow=rowIndex;
		 boolean firstGroup=true;
  		//layout groups for each phase
    	for (final LayoutGroup layoutGroup : layout.getGroups()) {

    		//layout group cell
    		if(!firstGroup){
    			row = table.getRowByIndex(++rowIndex);
    		}
    		firstGroup=false;	
     		CalcUtils.putGroupCell(table, 2, rowIndex, layoutGroup.getTitle());
     		CalcUtils.mergeCell(table, 2, rowIndex, data.getNumbOfCols(), rowIndex);
     		
    			//elements for each layout group
     		for (final LayoutConstraint constraint : layoutGroup.getConstraints()) {
     			final FlexibleElement element = constraint.getElement();   
    			
                //skip if element is not exportable
    			if(!element.isExportable()) continue;
    			
    			final String elementName="element."+ element.getClass().getSimpleName();
    			Integer id=
    				(clazz.equals(Project.class))?data.getProject().getId():data.getOrgUnit().getId();
				final GetValue command = new GetValue(id,
						element.getId(), elementName, null); 
				 
				final ValueResult valueResult = (ValueResult)data.getHandler().execute(command, null);
					
					//prepare value and label
					ValueLabel pair=null;
					boolean isMessage=false;
					/*DEF FLEXIBLE*/
	            	if(elementName.equals("element.DefaultFlexibleElement")){                	
	                	pair=data.getDataProvider().getDefElementPair(
	                			valueResult, 
	                			element,
	                			data.getProject()!=null?data.getProject():data.getOrgUnit(),
	    	                	clazz,
	                			data.getEntityManager(),
	                			data.getLocale(),
	                			data.getTranslator());
 		                	
	                }/*CHECKBOX*/ else if(elementName.equals("element.CheckboxElement")){
	                	pair=data.getDataProvider().getCheckboxElementPair(
	                			valueResult, 
	                			element,
	                			data.getLocale(),
	                			data.getTranslator());
		             } /* TEXT AREA */ else if(elementName.equals("element.TextAreaElement")){                	
	                	pair=data.getDataProvider().getTextAreaElementPair(valueResult, element); 
 		                  	
	                }/* TRIPLET */ else if(elementName.equals("element.TripletsListElement")){
	                	 pair=data.getDataProvider().getTripletPair(element, valueResult);	                	 
	                	  
	                 }/* CHOICE */ else if(elementName.equals("element.QuestionElement")){		                	 
	                	 pair=data.getDataProvider().getChoicePair(element, valueResult); 
	                	 
	                 }/* MESSAGE */else if(elementName.equals("element.MessageElement")){ 
	                	 pair=new ValueLabel(data.getLocalizedVersion("flexibleElementMessage"), 
	                			 GlobalExportDataProvider.clearHtmlFormatting(element.getLabel()));
	                 	 isMessage=true;
	                }
	            	
	            	if(pair!=null)
	            		putElement(table,++rowIndex,pair,isMessage);
	            		             
             
    		}// elements    		     		
    	}
		CalcUtils.mergeCell(table, 1, typeStartRow, 1, rowIndex);   
		
		return rowIndex;
		
	}
 
	private void putElement(Table table,int rowIndex,ValueLabel pair,boolean isMessage){
		row = table.getRowByIndex(rowIndex);
		row.getCellByIndex(2).setCellStyleName(null);
		row.getCellByIndex(3).setCellStyleName(null);
		CalcUtils.createBasicCell(table, 2, rowIndex, pair.getFormattedLabel());
		CalcUtils.createBasicCell(table, 3, rowIndex, pair.getValue());
		if(isMessage)
			row.getCellByIndex(3).setFont(CalcUtils.getFont(11, false, true));
	}		

	@Override
	public void write(OutputStream output) throws Throwable {
		doc.save(output);
		doc.close();
	}
}
