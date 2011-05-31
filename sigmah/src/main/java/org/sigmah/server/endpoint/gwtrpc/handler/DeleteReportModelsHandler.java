/**
 * 
 */
package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.logging.LogFactory;
import org.sigmah.shared.command.DeleteReportModels;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.ProjectReportModelResult;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.report.ProjectReportModel;
import org.sigmah.shared.domain.report.ProjectReportModelSection;
import org.sigmah.shared.dto.report.ProjectReportModelSectionDTO;
import org.sigmah.shared.dto.report.ReportModelDTO;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;



/**
 * The handler to delete report models or report models sections
 *
 *@author HUZHE (zhe.hu32@gmail.com)
 */
public class DeleteReportModelsHandler implements CommandHandler<DeleteReportModels> {
	
	private EntityManager em;
	private final static org.apache.commons.logging.Log LOG = LogFactory.getLog(DeleteReportModelsHandler.class);
	

	
	/**
	 * @param em
	 */
	@Inject
	public DeleteReportModelsHandler(EntityManager em) {

		this.em = em;
	}



	@Override
	public CommandResult execute(DeleteReportModels cmd, User user)
			throws CommandException {
		
		List<ReportModelDTO> reportModelDTOList = cmd.getReportModelList();
		List<ProjectReportModelSectionDTO> sectionDTOList = cmd.getSectionList();
		
		if(reportModelDTOList!=null)
		{//Delete the report models
			
			for(ReportModelDTO model:reportModelDTOList)
			{
			   ProjectReportModel reportModel = em.find(ProjectReportModel.class, model.getId());
			   if(reportModel!=null)
				   LOG.debug("Deleting the report model ID: "+reportModel.getId()+" Name: "+reportModel.getName());
			        em.remove(reportModel); //Delete cascade will delete all sections
			        
			}
			
			//Commit the changes
			em.flush();
		}
		
		
		
		if(sectionDTOList!=null)
		{//Delete the sections
			for(ProjectReportModelSectionDTO sectionDTO:sectionDTOList)
			{
				ProjectReportModelSection section = em.find(ProjectReportModelSection.class, sectionDTO.getId());
				if(section!=null)
					LOG.debug("Deleting the section ID: "+section.getId()+" Name: "+section.getName());
					em.remove(section);  //Delete cascade will delete all sub-sections
			}
			
			//Commit the changes
			em.flush();
			
			
			 //Return a new ReportModelDTO
		    ProjectReportModel model = em.find(ProjectReportModel.class, cmd.getReportModelId());
				
			ReportModelDTO reportModelToReturn = new ReportModelDTO();
		    reportModelToReturn.setId(model.getId());
		    reportModelToReturn.setName(model.getName());
		    reportModelToReturn.setOrganizationId(model.getOrganization().getId());
		           
		    List<ProjectReportModelSectionDTO>sectionDTOsToBeReturn = new ArrayList<ProjectReportModelSectionDTO>();           
		    LOG.debug("The size of section from db: "+model.getSections().size());
		    for(ProjectReportModelSection section:model.getSections())
		     {   
		       LOG.debug("name of root section: "+section.getName());
		       sectionDTOsToBeReturn.add(mappedIntoSectionDTO(section));
		     }
		              
		    reportModelToReturn.setSectionsDTO(sectionDTOsToBeReturn);
		       
		    return new ProjectReportModelResult(reportModelToReturn);
			
			
		}
		
		
		  
		
		return null;
	}
	
	
	
	
	  /**
     * 
     * Method to map a section into sectionDTO.This method ensure the the mapping between section and 
     * sectionDTO is correctly made. Using dozer.mapper causes ID not mapped correctly.
     * 
     * @param section
     * @return
     * 
     * @author HUZHE (zhe.hu32@gmail.com)
     * 
     */
	
    private ProjectReportModelSectionDTO mappedIntoSectionDTO(ProjectReportModelSection section)
	    {
	    	if(section==null)
	    	{
	    		return null;
	    	}
	    	
	    	LOG.debug("Section "+section.getName()+" enters. ID is: "+section.getId());
	    	
	    	ProjectReportModelSectionDTO sectionDTO = new ProjectReportModelSectionDTO();        
	         sectionDTO.setId(section.getId());
	  	     sectionDTO.setIndex(section.getIndex());
	  	     sectionDTO.setName(section.getName());
	  	     sectionDTO.setNumberOfTextarea(section.getNumberOfTextarea());
	  	     sectionDTO.setParentSectionModelId(section.getParentSectionModelId());
	  	     sectionDTO.setProjectModelId(section.getProjectModelId());
	  	     if(section.getSubSections()==null || section.getSubSections().size()==0)
	  	     {
	  	    	 sectionDTO.setSubSectionsDTO(null);
	  	    	 return sectionDTO;
	  	     }
	  	     else
	  	     {
	  	     List<ProjectReportModelSectionDTO>sectionDTOList = new ArrayList<ProjectReportModelSectionDTO>();
	  	     for(ProjectReportModelSection subSection:section.getSubSections())
	  	        {
	  	    	   //Recursive 
	  	    	   sectionDTOList.add(mappedIntoSectionDTO(subSection));
	  	        }
	  	    	 
	  	     sectionDTO.setSubSectionsDTO(sectionDTOList);
	  	     return sectionDTO;
	  	     }
	         
	    	
	   
	    }
	

}
