package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.report.ReportModelDTO;

/**
 * GetReportModels command.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetReportModels extends AbstractCommand<ListResult<ReportModelDTO>> {

	public GetReportModels() {
		// Serialization.
	}

}
