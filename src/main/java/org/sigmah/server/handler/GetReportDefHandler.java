package org.sigmah.server.handler;

import org.sigmah.server.dao.ReportDefinitionDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetReportDef;
import org.sigmah.shared.command.result.StringResult;
import org.sigmah.shared.dispatch.CommandException;

import com.google.inject.Inject;

/**
 * Handler for {@link GetReportDef} command
 * 
 * @author Alex Bertram (v1.3)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 */
public class GetReportDefHandler extends AbstractCommandHandler<GetReportDef, StringResult> {

	protected ReportDefinitionDAO reportDAO;

	@Inject
	public void setReportDAO(ReportDefinitionDAO dao) {
		this.reportDAO = dao;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StringResult execute(final GetReportDef cmd, final UserExecutionContext context) throws CommandException {
		return new StringResult(reportDAO.findById(cmd.getId()).getXml());
	}

}
