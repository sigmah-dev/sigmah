package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.profile.ProfileDTO;

/**
 * GetProfiles command.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetProfiles extends AbstractCommand<ListResult<ProfileDTO>> {

	/**
	 * The mapping mode. Set to {@code null} to ignore.
	 */
	private ProfileDTO.Mode mappingMode;

	protected GetProfiles() {
		// Serialization.
	}

	/**
	 * The type of model of the models for the current user organization (set to <code>null</code> to ignore this filter).
	 * 
	 * @param mappingMode
	 *          The mapping mode, or {@code null} to ignore and process a full mapping.
	 */
	public GetProfiles(final ProfileDTO.Mode mappingMode) {
		this.mappingMode = mappingMode;
	}

	public ProfileDTO.Mode getMappingMode() {
		return mappingMode;
	}

}
