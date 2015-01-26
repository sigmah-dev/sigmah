package org.sigmah.server.handler;

import org.apache.commons.lang3.StringUtils;
import org.sigmah.server.dao.OrganizationDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Organization;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.shared.command.UpdateOrganization;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.organization.OrganizationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Handler for {@link UpdateOrganization} command.
 * 
 * @author Aurélien Ponçon
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class UpdateOrganizationHandler extends AbstractCommandHandler<UpdateOrganization, OrganizationDTO> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(UpdateOrganizationHandler.class);

	private final Mapper mapper;
	private final OrganizationDAO organizationDAO;

	@Inject
	public UpdateOrganizationHandler(final Mapper mapper, final OrganizationDAO organizationDAO) {
		this.mapper = mapper;
		this.organizationDAO = organizationDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OrganizationDTO execute(final UpdateOrganization cmd, final UserExecutionContext context) throws CommandException {

		final OrganizationDTO form = cmd.getOrganization();

		if (form == null || form.getId() == null || StringUtils.isBlank(form.getName())) {
			throw new CommandException("Invalid command arguments: '" + cmd + "'.");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Persisting organization data: '{}'.", cmd);
		}

		final Organization organization = organizationDAO.findById(form.getId());

		if (organization == null) {
			throw new CommandException("Organization with id '" + form.getId() + "' cannot be found.");
		}

		organization.setName(form.getName());
		organization.setLogo(form.getLogo());

		organizationDAO.persist(organization, context.getUser());

		return mapper.map(organization, OrganizationDTO.class);
	}

}
