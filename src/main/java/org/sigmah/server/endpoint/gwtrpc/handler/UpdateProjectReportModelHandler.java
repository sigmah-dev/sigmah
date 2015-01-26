/**
 * 
 */
package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.shared.command.UpdateProjectReportModel;
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
 * 
 * Command handler to update report model 
 * 
 * @author HUZHE (zhe.hu32@gmail.com)
 *
 */
public class UpdateProjectReportModelHandler implements CommandHandler<UpdateProjectReportModel> {
	
	
	private final EntityManager em;
	private final static Log LOG = LogFactory.getLog(UpdateProjectReportModelHandler.class);
	

	/**
	 * @param em
	 * @param mapper
	 */
	@Inject
	public UpdateProjectReportModelHandler(EntityManager em, Mapper mapper) {
		super();
		this.em = em;
	}


	@Override
	public CommandResult execute(UpdateProjectReportModel cmd, User user)
			throws CommandException {
		
		//Get all objects needed
		int reportModelId = cmd.getReportModelId();
		List<ProjectReportModelSectionDTO> changedSectionsDTO = (List<ProjectReportModelSectionDTO>) cmd.getChanges().get(AdminUtil.PROP_REPORT_SECTION_MODEL);		
		
		if(changedSectionsDTO==null)
		{
			return null;
		}
		
		
		//Begins to update
		for(ProjectReportModelSectionDTO sectionDTO:changedSectionsDTO)
		{
			if((Integer)sectionDTO.getId()==-1)
			{//New section
				
				LOG.debug("The new section before mapping ID: "+sectionDTO.getId()+" Parent section ID: "+sectionDTO.getParentSectionModelId()+" ReportModel ID :"+sectionDTO.getProjectModelId()+"\n");
				ProjectReportModelSection newSection = new ProjectReportModelSection();				
				LOG.debug("The new section after mapping ID: "+newSection.getId()+" Parent section ID: "+newSection.getParentSectionModelId()+" ReportModel ID :"+newSection.getProjectModelId()+"\n");
				newSection.setIndex(sectionDTO.getIndex());
				newSection.setName(sectionDTO.getName());
				newSection.setNumberOfTextarea(sectionDTO.getNumberOfTextarea());
				newSection.setParentSectionModelId(sectionDTO.getParentSectionModelId());
				newSection.setProjectModelId(sectionDTO.getProjectModelId());
				em.merge(newSection);

				
				
			}
			else
			{//Update the existed section
				ProjectReportModelSection section = em.find(ProjectReportModelSection.class, (Integer)(sectionDTO).getId());
				section.setIndex(sectionDTO.getIndex());
				section.setName(sectionDTO.getName());
				section.setNumberOfTextarea(sectionDTO.getNumberOfTextarea());
				section.setParentSectionModelId(sectionDTO.getParentSectionModelId());
				section.setProjectModelId(sectionDTO.getProjectModelId());
				em.merge(section);
				
			}
		}
	
	   //commit the changes
	   em.flush();
	  
	   
		
	   //Return a new ReportModelDTO
	   ProjectReportModel model = em.find(ProjectReportModel.class, reportModelId);
		
	   ReportModelDTO reportModelToReturn = new ReportModelDTO();
       reportModelToReturn.setId(model.getId());
       reportModelToReturn.setName(model.getName());
       reportModelToReturn.setOrganizationId(model.getOrganization().getId());
           
       List<ProjectReportModelSectionDTO>sectionDTOList = new ArrayList<ProjectReportModelSectionDTO>();           
       LOG.debug("The size of section from db: "+model.getSections().size());
       for(ProjectReportModelSection section:model.getSections())
       {   
         LOG.debug("name of root section: "+section.getName());
         sectionDTOList.add(mappedIntoSectionDTO(section));
       }
              
       reportModelToReturn.setSectionsDTO(sectionDTOList);
       
       return new ProjectReportModelResult(reportModelToReturn);
           	
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
