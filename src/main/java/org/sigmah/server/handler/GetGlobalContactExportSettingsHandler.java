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

import com.google.inject.Inject;
import org.sigmah.server.dao.GlobalContactExportSettingsDAO;
import org.sigmah.server.dao.GlobalExportDAO;
import org.sigmah.server.dao.OrganizationDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.ContactModel;
import org.sigmah.server.domain.export.GlobalContactExportSettings;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.shared.command.GetGlobalContactExportSettings;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ContactModelDTO;
import org.sigmah.shared.dto.GlobalContactExportSettingsDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetGlobalContactExportSettingsHandler extends AbstractCommandHandler<GetGlobalContactExportSettings, GlobalContactExportSettingsDTO> {

	/**
	 * Logger.
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(GetGlobalContactExportSettingsHandler.class);

	private final GlobalExportDAO globalExportDAO;
	private final GlobalContactExportSettingsDAO globalContactExportSettingsDAO;
	private final OrganizationDAO organizationDAO;
	private final Mapper mapper;

	@Inject
	public GetGlobalContactExportSettingsHandler(GlobalExportDAO globalExportDAO, GlobalContactExportSettingsDAO globalContactExportSettingsDAO, OrganizationDAO organizationDAO, Mapper mapper) {
		this.globalExportDAO = globalExportDAO;
		this.globalContactExportSettingsDAO = globalContactExportSettingsDAO;
		this.organizationDAO = organizationDAO;
		this.mapper = mapper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GlobalContactExportSettingsDTO execute(final GetGlobalContactExportSettings cmd, final UserExecutionContext context) throws CommandException {

		// Retrieving Organization.
		final Integer organizationId = cmd.getOrganizationId();
		if (organizationId == null) {
			throw new CommandException("Invalid organization id.");
		}

		// Retrieving global export settings.
		final GlobalContactExportSettings settings = globalContactExportSettingsDAO.getGlobalExportSettingsByOrganization(organizationId);
		final GlobalContactExportSettingsDTO result = mapper.map(settings, new GlobalContactExportSettingsDTO());

		if (!cmd.isRetrieveContactModels()) {
			return result;
		}

		final List<ContactModel> pModels = globalExportDAO.getContactModels();
		final List<ContactModelDTO> pModelDTOs = new ArrayList<ContactModelDTO>();

		for (final ContactModel model : pModels) {
			if (model.getStatus() != ProjectModelStatus.DRAFT) {
				pModelDTOs.add(mapper.map(model, new ContactModelDTO()));
			}
		}
		result.setContactModelsDTO(pModelDTOs);

		return result;
	}

}
