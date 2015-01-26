package org.sigmah.server.endpoint.gwtrpc.handler;

import javax.persistence.EntityManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.shared.command.GetOrgUnitModel;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.domain.OrgUnitModel;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class GetOrgUnitModelHandler implements CommandHandler<GetOrgUnitModel> {

	private static final Log log = LogFactory.getLog(GetOrgUnitModelHandler.class);
	
	private final EntityManager em;
	private final Mapper mapper;
	
	@Inject
    public GetOrgUnitModelHandler(EntityManager em, Mapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }
			
	@Override
	public CommandResult execute(GetOrgUnitModel cmd, User user)
			throws CommandException {
		
		if (log.isDebugEnabled()) {
            log.debug("[execute] Retrieving orgUnit model with id '" + cmd.getId() + "'.");
        }
		
		Integer id = cmd.getId();

        final OrgUnitModel model = em.find(OrgUnitModel.class, id);
        
        if (model == null) {
            if (log.isDebugEnabled()) {
                log.debug("[execute] OrgUnit model id#" + cmd.getId() + " doesn't exist.");
            }

            return null;
        } else {
        	if (log.isDebugEnabled()) {
                log.debug("[execute] Found orgUnit model" + cmd.getId());
            }   		
    		
        	OrgUnitModelDTO o = mapper.map(model, OrgUnitModelDTO.class);   
        	
        	return o;
        	
        }
	}

}
