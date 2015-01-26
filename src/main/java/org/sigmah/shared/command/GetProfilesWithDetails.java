package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.profile.ProfileDTO;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetProfilesWithDetails extends AbstractCommand<ListResult<ProfileDTO>> {

	/**
	 * Profile identifier
	 */
	private Integer profileId;

	/**
	 * The optional mapping mode.
	 */
	private ProfileDTO.Mode mappingMode;

	protected GetProfilesWithDetails() {
		// Serialization.
	}

	/**
	 * Retrieves the authenticated user's organization profile<u>s</u>.<br>
	 * The command result may contain several profiles.
	 * 
	 * @param mappingMode
	 *          The optional mapping mode. Set to {@code null} to process a default mapping.
	 */
	public GetProfilesWithDetails(ProfileDTO.Mode mappingMode) {
		this(null, mappingMode);
	}

	/**
	 * Retrieves the authenticated user's organization profile corresponding to the given {@code profileId}.<br>
	 * The command result only contains one profile.
	 * 
	 * @param profileId
	 *          The profile id.
	 * @param mappingMode
	 *          The optional mapping mode. Set to {@code null} to process a default mapping.
	 */
	public GetProfilesWithDetails(Integer profileId, ProfileDTO.Mode mappingMode) {
		this.profileId = profileId;
	}

	public Integer getProfileId() {
		return profileId;
	}

	public ProfileDTO.Mode getMappingMode() {
		return mappingMode;
	}

}
