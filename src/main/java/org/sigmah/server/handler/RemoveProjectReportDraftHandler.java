package org.sigmah.server.handler;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.report.ProjectReportVersion;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.RemoveProjectReportDraft;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

/**
 * Handler for the {@link RemoveProjectReportDraft} command.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class RemoveProjectReportDraftHandler extends AbstractCommandHandler<RemoveProjectReportDraft, VoidResult> {

	

	@Inject
	public RemoveProjectReportDraftHandler() {
	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VoidResult execute(final RemoveProjectReportDraft cmd, final UserExecutionContext context) throws CommandException {
		removeReportVersion(cmd.getVersionId());
		return null;
	}
	
	@Transactional
	protected void removeReportVersion(int versionId) {
		final ProjectReportVersion version = em().find(ProjectReportVersion.class, versionId);
		em().remove(version);
	}

}
