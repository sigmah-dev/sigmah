package org.sigmah.shared.command.result;

import java.util.List;

import org.sigmah.shared.dto.ImportDetails;

/**
 * Result for command {@link org.sigmah.shared.command.GetImportInformation GetImportInformation}.
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ImportInformationResult implements Result {

	private List<ImportDetails> entitiesToImport;

	public ImportInformationResult() {
		// Serialization.
	}

	/**
	 * @return the entitiesToImport
	 */
	public List<ImportDetails> getEntitiesToImport() {
		return entitiesToImport;
	}

	/**
	 * @param entitiesToImport
	 *          the entitiesToImport to set
	 */
	public void setEntitiesToImport(List<ImportDetails> entitiesToImport) {
		this.entitiesToImport = entitiesToImport;
	}

}
