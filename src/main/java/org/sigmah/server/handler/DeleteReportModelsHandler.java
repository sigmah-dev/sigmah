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

import java.util.List;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.report.ProjectReportModel;
import org.sigmah.server.domain.report.ProjectReportModelSection;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.DeleteReportModels;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.report.ProjectReportModelSectionDTO;
import org.sigmah.shared.dto.report.ReportModelDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;

/**
 * The handler to delete report models or report models sections
 * 
 * @author HUZHE (zhe.hu32@gmail.com) (v1.3)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class DeleteReportModelsHandler extends AbstractCommandHandler<DeleteReportModels, ReportModelDTO> {

	/**
	 * Logger.
	 */
	private final static Logger LOG = LoggerFactory.getLogger(DeleteReportModelsHandler.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReportModelDTO execute(final DeleteReportModels cmd, final UserExecutionContext context) throws CommandException {

		final List<ReportModelDTO> reportModelDTOList = cmd.getReportModelList();
		final List<ProjectReportModelSectionDTO> sectionDTOList = cmd.getSectionList();

		return performDelete(reportModelDTOList, sectionDTOList, cmd.getReportModelId());
	}

	/**
	 * Delete the given models and sections in a transaction.
	 * 
	 * @param reportModelDTOList List of report models to delete (can be <code>null</code>).
	 * @param sectionDTOList List of section to delete (can be <code>null</code>).
	 * @param reportModelId Identifier of the parent report model.
	 * @return The report model or <code>null</code> if the list of sections is <code>null</code>.
	 */
	@Transactional
	protected ReportModelDTO performDelete(final List<ReportModelDTO> reportModelDTOList, final List<ProjectReportModelSectionDTO> sectionDTOList, final int reportModelId) {
		if (reportModelDTOList != null) {
			// Delete the report models.
			for (final ReportModelDTO model : reportModelDTOList) {

				final ProjectReportModel reportModel = em().find(ProjectReportModel.class, model.getId());

				if (reportModel != null) {
					LOG.debug("Deleting the following report model: {}.", reportModel);
					// Delete in cascade will delete all sections.
					em().remove(reportModel);
				}
			}

			// Commit the changes.
			em().flush();
		}

		if (sectionDTOList != null) {
			// Delete the sections.
			for (final ProjectReportModelSectionDTO sectionDTO : sectionDTOList) {
				final ProjectReportModelSection section = em().find(ProjectReportModelSection.class, sectionDTO.getId());
				if (section != null) {
					LOG.debug("Deleting the following section: {}.", section);
					em().remove(section); // Delete cascade will delete all sub-sections
				}
			}

			// Commit the changes.
			em().flush();

			// Finding project report model.
			final ProjectReportModel model = em().find(ProjectReportModel.class, reportModelId);
			return mapper().map(model, new ReportModelDTO());
		}

		return null;
	}

}
