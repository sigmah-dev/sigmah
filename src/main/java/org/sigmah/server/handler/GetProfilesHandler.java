package org.sigmah.server.handler;

import java.util.List;

import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.profile.Profile;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetProfiles;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for {@link GetProfiles} command.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetProfilesHandler extends AbstractCommandHandler<GetProfiles, ListResult<ProfileDTO>> {

	/**
	 * Logger.
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(GetProfilesHandler.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<ProfileDTO> execute(final GetProfiles cmd, final UserExecutionContext context) throws CommandException {

		final TypedQuery<Profile> query = em().createQuery("SELECT p FROM Profile p WHERE p.organization.id = :orgid ORDER BY p.name", Profile.class);
		query.setParameter("orgid", context.getUser().getOrganization().getId());

		final List<Profile> resultProfiles = query.getResultList();

		return new ListResult<ProfileDTO>(mapper().mapCollection(resultProfiles, ProfileDTO.class, cmd.getMappingMode()));
	}

}
