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
import org.sigmah.shared.command.GetReportModels;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.ReportModelsListResult;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.report.ProjectReportModel;
import org.sigmah.shared.dto.report.ReportModelDTO;
import org.sigmah.shared.exception.CommandException;

/**
 *
 * @author nrebiai
 */
public class GetReportModelsHandler implements CommandHandler<GetReportModels> {
    private EntityManager em;

    private static final Log log = LogFactory.getLog(GetReportModelsHandler.class);
    
    @Inject
    public GetReportModelsHandler(EntityManager em) {
        this.em = em;
    }

    @Override
    public CommandResult execute(GetReportModels cmd, User user) throws CommandException {
        final ArrayList<ReportModelDTO> reports = new ArrayList<ReportModelDTO>();

        final Query query = em.createQuery("SELECT r FROM ProjectReportModel r");

        @SuppressWarnings("unchecked")
		final List<ProjectReportModel> models = query.getResultList();
        log.debug("Size of reports found : " + models.size());
        for(final ProjectReportModel model : models){
        	ReportModelDTO reportModel = new ReportModelDTO();
        	reportModel.setId(model.getId());
        	reportModel.setName(model.getName());
        	reports.add(reportModel);
        }
            

        ReportModelsListResult result = new ReportModelsListResult(reports); 
        log.debug("Size of reports found : " + result.getList().size());
        
        return result;
    }

}
