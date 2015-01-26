package org.sigmah.server.handler;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.sigmah.server.dao.ProfileDAO;
import org.sigmah.server.dao.UserDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.profile.Profile;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.DeleteProfiles;
import org.sigmah.shared.command.result.DeleteResult;
import org.sigmah.shared.command.result.DeleteResult.DeleteErrorCause;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

/**
 * Handler for {@link DeleteProfiles} command.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class DeleteProfilesHandler extends AbstractCommandHandler<DeleteProfiles, DeleteResult<ProfileDTO>> {

	/**
	 * Logger.
	 */
	private final static Logger LOG = LoggerFactory.getLogger(DeleteProfilesHandler.class);

	/**
	 * Injected {@link UserDAO}.
	 */
	@Inject
	private UserDAO userDAO;

	/**
	 * Injected {@link ProfileDAO}.
	 */
	@Inject
	private ProfileDAO profileDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DeleteResult<ProfileDTO> execute(final DeleteProfiles cmd, final UserExecutionContext context) throws CommandException {

		// Profiles to delete.
		final List<ProfileDTO> profiles = cmd.getProfiles();

		// Result that may contain detected error(s).
		final DeleteResult<ProfileDTO> result = new DeleteResult<ProfileDTO>();

		if (CollectionUtils.isEmpty(profiles)) {
			return result;
		}

		// Delete the profiles
		performDelete(profiles, result, context);

		return result;
	}

	/**
	 * Delete the given profiles.
	 * 
	 * @param profiles List of profiles to delete.
	 * @param result List of delete result.
	 * @param context Execution context.
	 */
	@Transactional
	protected void performDelete(final List<ProfileDTO> profiles, final DeleteResult<ProfileDTO> result, final UserExecutionContext context) {
		for (final ProfileDTO profileDTO : profiles) {

			if (profileDTO == null) {
				continue;
			}

			final Profile profile = profileDAO.findById(profileDTO.getId());

			if (profile == null) {
				continue;
			}

			if (userDAO.countUsersByProfile(profile.getId()) > 0) {
				// Profile is referenced by user(s) ; it cannot be deleted.
				for (final User user : userDAO.findUsersByProfile(profile.getId())) {
					result.addError(profileDTO, new DeleteErrorCause(user.getName()));
				}

			} else {
				// Profile is not referenced by user(s) ; it can be deleted.
				LOG.debug("Deleting the following profile: {}", profile);
				profileDAO.remove(profile, context.getUser());
				result.addDeleted(profileDTO);
			}
		}
	}

}
