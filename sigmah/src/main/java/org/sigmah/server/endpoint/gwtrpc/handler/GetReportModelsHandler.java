/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.endpoint.gwtrpc.handler;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.server.dao.Transactional;
import org.sigmah.shared.command.GetReportModels;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.ReportModelsListResult;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.report.ProjectReportModel;
import org.sigmah.shared.domain.report.ProjectReportModelSection;
import org.sigmah.shared.dto.report.ProjectReportModelSectionDTO;
import org.sigmah.shared.dto.report.ReportModelDTO;
import org.sigmah.shared.exception.CommandException;

/**
 *
 * @author nrebiai
 */
public class GetReportModelsHandler implements CommandHandler<GetReportModels> {
    private EntityManager em;
    private final Mapper mapper;

    private static final Log log = LogFactory.getLog(GetReportModelsHandler.class);
    
    @Inject
    public GetReportModelsHandler(EntityManager em, Mapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }

    @Override
    @Transactional 
    public CommandResult execute(GetReportModels cmd, User user) throws CommandException {
        final ArrayList<ReportModelDTO> reports = new ArrayList<ReportModelDTO>();

        final Query query = em.createQuery("SELECT r FROM ProjectReportModel r WHERE r.organization.id = :orgid  ORDER BY r.id");
        query.setParameter("orgid", user.getOrganization().getId());
        
        @SuppressWarnings("unchecked")
		final List<ProjectReportModel> models = query.getResultList();
        for(final ProjectReportModel model : models){
   	    
           ReportModelDTO reportModel = new ReportModelDTO();
           reportModel.setId(model.getId());
           reportModel.setName(model.getName());
           reportModel.setOrganizationId(model.getOrganization().getId());
           
           List<ProjectReportModelSectionDTO>sectionDTOList = new ArrayList<ProjectReportModelSectionDTO>();
           
           for(ProjectReportModelSection section:model.getSections())
           {   
        	   sectionDTOList.add(mappedIntoSectionDTO(section));
           }
           
         
           
            reportModel.setSectionsDTO(sectionDTOList);
              	              	      	
           
           
        	reports.add(reportModel);
        }
            

        ReportModelsListResult result = new ReportModelsListResult(reports); 
        log.debug("Size of report models found : " + result.getList().size());
        
        return result;
    }
    
   
    private ProjectReportModelSectionDTO mappedIntoSectionDTO(ProjectReportModelSection section)
    {
    	if(section==null)
    	{
    		return null;
    	}
    	
    	log.debug("Section "+section.getName()+" enters. ID is: "+section.getId());
    	
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
