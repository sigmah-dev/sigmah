package org.sigmah.server.endpoint.gwtrpc.handler;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.shared.command.UpdateOrganization;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.OrganizationResult;
import org.sigmah.shared.domain.Organization;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.dto.OrganizationDTO;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

/**
 * Handler for update organization command.
 * 
 * @author Aurélien Ponçon
 * 
 */
public class UpdateOrganizationHandler implements CommandHandler<UpdateOrganization> {
    private final EntityManager em;
    private final Mapper mapper;

    private final static Log LOG = LogFactory.getLog(UpdateLogFrameHandler.class);

    @Inject
    public UpdateOrganizationHandler(EntityManager em, Mapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }

    @Override
    public CommandResult execute(UpdateOrganization cmd, User user) throws CommandException {
        
        if(cmd.getOrganization() != null) {
            Organization ong = mapper.map(cmd.getOrganization(), Organization.class);
            
            if(cmd.getNewName() != null && !cmd.getNewName().isEmpty()) {
                
                if (LOG.isDebugEnabled()) {
                    LOG.debug("[execute] Merges the organization.");
                }
                
                ong.setName(cmd.getNewName());
                
                ong = em.merge(ong);
                
                OrganizationDTO dto = mapper.map(ong, OrganizationDTO.class);
                
                return new OrganizationResult(dto);                
            } else {
                return new OrganizationResult(cmd.getOrganization());
            }             
        } else {
            return null;
        }
    }
}
