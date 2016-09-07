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

import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import org.sigmah.server.dao.GlobalContactExportSettingsDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.export.GlobalContactExportSettings;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.UpdateGlobalContactExportSettingsCommand;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;

public class UpdateGlobalContactExportSettingsHandler extends AbstractCommandHandler<UpdateGlobalContactExportSettingsCommand, VoidResult> {

	private final GlobalContactExportSettingsDAO globalContactExportSettingsDAO;

	@Inject
	public UpdateGlobalContactExportSettingsHandler(final GlobalContactExportSettingsDAO globalContactExportSettingsDAO) {
		this.globalContactExportSettingsDAO = globalContactExportSettingsDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VoidResult execute(final UpdateGlobalContactExportSettingsCommand cmd, final UserExecutionContext context) throws CommandException {

		final GlobalContactExportSettings settings = globalContactExportSettingsDAO.getGlobalExportSettingsByOrganization(cmd.getOrganizationId());

		performUpdate(cmd, settings, context);

		return new VoidResult();
	}

	/**
	 * Update the settings in a transaction.
	 * 
	 * @param cmd Command to execute.
	 * @param settings List of global export settings.
	 * @param context Execution context.
	 * @throws CommandException If the export format is invalid.
	 */
	@Transactional
	protected void performUpdate(final UpdateGlobalContactExportSettingsCommand cmd, final GlobalContactExportSettings settings, final UserExecutionContext context) throws CommandException {
		if (cmd.getUpdateDefaultExportFormat()) {
			
			// Only updates default global export format.
			
			if (cmd.getDefaultOrganizationExportFormat() == null) {
				throw new CommandException("Invalid export format.");
			}

			settings.setDefaultOrganizationExportFormat(cmd.getDefaultOrganizationExportFormat());
			globalContactExportSettingsDAO.persist(settings, context.getUser());

		} else {

			// Updates all properties.

			settings.setAutoDeleteFrequency(cmd.getAutoDeleteFrequency());
			settings.setAutoExportFrequency(cmd.getAutoExportFrequency());
			settings.setExportFormat(cmd.getExportFormat());
			globalContactExportSettingsDAO.persist(settings, context.getUser());

			final Map<Integer, Boolean> fieldsMap = cmd.getFieldsMap();

			for (final Integer elementid : fieldsMap.keySet()) {
				final FlexibleElement element = em().find(FlexibleElement.class, elementid);
				element.setGloballyExportable(fieldsMap.get(elementid));
				em().merge(element);
			}
		}
	}
}
