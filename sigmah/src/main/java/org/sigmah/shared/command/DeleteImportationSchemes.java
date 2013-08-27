package org.sigmah.shared.command;

import java.util.List;

import org.sigmah.shared.command.result.VoidResult;

public class DeleteImportationSchemes implements Command<VoidResult>{

	/**
	 * 
	 */
    private static final long serialVersionUID = 371315467160738977L;
    
    private List<Long> schemaIdsList;
    private List<Long> variableIdsList;
    
    public DeleteImportationSchemes(){}
    
    
	public List<Long> getSchemaIdsList() {
	    return schemaIdsList;
    }
	
	public void setSchemaIdsList(List<Long> schemaIdsList) {
	    this.schemaIdsList = schemaIdsList;
    }

	public List<Long> getVariableIdsList() {
	    return variableIdsList;
    }

	public void setVariableIdsList(List<Long> variableIdsList) {
	    this.variableIdsList = variableIdsList;
    }

}
