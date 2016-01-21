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

import org.sigmah.client.util.AdminUtil;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.report.ProjectReportModel;
import org.sigmah.server.domain.report.ProjectReportModelSection;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.UpdateProjectReportModel;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.report.ProjectReportModelSectionDTO;
import org.sigmah.shared.dto.report.ReportModelDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;

/**
 * Command handler to update report model
 * 
 * @author HUZHE (zhe.hu32@gmail.com) (v1.3)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class UpdateProjectReportModelHandler extends AbstractCommandHandler<UpdateProjectReportModel, ReportModelDTO> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(UpdateProjectReportModelHandler.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReportModelDTO execute(final UpdateProjectReportModel cmd, final UserExecutionContext context) throws CommandException {

		// --
		// Get all objects needed.
		// --

		final int reportModelId = cmd.getReportModelId();

		@SuppressWarnings("unchecked")
		List<ProjectReportModelSectionDTO> changedSectionsDTO = (List<ProjectReportModelSectionDTO>) cmd.getChanges().get(AdminUtil.PROP_REPORT_SECTION_MODEL);

		if (changedSectionsDTO == null) {
			return null;
		}

		// Update the sections.
		performUpdate(changedSectionsDTO);

		// --
		// Returns a new ReportModelDTO.
		// --

		final ProjectReportModel model = em().find(ProjectReportModel.class, reportModelId);

		return mapper().map(model, ReportModelDTO.class);
	}

	/**
	 * Update the given sections in a transaction.
	 * 
	 * @param changedSectionsDTO List of sections to update.
	 */
	@Transactional
	protected void performUpdate(List<ProjectReportModelSectionDTO> changedSectionsDTO) {
		// --
		// Begins to update.
		// --
		
		for (final ProjectReportModelSectionDTO sectionDTO : changedSectionsDTO) {
			
			final ProjectReportModelSection section;
			
			if (sectionDTO.getId() == null || sectionDTO.getId() < 0) {
				// Creates a new section.
				LOG.debug("Creating a new section from DTO: {}", sectionDTO);
				section = new ProjectReportModelSection();

			} else {
				// Updates an existing section.
				LOG.debug("Updates the existing section corresponding to DTO: {}", sectionDTO);
				section = em().find(ProjectReportModelSection.class, sectionDTO.getId());
			}

			section.setIndex(sectionDTO.getIndex());
			section.setName(sectionDTO.getName());
			section.setNumberOfTextarea(sectionDTO.getNumberOfTextarea());
			section.setParentSectionModelId(sectionDTO.getParentSectionModelId());
			section.setProjectModelId(sectionDTO.getProjectModelId());

			em().merge(section);
		}

		// --
		// Commit the changes.
		// --

		em().flush();
	}

}
