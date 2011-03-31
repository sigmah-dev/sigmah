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
    public CommandResult execute(GetReportModels cmd, User user) throws CommandException {
        final ArrayList<ReportModelDTO> reports = new ArrayList<ReportModelDTO>();

        final Query query = em.createQuery("SELECT r FROM ProjectReportModel r ORDER BY r.id");

        @SuppressWarnings("unchecked")
		final List<ProjectReportModel> models = query.getResultList();
        for(final ProjectReportModel model : models){
        	ReportModelDTO reportModel = mapper.map(model, ReportModelDTO.class);
        	List<ProjectReportModelSectionDTO> sections = new ArrayList<ProjectReportModelSectionDTO>();
        	for(ProjectReportModelSection section : model.getSections()){
        		ProjectReportModelSectionDTO sectionDTO = new ProjectReportModelSectionDTO();
        		sectionDTO = mapper.map(section, ProjectReportModelSectionDTO.class);
        		sections.add(sectionDTO);
        	}       	
			reportModel.setSectionsDTO(sections);
        	reports.add(reportModel);
        }
            

        ReportModelsListResult result = new ReportModelsListResult(reports); 
        log.debug("Size of report models found : " + result.getList().size());
        
        return result;
    }

}
