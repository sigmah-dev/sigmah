/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.command;

import org.sigmah.shared.command.result.ProjectReportListResult;

/**
 * Request to retrieves the reports attached to a given project or the given orgUnit.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GetProjectReports implements Command<ProjectReportListResult> {

    private static final long serialVersionUID = -5074144662654783191L;

    private Integer projectId;
    private Integer reportId;
    private Integer orgUnitId;

    public GetProjectReports() {
    }

    public GetProjectReports(Integer projectId, Integer orgUnitId) {
        this.projectId = projectId;
        this.orgUnitId = orgUnitId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

	public Integer getOrgUnitId() {
		return orgUnitId;
	}

	public void setOrgUnitId(Integer orgUnitId) {
		this.orgUnitId = orgUnitId;
	}
}
