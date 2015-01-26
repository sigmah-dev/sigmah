package org.sigmah.server.handler;

import java.util.Map;

import org.sigmah.server.dao.GlobalExportSettingsDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.export.GlobalExportSettings;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.UpdateGlobalExportSettingsCommand;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

/**
 * Updates {@link GlobalExportSettings} from {@link UpdateGlobalExportSettingsCommand}.
 * 
 * @author sherzod
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class UpdateGlobalExportSettingsHandler extends AbstractCommandHandler<UpdateGlobalExportSettingsCommand, VoidResult> {

	private final GlobalExportSettingsDAO globalExportSettingsDAO;

	@Inject
	public UpdateGlobalExportSettingsHandler(final GlobalExportSettingsDAO globalExportSettingsDAO) {
		this.globalExportSettingsDAO = globalExportSettingsDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VoidResult execute(final UpdateGlobalExportSettingsCommand cmd, final UserExecutionContext context) throws CommandException {

		final GlobalExportSettings settings = globalExportSettingsDAO.getGlobalExportSettingsByOrganization(cmd.getOrganizationId());

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
	protected void performUpdate(final UpdateGlobalExportSettingsCommand cmd, final GlobalExportSettings settings, final UserExecutionContext context) throws CommandException {
		if (cmd.getUpdateDefaultExportFormat()) {
			
			// Only updates default global export format.
			
			if (cmd.getDefaultOrganizationExportFormat() == null) {
				throw new CommandException("Invalid export format.");
			}

			settings.setDefaultOrganizationExportFormat(cmd.getDefaultOrganizationExportFormat());
			globalExportSettingsDAO.persist(settings, context.getUser());

		} else {

			// Updates all properties.

			settings.setAutoDeleteFrequency(cmd.getAutoDeleteFrequency());
			settings.setAutoExportFrequency(cmd.getAutoExportFrequency());
			settings.setExportFormat(cmd.getExportFormat());
			globalExportSettingsDAO.persist(settings, context.getUser());

			final Map<Integer, Boolean> fieldsMap = cmd.getFieldsMap();

			for (final Integer elementid : fieldsMap.keySet()) {
				final FlexibleElement element = em().find(FlexibleElement.class, elementid);
				element.setGloballyExportable(fieldsMap.get(elementid));
				em().merge(element);
			}
		}
	}
}
