package org.sigmah.server.endpoint.export.sigmah.spreadsheet.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.sigmah.server.endpoint.export.sigmah.Exporter;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.ExportConstants.MultiItemText;
import org.sigmah.shared.command.Command;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.command.result.ValueResultUtils;
import org.sigmah.shared.domain.Country;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementContainer;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.QuestionChoiceElementDTO;
import org.sigmah.shared.dto.element.TextAreaElementDTO;
import org.sigmah.shared.dto.value.ListableValue;
import org.sigmah.shared.dto.value.TripletValueDTO;

public class ProjectSynthesisData extends ExportData{
 

	public static class ElementPair{
		public String label;
		public Object value;
		public ElementPair(String label,Object value){
			this.label=label;
			this.value=value;
		}
	}
	
	
	 private final ProjectDTO project;
	 private final EntityManager entityManager;
	 private final List<Command> commands= new ArrayList<Command>(1);
 	 private final DefaultFlexibleElementContainer defaultContainer;
	 
	public ProjectSynthesisData(			
			final Exporter exporter,
			final ProjectDTO project,
			final EntityManager entityManager) {
		super(exporter, 3);
		this.project=project;
		this.entityManager=entityManager;
		defaultContainer=(DefaultFlexibleElementContainer)project;
  	}

	public ProjectDTO getProject() {
		return project;
	}
	
	public String clearHtmlFormatting(String text){
		if(text!=null && text.length()>0){
			text = text.replaceAll("<br>", " ");
			text = text.replaceAll("<[^>]+>|\\n", "");
			text = text.trim().replaceAll(" +", " ");
		}
		return text;
	}
	
	public MultiItemText formatTripletValues(List<ListableValue> list){
		int lines=list.size()+1;
  		final StringBuilder builder=new StringBuilder();
  		for (ListableValue s : list) {
        	 final TripletValueDTO tripletValue=(TripletValueDTO) s;
        	 builder.append(" - ");                       	 
        	 builder.append(tripletValue.getCode());
        	 builder.append(" - ");
        	 builder.append(tripletValue.getName());
        	 builder.append(" : ");
        	 builder.append(tripletValue.getPeriod());
        	 builder.append("\n");
   		}
  		 String value=null;
         if(lines>1){
         	value= builder.substring(0, builder.length()-2);
         	lines--;       
         }
         
         return new MultiItemText(value, lines);
	}
	
	public MultiItemText formatMultipleChoices(List<QuestionChoiceElementDTO> list,String values){
		  final List<Long> selectedChoicesId =
          	ValueResultUtils.splitValuesAsLong(values);
           final StringBuffer builder=new StringBuffer();
           int lines=1;
           for(QuestionChoiceElementDTO choice:list){
           	 for (Long id : selectedChoicesId) {
          		 if (id == choice.getId()) {
          			 builder.append(" - ");
          			 builder.append(choice.getLabel());
          			 builder.append("\n");
          			 lines++;
                   }
          	 }                                	                                 	
           }
          String value=null;
          if(lines>1){
          	value = builder.substring(0, builder.length()-2);
          	lines--;       
          }
          return new MultiItemText(value, lines);
	}
	
	public ValueResult getValue(int elementId, String entityName)
			throws Throwable {
		
		Integer amendmentId = null;
		if (project.getCurrentAmendment() != null)
			amendmentId = project.getCurrentAmendment().getId();
		
		// command to get element value
		final GetValue command = new GetValue(project.getId(), elementId,
				entityName, amendmentId);

		commands.clear();
		commands.add(command);
		return (ValueResult) exporter.executeCommands(commands);
	}
	
	public String getCountryName(Integer countryId) throws Throwable{ 		 
		return entityManager.find(Country.class, countryId).getName();		
	}
	
	public String getUserName(Integer userId) throws Throwable{
		User u = entityManager.find(User.class, userId);
		String name =u.getFirstName() != null ? 
				u.getFirstName() + " " + u.getName() : u.getName();
		return name;		
	}
	
	public String getOrgUnitName(Integer orgUnitId) throws Throwable{
		String name="";
		OrgUnit orgUnit = entityManager.find(OrgUnit.class, orgUnitId);
		if(orgUnit!=null)
			name =orgUnit.getName() + " - " + orgUnit.getFullName();
		return name;		
	}
 
	public DefaultFlexibleElementContainer getDefaultContainer() {
		return defaultContainer;
	}

	public ElementPair getTextAreaElementPair(final ValueResult valueResult,
			final FlexibleElementDTO element){
		Object value=null;

		final TextAreaElementDTO textAreaElement=
    		(TextAreaElementDTO)element;
      	  if (valueResult != null && valueResult.isValueDefined()) {
    		  String strValue=(String) valueResult.getValueObject();
    		  if(textAreaElement.getType()!=null){
    			  switch (textAreaElement.getType()) {
                  // Number
                      case 'N': {    	                        	  
                          if(textAreaElement.getIsDecimal()){		                        		  
                    		  value=Double.parseDouble(strValue);
                    	  }else{
                    		  value=Long.parseLong(strValue); 
                    		  
                    	  }    	                        	 
                      }break;
                      case 'D': {    	                        	  
                    	  value=new Date(Long.parseLong(strValue));                       	 
                      }break;    	                          
                      default : {    	                        	  
                    	  value=strValue;                       	 
                      }break;    	                         
            	  }  
    		  }else{
    			  value=strValue; 
    		  }
    		   
    	  }
		
     	return new ElementPair(element.getLabel(), value);
	}
	
	public ElementPair getCheckboxElementPair(final ValueResult valueResult,
			final FlexibleElementDTO element){
		String value=null;
		value=getLocalizedVersion("no");
    	if (valueResult != null && valueResult.getValueObject() != null) {
            if(((String) valueResult.getValueObject()).equalsIgnoreCase("true"))
            	value=getLocalizedVersion("yes");
            	
    	}
     	return new ElementPair(element.getLabel(), value);
	}
	
	public ElementPair getDefElementPair(final ValueResult valueResult,
			final FlexibleElementDTO element) throws Throwable{

		Object value=null;
		String label=null;
     	
    	final DefaultFlexibleElementDTO defaultElement=
    		(DefaultFlexibleElementDTO)element;
    	                    	
    	boolean hasValue= valueResult != null && valueResult.isValueDefined();
    	
    	switch(defaultElement.getType()){
	    	case CODE:{
	    		label=getLocalizedVersion("projectName");
	    		if(hasValue){
	    			value=valueResult.getValueObject();
	    		}else{
	    			value=getDefaultContainer().getName();
	    		}
	    	}break;
	    	case TITLE:{
	    		label=getLocalizedVersion("projectFullName");
	    		if(hasValue){
	    			value=valueResult.getValueObject();
	    		}else{
	    			value=getDefaultContainer().getFullName();
	    		}
	    	}break;
	    	case START_DATE:{
	     		label=getLocalizedVersion("projectStartDate");
	    		if(hasValue){
	    			value=new Date(Long.parseLong(valueResult.getValueObject()));
	    		}else{
	    			value=getDefaultContainer().getStartDate();
	    		}
	    	}break;
	    	case END_DATE:{
	    		label=getLocalizedVersion("projectEndDate");
	    		if(hasValue){
	    			value=new Date(Long.parseLong(valueResult.getValueObject()));
	    		}else{
	    			value="";
	    			if(getDefaultContainer().getEndDate()!=null)
	    				value=getDefaultContainer().getEndDate();
	    		}
	    	}break;
	    	case BUDGET:{
				label = getLocalizedVersion("projectBudget");
				Double pb = 0d;
				Double sb = 0d;
	
				if (hasValue) {
					final String[] parts = valueResult.getValueObject().split("~");
					pb = Double.parseDouble(parts[0]);
					sb = Double.parseDouble(parts[1]);
				} else {
					pb = getDefaultContainer().getPlannedBudget();
					sb = getDefaultContainer().getSpendBudget();
				}
				value = sb + " / " + pb;
	    	}break;
	    	case COUNTRY:{
	    		label=getLocalizedVersion("projectCountry");
	     		if(hasValue){
	    			int countryId = Integer.parseInt(valueResult.getValueObject());
	    			value = getCountryName(countryId);
	     		}else{
	     			value = getDefaultContainer().getCountry().getName();
	    		}
	    	}break;
	    	case OWNER:{
	    		label=getLocalizedVersion("projectOwner");
	     		if(hasValue){
	     			value=valueResult.getValueObject();
	     		}else{
	     			value = getDefaultContainer().getOwnerFirstName() != null ? 
	     					getDefaultContainer().getOwnerFirstName()+ " "+ getDefaultContainer().getOwnerName() 
	     					: getDefaultContainer().getOwnerName();
	    		}
	    	}break;
	    	case MANAGER:{
	    		label=getLocalizedVersion("projectManager");
	     		if(hasValue){
	     			int userId = Integer.parseInt(valueResult.getValueObject());                     			 
	     			value= getUserName(userId);
	     		}else{
	     			 UserDTO u = getDefaultContainer().getManager();
	     			 value = u.getFirstName() != null ? 
	     					 u.getFirstName() + " " + u.getName() : u.getName();                     			 
	    		}
	    	}break;
	    	case ORG_UNIT:{
	    		label=getLocalizedVersion("orgunit");
	    		int orgUnitId=-1;
	     		if(hasValue){
	     			  orgUnitId = Integer.parseInt(valueResult.getValueObject());                     			                      			
	     		}else{
	     			orgUnitId=getDefaultContainer().getOrgUnitId();                     			                   			
	    		}
	     		value= getOrgUnitName(orgUnitId);
	    	}break;
    	
    	} 
    	return new ElementPair(label, value);
	}
	

}
