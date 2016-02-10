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
