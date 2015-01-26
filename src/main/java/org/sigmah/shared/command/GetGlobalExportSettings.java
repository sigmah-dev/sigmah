package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.dto.GlobalExportSettingsDTO;
import org.sigmah.shared.dto.ProjectModelDTO;

/**
 * See {@link #GetGlobalExportSettings(Integer, boolean)} for JavaDoc.
 * 
 * @author sherzod
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetGlobalExportSettings extends AbstractCommand<GlobalExportSettingsDTO> {

	private Integer organizationId;
	private boolean retrieveProjectModels;

	public GetGlobalExportSettings() {
		// Serialization.
	}

	/**
	 * Retrieves the {@code organizationId} corresponding {@link GlobalExportSettingsDTO} configuration.
	 * 
	 * @param organizationId
	 *          The Organization id.
	 * @param retrieveProjectModels
	 *          {@code true} to also retrieve {@code organizationId} corresponding {@link ProjectModelDTO} list (can be
	 *          greedy), {@code false} to only retrieve {@link GlobalExportSettingsDTO} configuration.
	 */
	public GetGlobalExportSettings(final Integer organizationId, final boolean retrieveProjectModels) {
		this.organizationId = organizationId;
		this.retrieveProjectModels = retrieveProjectModels;
	}

	public Integer getOrganizationId() {
		return organizationId;
	}

	public boolean isRetrieveProjectModels() {
		return retrieveProjectModels;
	}

}
