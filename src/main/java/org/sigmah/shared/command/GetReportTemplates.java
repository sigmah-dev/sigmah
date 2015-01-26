package org.sigmah.shared.command;

import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ReportDefinitionDTO;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetReportTemplates extends GetListCommand<ListResult<ReportDefinitionDTO>> {

	private Integer databaseId;
	private Integer templateId;

	public GetReportTemplates() {
		// Serialization.
	}

	public Integer getDatabaseId() {
		return databaseId;
	}

	public void setDatabaseId(Integer databaseId) {
		this.databaseId = databaseId;
	}

	public Integer getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	public static GetReportTemplates byTemplateId(int id) {
		GetReportTemplates cmd = new GetReportTemplates();
		cmd.setTemplateId(id);

		return cmd;
	}

}
