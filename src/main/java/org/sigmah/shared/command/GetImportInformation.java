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
	private boolean commit;

	protected GetImportInformation() {
		// Serialization.
	}
	
	public GetImportInformation(String fileName, ImportationSchemeDTO scheme) {
		this.fileName = fileName;
		this.scheme = scheme;
	}
	
	public GetImportInformation(String fileName, boolean commit) {
		this.fileName = fileName;
		this.commit = commit;
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

	/**
	 * @return the commit flag.
	 */
	public boolean isCommit() {
		return commit;
	}
	
}
