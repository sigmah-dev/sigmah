package org.sigmah.server.handler;

import com.google.inject.persist.Transactional;
import java.util.List;
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
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * @author Maxime Lombard (mlombard@ideia.fr) v2.0
 * @author Raphaël Calabro (rcalabro@ideia.fr)
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
	public VoidResult execute(final DeleteImportationSchemeModels command, final UserExecutionContext context) throws CommandException {

		if (command.getImportationSchemeIds() != null) {
			removeImportationSchemeModels(command.getImportationSchemeIds());
		}

		if (command.getVariableFlexibleElemementIds() != null) {
			removeVariableFlexibleElements(command.getVariableFlexibleElemementIds());
		}
		return null;
	}
	
	@Transactional
	protected void removeImportationSchemeModels(List<Integer> modelIds) {
		for (final Integer schemeModelId : modelIds) {
			if (schemeModelId != null && schemeModelId > 0) {
				final ImportationSchemeModel schemeModel = em().find(ImportationSchemeModel.class, schemeModelId);
				schemeModel.delete();

				for (VariableFlexibleElement varfle : schemeModel.getVariableFlexibleElements()) {
					varfle.delete();
				}
				em().merge(schemeModel);
			}
		}
	}
	
	@Transactional
	protected void removeVariableFlexibleElements(List<Integer> elements) {
		for (final Integer elementId : elements) {
			if (elementId != null && elementId > 0) {
				final VariableFlexibleElement element = em().find(VariableFlexibleElement.class, elementId);
				element.delete();
				em().merge(element);
			}
		}
	}
}
