package org.sigmah.server.endpoint.gwtrpc.handler;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.shared.command.DeleteImportationSchemeModels;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.importation.ImportationSchemeModel;
import org.sigmah.shared.domain.importation.VariableFlexibleElement;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class DeleteImportationSchemeModelsHandler extends BaseEntityHandler implements
                CommandHandler<DeleteImportationSchemeModels> {

	private final static Log log = LogFactory.getLog(DeleteImportationSchemesHandler.class);

	@Inject
	public DeleteImportationSchemeModelsHandler(EntityManager em) {
		super(em);
	}

	@Override
	public CommandResult execute(DeleteImportationSchemeModels cmd, User user) throws CommandException {
		if (cmd.getImportationSchemeIdsList() != null) {
			ImportationSchemeModel schemeModelToDelete;
			for (Long schemeModelToDeleteId : cmd.getImportationSchemeIdsList()) {
				if (schemeModelToDeleteId > 0) {
					schemeModelToDelete = em.find(ImportationSchemeModel.class, schemeModelToDeleteId);
					schemeModelToDelete.delete();

					for (VariableFlexibleElement varfle : schemeModelToDelete.getVariableFlexibleElements()) {
						varfle.delete();
					}
					em.merge(schemeModelToDelete);
					if(log.isDebugEnabled()) {
						log.debug("Importation scheme (id = "+ schemeModelToDeleteId + " has been deleted" );
					}
				}

			}
		}

		if (cmd.getVariableFlexibleElemementIdsList() != null) {
			VariableFlexibleElement variablefleElement;
			for (Long varfleIdToDelete : cmd.getVariableFlexibleElemementIdsList()) {
				if (varfleIdToDelete > 0) {
					variablefleElement = em.find(VariableFlexibleElement.class, varfleIdToDelete);
					variablefleElement.delete();
					em.merge(variablefleElement);
				}
			}

		}
		return null;
	}
}
