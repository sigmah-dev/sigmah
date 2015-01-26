package org.sigmah.shared.command;

import java.util.List;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.DeleteResult;
import org.sigmah.shared.dto.profile.ProfileDTO;

/**
 * <p>
 * Command to delete profile(s).
 * </p>
 * <p>
 * If the returned map contains values, it means that errors have been detected and some profiles have not been deleted.
 * <br>
 * Otherwise, no error was detected and all the profiles have been deleted.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class DeleteProfiles extends AbstractCommand<DeleteResult<ProfileDTO>> {

	private List<ProfileDTO> profiles;

	protected DeleteProfiles() {
		// Serialization.
	}

	/**
	 * @param profiles
	 *          The profiles to delete.
	 */
	public DeleteProfiles(final List<ProfileDTO> profiles) {
		this.profiles = profiles;
	}

	/**
	 * @return The profiles to delete.
	 */
	public List<ProfileDTO> getProfiles() {
		return profiles;
	}

}
