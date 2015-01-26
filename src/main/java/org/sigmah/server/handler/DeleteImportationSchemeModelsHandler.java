package org.sigmah.server.handler;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.importation.ImportationSchemeModel;
import org.sigmah.server.domain.importation.VariableFlexibleElement;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.DeleteImportationSchemeModels;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for {@link DeleteImportationSchemeModels} command
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class DeleteImportationSchemeModelsHandler extends AbstractCommandHandler<DeleteImportationSchemeModels, VoidResult> {

	/**
	 * Logger.
	 */
	private final static Logger LOG = LoggerFactory.getLogger(DeleteImportationSchemeModelsHandler.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VoidResult execute(final DeleteImportationSchemeModels cmd, final UserExecutionContext context) throws CommandException {

		if (cmd.getImportationSchemeIdsList() != null) {
			ImportationSchemeModel schemeModelToDelete;
			for (final Integer schemeModelToDeleteId : cmd.getImportationSchemeIdsList()) {
				if (schemeModelToDeleteId != null && schemeModelToDeleteId > 0) {
					schemeModelToDelete = em().find(ImportationSchemeModel.class, schemeModelToDeleteId);
					schemeModelToDelete.delete();

					for (VariableFlexibleElement varfle : schemeModelToDelete.getVariableFlexibleElements()) {
						varfle.delete();
					}
					em().merge(schemeModelToDelete);
					if (LOG.isDebugEnabled()) {
						LOG.debug("Importation scheme (id = " + schemeModelToDeleteId + " has been deleted");
					}
				}

			}
		}

		if (cmd.getVariableFlexibleElemementIdsList() != null) {
			VariableFlexibleElement variablefleElement;
			for (final Integer varfleIdToDelete : cmd.getVariableFlexibleElemementIdsList()) {
				if (varfleIdToDelete != null && varfleIdToDelete > 0) {
					variablefleElement = em().find(VariableFlexibleElement.class, varfleIdToDelete);
					variablefleElement.delete();
					em().merge(variablefleElement);
				}
			}

		}
		return null;
	}
}
