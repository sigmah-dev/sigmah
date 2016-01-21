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
import javax.persistence.TypedQuery;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.importation.ImportationScheme;
import org.sigmah.server.domain.importation.ImportationSchemeModel;
import org.sigmah.server.domain.importation.Variable;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.DeleteImportationSchemes;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dispatch.FunctionalException;

/**
 * Handler for {@link DeleteImportationSchemes} command
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * @author Maxime Lombard (mlombard@ideia.fr) v2.0
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class DeleteImportationSchemesHandler extends AbstractCommandHandler<DeleteImportationSchemes, VoidResult> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VoidResult execute(final DeleteImportationSchemes cmd, final UserExecutionContext context) throws CommandException {

		if (cmd.getSchemaId() != null) {
			deleteImportationScheme(cmd.getSchemaId());
		}

		if (cmd.getVariableIdsList() != null) {
			deleteVariables(cmd.getVariableIdsList());
		}

		return null;
	}
	
	@Transactional
	protected void deleteImportationScheme(int schemaId) throws FunctionalException {
		final ImportationScheme importationScheme = em().find(ImportationScheme.class, schemaId);
		
		if(importationScheme == null) {
			throw new IllegalArgumentException("Importation scheme '" + schemaId + "' was not found.");
		}
		
		final TypedQuery<ImportationSchemeModel> query = em().createQuery("FROM ImportationSchemeModel model WHERE model.importationScheme = :importationScheme", ImportationSchemeModel.class);
		query.setParameter("importationScheme", importationScheme);
		final List<ImportationSchemeModel> links = query.getResultList();
		if(!links.isEmpty()) {
			// Listing the names of the linked models.
			final StringBuilder modelNames = new StringBuilder();
			for(final ImportationSchemeModel link : links) {
				if(modelNames.length() > 0) {
					modelNames.append(", ");
				}
				if(link.getProjectModel() != null) {
					modelNames.append(link.getProjectModel().getName()).append(" (P)");
					
				} else if(link.getOrgUnitModel() != null) {
					modelNames.append(link.getOrgUnitModel().getName()).append(" (O)");
				}
			}
			
			throw new FunctionalException(FunctionalException.ErrorCode.IMPORTATION_SCHEME_IS_LINKED, modelNames.toString());
		}
		
		importationScheme.delete();

		for (final Variable var : importationScheme.getVariables()) {
			var.delete();
		}
		
		em().merge(importationScheme);
	}

	@Transactional
	protected void deleteVariables(List<Integer> variableIds) {
		for (final Integer varIdToDelete : variableIds) {
			if (varIdToDelete > 0) {
				final Variable variable = em().find(Variable.class, varIdToDelete);
				variable.delete();
				em().merge(variable);
			}
		}
	}
}
