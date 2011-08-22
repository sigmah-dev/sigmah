/**
 * 
 */
package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.shared.command.GetProjectsByModel;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.ProjectListResult;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.dto.ProjectDTOLight;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

/**
 * @author HUZHE
 *
 */
public class GetProjectsByModelHandler implements CommandHandler<GetProjectsByModel>{

	
	private final static Log LOG = LogFactory.getLog(GetProjectsByModelHandler.class);

    private final EntityManager em;
    private final Mapper mapper;

    @Inject
    public GetProjectsByModelHandler(EntityManager em, Mapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }
	
	@Override
	public CommandResult execute(GetProjectsByModel cmd, User user)
			throws CommandException {
		
		if(cmd==null || cmd.getProjectModelId()==null)
		{
			return null;
		}
		
		String qlString = "SELECT p from Project p WHERE p.projectModel.id= :projectId";
		Query query = em.createQuery(qlString);
		query.setParameter("projectId", cmd.getProjectModelId());
	
		List<Project> projectList = (List<Project>)query.getResultList();
		List<ProjectDTOLight> projectDTOList = new ArrayList<ProjectDTOLight>();
		
		if(projectList==null)
		{
			return null;
		}
		
		for(Project p : projectList)
		{
			projectDTOList.add(mapper.map(p, ProjectDTOLight.class));
		}
		
		
		ProjectListResult result = new ProjectListResult();
		result.setListProjectsLightDTO(projectDTOList);
		
		
		return  result;
	}
	
	

}
