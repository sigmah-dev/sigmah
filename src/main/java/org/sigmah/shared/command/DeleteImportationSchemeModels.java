package org.sigmah.shared.command;

import java.util.List;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.VoidResult;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class DeleteImportationSchemeModels extends AbstractCommand<VoidResult> {

	private List<Integer> importationSchemeIdsList;
	private List<Integer> variableFlexibleElemementIdsList;

	public DeleteImportationSchemeModels() {
		// Serialization.
	}

	public List<Integer> getImportationSchemeIdsList() {
		return importationSchemeIdsList;
	}

	public void setImportationSchemeIdsList(List<Integer> importationSchemeIdsList) {
		this.importationSchemeIdsList = importationSchemeIdsList;
	}

	public List<Integer> getVariableFlexibleElemementIdsList() {
		return variableFlexibleElemementIdsList;
	}

	public void setVariableFlexibleElemementIdsList(List<Integer> variableFlexibleElemementIdsList) {
		this.variableFlexibleElemementIdsList = variableFlexibleElemementIdsList;
	}

}
