package org.sigmah.shared.command;

import java.util.List;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.VoidResult;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class DeleteImportationSchemes extends AbstractCommand<VoidResult> {

	private List<Integer> schemaIdsList;
	private List<Integer> variableIdsList;

	public DeleteImportationSchemes() {
		// Serialization.
	}

	public List<Integer> getSchemaIdsList() {
		return schemaIdsList;
	}

	public void setSchemaIdsList(List<Integer> schemaIdsList) {
		this.schemaIdsList = schemaIdsList;
	}

	public List<Integer> getVariableIdsList() {
		return variableIdsList;
	}

	public void setVariableIdsList(List<Integer> variableIdsList) {
		this.variableIdsList = variableIdsList;
	}

}
