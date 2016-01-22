package org.sigmah.server.handler;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


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
