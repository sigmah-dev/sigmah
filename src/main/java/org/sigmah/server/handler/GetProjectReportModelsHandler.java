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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.report.ProjectReportModel;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetProjectReportModels;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.ModelReference;
import org.sigmah.shared.dispatch.CommandException;

/**
 * Handler for {@link GetProjectReportModels} command.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class GetProjectReportModelsHandler extends AbstractCommandHandler<GetProjectReportModels, ListResult<ModelReference>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<ModelReference> execute(final GetProjectReportModels cmd, final UserExecutionContext context) throws CommandException {

		final ArrayList<ModelReference> references = new ArrayList<ModelReference>();

		final TypedQuery<ProjectReportModel> query = em().createQuery("SELECT r FROM ProjectReportModel r", ProjectReportModel.class);

		try {

			final List<ProjectReportModel> models = query.getResultList();

			for (final ProjectReportModel model : models) {
				references.add(new ModelReference(model));
			}

		} catch (NoResultException e) {
			// No reports in the current project
		}

		return new ListResult<ModelReference>(references);
	}

}
