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

import org.sigmah.server.dao.GlobalExportDAO;
import org.sigmah.server.dao.GlobalExportSettingsDAO;
import org.sigmah.server.dao.OrganizationDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.export.GlobalExportSettings;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.shared.command.GetGlobalExportSettings;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.GlobalExportSettingsDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Provides {@link GlobalExportSettingsDTO} for given organization.
 * 
 * @author sherzod
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetGlobalExportSettingsHandler extends AbstractCommandHandler<GetGlobalExportSettings, GlobalExportSettingsDTO> {

	/**
	 * Logger.
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(GetGlobalExportSettingsHandler.class);

	private final GlobalExportDAO globalExportDAO;
	private final GlobalExportSettingsDAO globalExportSettingsDAO;
	private final OrganizationDAO organizationDAO;
	private final Mapper mapper;

	@Inject
	public GetGlobalExportSettingsHandler(GlobalExportDAO globalExportDAO, GlobalExportSettingsDAO globalExportSettingsDAO, OrganizationDAO organizationDAO, Mapper mapper) {
		this.globalExportDAO = globalExportDAO;
		this.globalExportSettingsDAO = globalExportSettingsDAO;
		this.organizationDAO = organizationDAO;
		this.mapper = mapper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GlobalExportSettingsDTO execute(final GetGlobalExportSettings cmd, final UserExecutionContext context) throws CommandException {

		// Retrieving Organization.
		final Integer organizationId = cmd.getOrganizationId();
		if (organizationId == null) {
			throw new CommandException("Invalid organization id.");
		}

		// Retrieving global export settings.
		final GlobalExportSettings settings = globalExportSettingsDAO.getGlobalExportSettingsByOrganization(organizationId);
		final GlobalExportSettingsDTO result = mapper.map(settings, new GlobalExportSettingsDTO());

		if (!cmd.isRetrieveProjectModels()) {
			return result;
		}

		final List<ProjectModel> pModels = globalExportDAO.getProjectModelsByOrganization(organizationDAO.findById(organizationId));
		final List<ProjectModelDTO> pModelDTOs = new ArrayList<ProjectModelDTO>();

		for (final ProjectModel model : pModels) {
			if (model.getStatus() != ProjectModelStatus.DRAFT) {
				pModelDTOs.add(mapper.map(model, new ProjectModelDTO()));
			}
		}
		result.setProjectModelsDTO(pModelDTOs);

		return result;
	}

}
