package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.shared.command.GetProjectModel;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.domain.ProjectModel;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.layout.LayoutGroup;
import org.sigmah.shared.domain.logframe.LogFrameModel;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.logframe.LogFrameModelDTO;
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
        
        if (model == null) {
            if (log.isDebugEnabled()) {
                log.debug("[execute] Project model id#" + cmd.getId() + " doesn't exist.");
            }

            return null;
        } else {
        	if (log.isDebugEnabled()) {
                log.debug("[execute] Found project model" + cmd.getId());
            }
        	
        	final Query query = em.createQuery("SELECT l FROM LogFrameModel l ORDER BY l.id");
        	for(LogFrameModel logFrame : (List<LogFrameModel>) query.getResultList()){
        		if(logFrame.getProjectModel() != null && logFrame.getProjectModel().getId().equals(model.getId())){
        			model.setLogFrameModel(logFrame);
        		}
        	}
    		
    		
        	ProjectModelDTO p = mapper.map(model, ProjectModelDTO.class);   
        	if(model.getLogFrameModel() != null){
        		LogFrameModelDTO logFrameModelDTO =mapper.map(model.getLogFrameModel(), LogFrameModelDTO.class);
            	p.setLogFrameModelDTO(logFrameModelDTO);
        	}
        	
        	return p;
        	
        }
	}

}
