package org.sigmah.server.handler;

import java.util.Date;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.report.ProjectReport;
import org.sigmah.server.domain.report.ProjectReportVersion;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.PromoteProjectReportDraft;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.report.ProjectReportDTO;

import com.google.inject.persist.Transactional;

/**
 * Handler for the {@link PromoteProjectReportDraft} command.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class PromoteProjectReportDraftHandler extends AbstractCommandHandler<PromoteProjectReportDraft, ProjectReportDTO> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectReportDTO execute(final PromoteProjectReportDraft cmd, final UserExecutionContext context) throws CommandException {

		final ProjectReport report = em().find(ProjectReport.class, cmd.getReportId());
		final ProjectReportVersion version = em().find(ProjectReportVersion.class, cmd.getVersionId());

		version.setVersion(report.getCurrentVersion().getVersion() + 1);
		version.setEditDate(new Date());
		version.setEditor(context.getUser());
		report.setCurrentVersion(version);

		updateReport(report);

		return GetProjectReportHandler.toDTO(report, version);
	}

	/**
	 * Update to project report in a transaction.
	 * 
	 * @param report Project report to update.
	 */
	@Transactional
	protected void updateReport(final ProjectReport report) {
		em().merge(report);
	}

}
