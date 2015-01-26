package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.report.ReportReference;

/**
 * Request to retrieves the reports attached to a given project or the given orgUnit.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr) (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetProjectReports extends AbstractCommand<ListResult<ReportReference>> {

	private Integer projectId;
	private Integer reportId;
	private Integer orgUnitId;

	public GetProjectReports() {
		// Serialization.
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
