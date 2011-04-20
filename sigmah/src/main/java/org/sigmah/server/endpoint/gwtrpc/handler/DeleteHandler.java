/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.sigmah.client.page.dashboard.CreateProjectWindow.Mode;
import org.sigmah.shared.command.Delete;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.domain.Deleteable;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.UserDatabase;
import org.sigmah.shared.domain.report.ProjectReport;
import org.sigmah.shared.domain.report.ProjectReportVersion;
import org.sigmah.shared.domain.report.RichTextElement;
import org.sigmah.shared.domain.value.Value;

import com.google.inject.Inject;

/**
 * @author Alex Bertram
 * @see org.sigmah.shared.command.Delete
 * @see org.sigmah.shared.domain.Deleteable
 */
public class DeleteHandler implements CommandHandler<Delete> {
    private EntityManager em;

    @Inject
    public DeleteHandler(EntityManager em) {
        this.em = em;
    }

    @Override
    public CommandResult execute(Delete cmd, User user) {

        // TODO check permissions for delete!
        // These handler should redirect to one of the Entity policy classes.
    	Class entityClass = entityClassForEntityName(cmd.getEntityName());
    	
    	if(Mode.TEST.equals(cmd.getMode())){
    		Project entity = (Project)em.find(entityClass, cmd.getId());
    		deleteTestProject(entity);
    	}else{
            Deleteable entity = (Deleteable) em.find(entityClass, cmd.getId());

            entity.delete();
    	}

        return null;
    }

    private Class<Deleteable> entityClassForEntityName(String entityName) {
        try {
            return (Class<Deleteable>) Class.forName(UserDatabase.class.getPackage().getName() + "." + entityName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Invalid entity name '" + entityName + "'");
        } catch (ClassCastException e) {
            throw new RuntimeException("Entity type '" + entityName + "' not Deletable");
        }
    }
    
	/**
	 * Delete the test project object.
	 * 
	 * @param project
	 *            the object to delete.
	 */
    private void deleteTestProject(Project project){
    	//delete the project flexible elements 
    	deleteProjectFlexibleElement(project);
    	
    	//delete the test project
    	em.remove(project);
    }
   
    /**
     * Delete the values of the test project.
     * @param project
     */
    private void deleteProjectFlexibleElement(Project project){
    	//delete values
    	Query query = em.createQuery("Select v FROM Value v WHERE v.containerId =:containerId");
        query.setParameter("containerId", project.getId());
        final List<Value> listResultsValues = (List<Value>) query.getResultList();
    	if(listResultsValues!=null){
    		for(Value value : listResultsValues){
    			em.remove(value);
    		}
    	}
    	
    	//delete project reports
    	query = em.createQuery("Select pr FROM ProjectReport pr WHERE pr.project.id =:databaseid");
        query.setParameter("databaseid", project.getId());
        final List<ProjectReport> listResultReports = (List<ProjectReport>) query.getResultList();
    	if(listResultReports!=null){
    		for(ProjectReport report : listResultReports){
    			//Delete the project report's version
    			ProjectReportVersion version = report.getCurrentVersion();
    			if(version!=null){
    				//delete vercion's richText elements
    				List<RichTextElement> richTextElements = version.getTexts();
    				if(richTextElements!=null){
    					for(RichTextElement richTextElement: richTextElements){
    						em.remove(richTextElement);
    					}
    				}
    				
    				em.remove(version);
    			}
    			em.remove(report);
    		}
    	}
    }
        
}
