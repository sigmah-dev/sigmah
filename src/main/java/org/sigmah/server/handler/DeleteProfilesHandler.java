package org.sigmah.server.handler;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
