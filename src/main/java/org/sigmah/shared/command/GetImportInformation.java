package org.sigmah.shared.command;

import org.sigmah.shared.command.result.ImportInformationResult;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;

public class GetImportInformation implements Command<ImportInformationResult> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4956162541695505475L;
	
	private String fileName;
	private ImportationSchemeDTO scheme;
	
	public GetImportInformation(){
	
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the scheme
	 */
	public ImportationSchemeDTO getScheme() {
		return scheme;
	}

	/**
	 * @param scheme the scheme to set
	 */
	public void setScheme(ImportationSchemeDTO scheme) {
		this.scheme = scheme;
	}
	
	
	

}
