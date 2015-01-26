package org.sigmah.server.endpoint.gwtrpc.handler;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.shared.command.DeleteImportationSchemes;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.importation.ImportationScheme;
import org.sigmah.shared.domain.importation.Variable;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class DeleteImportationSchemesHandler extends BaseEntityHandler implements
                CommandHandler<DeleteImportationSchemes> {

	private final static Log LOG = LogFactory.getLog(DeleteImportationSchemesHandler.class);

	private final Injector injector;

	@Inject
	public DeleteImportationSchemesHandler(EntityManager em, Injector injector) {
		super(em);
		this.injector = injector;
	}

	@Override
	public CommandResult execute(DeleteImportationSchemes cmd, User user) throws CommandException {

		if (cmd.getSchemaIdsList() != null) {
			ImportationScheme schemaToDelete;
			for (Long schemaToDeleteId : cmd.getSchemaIdsList()) {
				if (schemaToDeleteId > 0) {
					schemaToDelete = em.find(ImportationScheme.class, schemaToDeleteId);
					schemaToDelete.delete();

					for (Variable var : schemaToDelete.getVariables()) {
						var.delete();
					}
					em.merge(schemaToDelete);
				}

			}
		}

		if (cmd.getVariableIdsList() != null) {
			Variable variable;
			for (Long varIdToDelete : cmd.getVariableIdsList()) {
				if (varIdToDelete > 0) {
					variable = em.find(Variable.class, varIdToDelete);
					variable.delete();
					em.merge(variable);
				}
			}

		}

		return null;
	}

}
