package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.dto.report.ProjectReportDTO;

/**
 * Gets a project report from its id.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetProjectReport extends AbstractCommand<ProjectReportDTO> {

	private Integer reportId;

	protected GetProjectReport() {
		// Serialization.
	}

	public GetProjectReport(int reportId) {
		this.reportId = reportId;
	}

	public Integer getReportId() {
		return reportId;
	}

}
