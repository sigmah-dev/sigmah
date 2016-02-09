package org.sigmah.shared.command;

import com.extjs.gxt.ui.client.data.BaseModelData;
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;

/**
 * Command to import a file without asking the user what he wants to do.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public class AutomatedImport extends AbstractCommand<ListResult<BaseModelData>> {

	private String fileId;
	private String fileName;
	private ImportationSchemeDTO scheme;
	
	private boolean createProjects;
	private boolean unlockProjectCores;
	private boolean updateAllMatches;

	/**
	 * Empty constructor, required by the serialization.
	 */
	public AutomatedImport() {
		// No initialization.
	}

	/**
	 * Creates a new command with the given parameters.
	 * 
	 * @param fileId
	 *			Identifier of the file to import.
	 * @param fileName
	 *			Name of the file to import.
	 * @param scheme
	 *			Import scheme to use for the import.
	 * @param createProjects
	 *			<code>true</code> to create projects if none matches,
	 *			<code>false</code> to ignore them.
	 * @param unlockProjectCores
	 *			<code>true</code> to unlock the project core if a change requires it,
	 *			<code>false</code> to ignore these changes.
	 * @param updateAllMatches 
	 *			<code>true</code> to all the projects matching the identification key,
	 *			<code>false</code> to skip those.
	 */
	public AutomatedImport(String fileId, String fileName, ImportationSchemeDTO scheme, boolean createProjects, boolean unlockProjectCores, boolean updateAllMatches) {
		this.fileId = fileId;
		this.fileName = fileName;
		this.scheme = scheme;
		this.createProjects = createProjects;
		this.unlockProjectCores = unlockProjectCores;
		this.updateAllMatches = updateAllMatches;
	}

	public String getFileId() {
		return fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public ImportationSchemeDTO getScheme() {
		return scheme;
	}

	public boolean isCreateProjects() {
		return createProjects;
	}

	public boolean isUnlockProjectCores() {
		return unlockProjectCores;
	}

	public boolean isUpdateAllMatches() {
		return updateAllMatches;
	}
	
}
