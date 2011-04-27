package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.shared.command.UpdateLogFrame;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.LogFrameResult;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.logframe.LogFrame;
import org.sigmah.shared.dto.logframe.LogFrameActivityDTO;
import org.sigmah.shared.dto.logframe.LogFrameDTO;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

/**
 * Handler for update log frame command.
 * 
 * @author tmi
 * 
 */
public class UpdateLogFrameHandler implements CommandHandler<UpdateLogFrame> {

    private final EntityManager em;
    private final Mapper mapper;

    private final static Log LOG = LogFactory.getLog(UpdateLogFrameHandler.class);

    @Inject
    public UpdateLogFrameHandler(EntityManager em, Mapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }

    @Override
    public CommandResult execute(UpdateLogFrame cmd, User user) throws CommandException {

        LogFrameDTO logFrameDTO = cmd.getLogFrame();
        LogFrame logFrame = null;
        
        // Maps the log frame.
        if (logFrameDTO != null) {
            logFrame = mapper.map(logFrameDTO, LogFrame.class);
        }

        // Sets the log frame parent project.
        if (logFrame != null) {

            final Project project = new Project();
            project.setId(cmd.getProjectId());

            logFrame.setParentProject(project);
           
        }

        if (logFrame != null) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("[execute] Merges the log frame.");
            }

            // Merges log frame.
            logFrame = em.merge(logFrame);
            
            //Update the project activities advancement
            List<LogFrameActivityDTO> activities = logFrameDTO.getAllActivitiesDTO();
            int countActivities= 0;
            int projectActivitiesAdvancement = 0;
            
            if(activities!=null && !activities.isEmpty()){
            	for(LogFrameActivityDTO activity :activities){
            		countActivities++;
            		projectActivitiesAdvancement+=activity.getAdvancement();
            	}
            }            
            
            if(countActivities>0)
            	projectActivitiesAdvancement/=countActivities;
            
            logFrame.getParentProject().setActivityAdvancement(projectActivitiesAdvancement);
        	em.merge(logFrame.getParentProject());
        	
            // Re-map as DTO.
            logFrameDTO = mapper.map(logFrame, LogFrameDTO.class);
        }
         
        return new LogFrameResult(logFrameDTO);
    }

}
