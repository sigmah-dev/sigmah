package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.dto.report.ProjectReportDTO;

/**
 * Turn a draft into the current version of a project report.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class PromoteProjectReportDraft extends AbstractCommand<ProjectReportDTO> {

	private int reportId;
	private int versionId;

	public PromoteProjectReportDraft() {
		// Serialization.
	}

	public PromoteProjectReportDraft(int reportId, int versionId) {
		this.reportId = reportId;
		this.versionId = versionId;
	}

	public int getReportId() {
		return reportId;
	}

	public void setReportId(int reportId) {
		this.reportId = reportId;
	}

	public int getVersionId() {
		return versionId;
	}

	public void setVersionId(int versionId) {
		this.versionId = versionId;
	}
}
