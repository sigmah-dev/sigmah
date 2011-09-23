/**
 * 
 */
package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.sigmah.shared.command.CheckModelUsage;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.ModelCheckResult;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

/**
 * @author HUZHE (zhe.hu32@gmail.com)
 *
 */
public class CheckModelUsageHandler implements CommandHandler<CheckModelUsage>{

	private EntityManager em;
	
	@Inject
	public CheckModelUsageHandler(EntityManager em)
	{
		this.em =em;
	}
	
	
	@SuppressWarnings({ "unchecked" })
	@Override
	public CommandResult execute(CheckModelUsage cmd, User user)
			throws CommandException {
		
	   
		ModelCheckResult result = new ModelCheckResult();
		
		if(cmd.getModelType().equals(CheckModelUsage.ModelType.ProjectModel) && cmd.getProjectModelId()!=null)
		{//Check if project model is ever used by project
			
			String queryStr = "SELECT p FROM Project p WHERE p.projectModel.id =:projectModelId";
			Query query = em.createQuery(queryStr);
			query.setParameter("projectModelId", cmd.getProjectModelId());
			
			List<Project> projects = (List<Project> ) query.getResultList();
			
			if(projects!=null && projects.size()>0)
			{
				
				result.setUsed(true);
				
			}
			else
			{
				
				result.setUsed(false);
			}
			
			
			
		}
		else if(cmd.getModelType().equals(CheckModelUsage.ModelType.OrgUnitModel) && cmd.getOrgUnitModelId()!=null)
		{//Check if orgunit model is ever used
			
			String queryStr = "SELECT o FROM OrgUnit o WHERE o.orgUnitModel.id =:orgUnitModelId";
			Query query = em.createQuery(queryStr);
			query.setParameter("orgUnitModelId", cmd.getOrgUnitModelId());
			
			List<OrgUnit> orgUnits = (List<OrgUnit>) query.getResultList();
			
			if(orgUnits!=null && orgUnits.size()>0)
			{
				result.setUsed(true);
			}
			else
			{
				result.setUsed(false);
			}
			
		}
		
		
		
		
		return result;
	}
	
	

}
