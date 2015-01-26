package org.sigmah.server.endpoint.gwtrpc.handler;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.shared.command.GetOrganization;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.domain.Organization;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.dto.OrganizationDTO;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

/**
 * {@link GetOrganization} command implementation.
 * 
 * @author tmi
 * 
 */
public class GetOrganizationHandler implements CommandHandler<GetOrganization> {

    private final static Log LOG = LogFactory.getLog(GetOrganizationHandler.class);

    private final EntityManager em;
    private final Mapper mapper;

    @Inject
    public GetOrganizationHandler(EntityManager em, Mapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }

    @Override
    public CommandResult execute(GetOrganization cmd, User user) throws CommandException {

        if (LOG.isDebugEnabled()) {
            LOG.debug("[execute] Getting organization id#" + cmd.getId() + " from the database.");
        }

        final Organization organization = em.find(Organization.class, cmd.getId());

        if (organization == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("[execute] Organization id#" + cmd.getId() + " doesn't exist.");
            }
            return null;
        } else {
            return mapper.map(organization, OrganizationDTO.class);
        }
    }
}
