package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ImportInformationResult;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetImportInformation extends AbstractCommand<ImportInformationResult> {

	private String fileName;
	private ImportationSchemeDTO scheme;

	protected GetImportInformation() {
		// Serialization.
	}
	
	public GetImportInformation(String fileName, ImportationSchemeDTO scheme) {
		this.fileName = fileName;
		this.scheme = scheme;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return the scheme
	 */
	public ImportationSchemeDTO getScheme() {
		return scheme;
	}

}
