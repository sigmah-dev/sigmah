package org.sigmah.server.handler;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.importation.ImportationScheme;
import org.sigmah.server.domain.importation.Variable;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.DeleteImportationSchemes;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for {@link DeleteImportationSchemes} command
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class DeleteImportationSchemesHandler extends AbstractCommandHandler<DeleteImportationSchemes, VoidResult> {

	/**
	 * Logger.
	 */
	@SuppressWarnings("unused")
	private final static Logger LOG = LoggerFactory.getLogger(DeleteImportationSchemesHandler.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VoidResult execute(final DeleteImportationSchemes cmd, final UserExecutionContext context) throws CommandException {

		if (cmd.getSchemaIdsList() != null) {
			ImportationScheme schemaToDelete;
			for (final Integer schemaToDeleteId : cmd.getSchemaIdsList()) {
				if (schemaToDeleteId > 0) {
					schemaToDelete = em().find(ImportationScheme.class, schemaToDeleteId);
					schemaToDelete.delete();

					for (Variable var : schemaToDelete.getVariables()) {
						var.delete();
					}
					em().merge(schemaToDelete);
				}

			}
		}

		if (cmd.getVariableIdsList() != null) {
			Variable variable;
			for (final Integer varIdToDelete : cmd.getVariableIdsList()) {
				if (varIdToDelete > 0) {
					variable = em().find(Variable.class, varIdToDelete);
					variable.delete();
					em().merge(variable);
				}
			}

		}

		return null;
	}

}
