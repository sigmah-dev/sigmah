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
import javax.persistence.NoResultException;
import org.sigmah.shared.command.GetProjectReports;
import org.sigmah.shared.dto.report.ReportReference;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.ProjectReportListResult;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.profile.PrivacyGroup;
import org.sigmah.shared.domain.profile.PrivacyGroupPermission;
import org.sigmah.shared.domain.profile.Profile;
import org.sigmah.shared.domain.report.ProjectReport;
import org.sigmah.shared.domain.value.Value;
import org.sigmah.shared.exception.CommandException;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GetProjectReportsHandler implements CommandHandler<GetProjectReports> {
    private EntityManager em;

    @Inject
    public GetProjectReportsHandler(EntityManager em) {
        this.em = em;
    }

    @Override
    public CommandResult execute(GetProjectReports cmd, User user) throws CommandException {
        final ArrayList<ReportReference> references = new ArrayList<ReportReference>();

        final Query query;

        if(cmd.getProjectId() != null) {
            query = em.createQuery("SELECT r FROM ProjectReport r WHERE r.project.id = :projectId");
            query.setParameter("projectId", cmd.getProjectId());

        } else if(cmd.getOrgUnitId()!=null){
        	query = em.createQuery("SELECT r FROM ProjectReport r WHERE r.orgUnit.id = :orgUnitId");
            query.setParameter("orgUnitId", cmd.getOrgUnitId());
            
        } else if(cmd.getReportId() != null) {
            query = em.createQuery("SELECT r FROM ProjectReport r WHERE r.id = :reportId");
            query.setParameter("reportId", cmd.getReportId());
        } else
            throw new IllegalArgumentException("GetProjectReports should either specify a project id or a report id.");

        try {
            final List<ProjectReport> reports = query.getResultList();

            for(final ProjectReport report : reports){
            	if(isViewableByUser(report, user)) {
            		references.add(new ReportReference(report));
            	}
            }
               

        } catch (NoResultException e) {
            // No reports in the current project
        }

        return new ProjectReportListResult(references);
    }

    
    /***
	 * Check if the user has the right to see the report
	 * @param pr the project report
	 * @param user the user
	 * @return boolean indicating the right of the user regarding the report
	 */
    public boolean isViewableByUser(ProjectReport pr, User user) {
		boolean right = false;
		final PrivacyGroup documentPG = pr.getFlexibleElement().getPrivacyGroup();
		if(documentPG == null) {
			right = true;
		} else {
			for(Profile profile : user.getOrgUnitWithProfiles().getProfiles()) {
				for( PrivacyGroupPermission pgp : profile.getPrivacyGroupPermissions()){
					if(pgp.getPrivacyGroup() == documentPG) {
						right = true;
						return right;
					}
				}
			}
		}
		return right;
	}
}
