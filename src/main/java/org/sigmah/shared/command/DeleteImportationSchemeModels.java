package org.sigmah.shared.command;

import java.util.List;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.VoidResult;

/**
 * Remove the given importation scheme model or some of its variables.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class DeleteImportationSchemeModels extends AbstractCommand<VoidResult> {

	private List<Integer> importationSchemeIds;
	private List<Integer> variableFlexibleElemementIds;

	protected DeleteImportationSchemeModels() {
		// Serialization.
	}

	public DeleteImportationSchemeModels(List<Integer> importationSchemeIds, List<Integer> variableFlexibleElemementIds) {
		this.importationSchemeIds = importationSchemeIds;
		this.variableFlexibleElemementIds = variableFlexibleElemementIds;
	}

	public List<Integer> getImportationSchemeIds() {
		return importationSchemeIds;
	}

	public void setImportationSchemeIds(List<Integer> importationSchemeIdsList) {
		this.importationSchemeIds = importationSchemeIdsList;
	}

	public List<Integer> getVariableFlexibleElemementIds() {
		return variableFlexibleElemementIds;
	}

	public void setVariableFlexibleElemementIds(List<Integer> variableFlexibleElemementIdsList) {
		this.variableFlexibleElemementIds = variableFlexibleElemementIdsList;
	}

}
