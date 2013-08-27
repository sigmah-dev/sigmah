package org.sigmah.shared.command.result;

import java.util.List;

import org.sigmah.shared.domain.ImportDetails;

/**
 * Result for command {@link GetImportInformation}
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 */
public class ImportInformationResult implements CommandResult {

	
	private static final long serialVersionUID = 7846128691191021492L;

	List<ImportDetails> entitiesToImport;

	public ImportInformationResult() {

	}

	/**
	 * @return the entitiesToImport
	 */
	public List<ImportDetails> getEntitiesToImport() {
		return entitiesToImport;
	}

	/**
	 * @param entitiesToImport
	 *            the entitiesToImport to set
	 */
	public void setEntitiesToImport(List<ImportDetails> entitiesToImport) {
		this.entitiesToImport = entitiesToImport;
	}

}
