package org.sigmah.server.endpoint.gwtrpc.handler;

import javax.persistence.EntityManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.shared.command.GetProjectModel;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.domain.ProjectModel;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.layout.LayoutGroup;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class GetProjectModelHandler implements CommandHandler<GetProjectModel> {

	private static final Log log = LogFactory.getLog(GetProjectModelHandler.class);
	
	private final EntityManager em;
	private final Mapper mapper;
	
	@Inject
    public GetProjectModelHandler(EntityManager em, Mapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }
			
	@Override
	public CommandResult execute(GetProjectModel cmd, User user)
			throws CommandException {
		
		if (log.isDebugEnabled()) {
            log.debug("[execute] Retrieving project model with id '" + cmd.getId() + "'.");
        }
		
		Long id = Long.valueOf(String.valueOf(cmd.getId()));

        final ProjectModel model = em.find(ProjectModel.class, id);

        if(model.getId() == 1){
        	for(LayoutGroup lg : model.getProjectBanner().getLayout().getGroups()){
        		log.debug("group for banner before mapping" + lg.getId());
        	}       	
        }
        
        if (model == null) {
            if (log.isDebugEnabled()) {
                log.debug("[execute] Project model id#" + cmd.getId() + " doesn't exist.");
            }

            return null;
        } else {
        	if (log.isDebugEnabled()) {
                log.debug("[execute] Found project model" + cmd.getId());
            }

        	return mapper.map(model, ProjectModelDTO.class);      
        	
        }
	}

}
