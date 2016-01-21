package org.sigmah.server.handler;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.handler.util.Handlers;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.shared.command.GetOrgUnit;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * {@link GetOrgUnit} command implementation.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class GetOrgUnitHandler extends AbstractCommandHandler<GetOrgUnit, OrgUnitDTO> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(GetOrgUnitHandler.class);

	private final Mapper mapper;

	@Inject
	public GetOrgUnitHandler(Mapper mapper) {
		this.mapper = mapper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OrgUnitDTO execute(GetOrgUnit cmd, final UserExecutionContext context) throws CommandException {

		if (LOG.isDebugEnabled()) {
			LOG.debug("Getting org unit with id #{} from the database.", cmd.getId());
		}

		final OrgUnit orgUnit = em().find(OrgUnit.class, cmd.getId());

		// No org unit.
		if (orgUnit == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Org unit with id # doesn't exist.", cmd.getId());
			}
			return null;
		}

		// The user cannot see this org unit.
		if (!Handlers.isOrgUnitVisible(orgUnit, context.getUser())) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("User cannot see org unit id #{}, returns null.", cmd.getId());
			}
			return null;
		}

		return mapper.map(orgUnit, new OrgUnitDTO());
	}

}
