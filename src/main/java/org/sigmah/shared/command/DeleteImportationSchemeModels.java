package org.sigmah.shared.command;

import java.util.List;

import org.sigmah.shared.command.result.VoidResult;

public class DeleteImportationSchemeModels implements Command<VoidResult> {

    
    /**
	 * 
	 */
    private static final long serialVersionUID = 868084969103558611L;
	private List<Long> importationSchemeIdsList;
    private List<Long> variableFlexibleElemementIdsList;
    
    public DeleteImportationSchemeModels(){}
    
    
	public List<Long> getImportationSchemeIdsList() {
	    return importationSchemeIdsList;
    }
	
	public void setImportationSchemeIdsList(List<Long> importationSchemeIdsList) {
	    this.importationSchemeIdsList = importationSchemeIdsList;
    }

	public List<Long> getVariableFlexibleElemementIdsList() {
	    return variableFlexibleElemementIdsList;
    }

	public void setVariableFlexibleElemementIdsList(List<Long> variableFlexibleElemementIdsList) {
	    this.variableFlexibleElemementIdsList = variableFlexibleElemementIdsList;
    }

}
