package org.sigmah.shared.command;

import java.util.List;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.VoidResult;

/**
 * Deletes an importation scheme or a list of importation scheme variables.
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr) v2.0
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class DeleteImportationSchemes extends AbstractCommand<VoidResult> {

	private Integer schemaId;
	private List<Integer> variableIdsList;

	protected DeleteImportationSchemes() {
		// Serialization.
	}
	
	public DeleteImportationSchemes(Integer schemaId) {
		this.schemaId = schemaId;
	}
	
	public DeleteImportationSchemes(List<Integer> variableIdsList) {
		this.variableIdsList = variableIdsList;
	}

	public Integer getSchemaId() {
		return schemaId;
	}

	public List<Integer> getVariableIdsList() {
		return variableIdsList;
	}

}
