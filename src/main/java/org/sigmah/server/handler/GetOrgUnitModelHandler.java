package org.sigmah.server.handler;

import org.sigmah.server.dao.OrgUnitModelDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.OrgUnitModel;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetOrgUnitModel;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Handler for {@link GetOrgUnitModel} command.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetOrgUnitModelHandler extends AbstractCommandHandler<GetOrgUnitModel, OrgUnitModelDTO> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(GetOrgUnitModelHandler.class);

	/**
	 * Injected {@link OrgUnitModelDAO}.
	 */
	@Inject
	private OrgUnitModelDAO orgUnitModelDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OrgUnitModelDTO execute(final GetOrgUnitModel cmd, final UserExecutionContext context) throws CommandException {

		final Integer modelId = cmd.getId();

		LOG.debug("Retrieving orgUnit model with id '{}'.", modelId);

		final OrgUnitModel model = orgUnitModelDAO.findById(modelId);

		if (model == null) {
			LOG.debug("OrgUnit model id#{} doesn't exist.", modelId);
			return null;
		}

		LOG.debug("Found orgUnit model with id #{}.", modelId);

		return mapper().map(model, OrgUnitModelDTO.class, cmd.getMappingMode());
	}

}
