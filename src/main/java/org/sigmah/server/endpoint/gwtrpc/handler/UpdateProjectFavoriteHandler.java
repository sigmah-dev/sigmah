/**
 * 
 */
package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.HashSet;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.shared.command.UpdateProjectFavorite;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.dto.ProjectDTOLight;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

/**
 * @author HUZHE
 *
 */
public class UpdateProjectFavoriteHandler implements CommandHandler<UpdateProjectFavorite>{
	
	private EntityManager em;
	private ProjectMapper mapper;
	
	private final static Log LOG = LogFactory.getLog(UpdateProjectFavoriteHandler.class);

	

	/**
	 * @param em
	 */
	@Inject
	public UpdateProjectFavoriteHandler(EntityManager em, ProjectMapper mapper) {
		super();
		this.em = em;
		this.mapper = mapper;
	}





	@Override
	public CommandResult execute(UpdateProjectFavorite cmd, User user)
			throws CommandException {
		
		LOG.debug("[UpdateProjectFavorite]: user "+user.getEmail()+" userid "+user.getId()+" projectid "+cmd.getProjectId());
		
		Project project = em.find(Project.class, cmd.getProjectId());
		User u = em.find(User.class, user.getId());
		if(project == null || u ==null)
			return null;
		
		
		if(cmd.getUpdateType().equals(UpdateProjectFavorite.UpdateType.REMOVE))
		{
		   if(project.getFavoriteUsers()==null || !project.getFavoriteUsers().contains(u))
		   {
			   return null;
		   }
		   else
		   {
             project.getFavoriteUsers().remove(u);
		   }
       
		}
		else
		{
		  if(project.getFavoriteUsers()==null)
		  {
				project.setFavoriteUsers(new HashSet<User>());
		  }
		  
			project.getFavoriteUsers().add(u);
			
		}
        
	    Project resultProject =em.merge(project);
		
        ProjectDTOLight resultProjectDTOLight = mapper.map(resultProject, false);
        
        CreateResult result = new CreateResult(resultProjectDTOLight);
        
		return result;
		
	}
	
	

}
