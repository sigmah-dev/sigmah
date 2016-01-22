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

import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.importation.ImportationScheme;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetImportationSchemes;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;

/**
 * {@link GetImportationSchemes} command execution.
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr) (v1.3)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 */
public class GetImportationSchemesHandler extends AbstractCommandHandler<GetImportationSchemes, ListResult<ImportationSchemeDTO>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<ImportationSchemeDTO> execute(final GetImportationSchemes cmd, final UserExecutionContext context) throws CommandException {

		final List<ImportationScheme> schemeToExclude = new ArrayList<ImportationScheme>();

		if (cmd.getExcludeExistent()) {

			final TypedQuery<ImportationScheme> query;

			if (cmd.getProjectModelId() != null) {
				query =
						em().createQuery("SELECT sm.importationScheme FROM ImportationSchemeModel sm WHERE sm.projectModel.id = :projectModelId", ImportationScheme.class);
				query.setParameter("projectModelId", cmd.getProjectModelId());

			} else {
				query = em().createQuery("SELECT sm.importationScheme FROM ImportationSchemeModel sm WHERE sm.orgUnitModel.id = :orgUnitId", ImportationScheme.class);
				query.setParameter("orgUnitId", cmd.getOrgUnitModelId());
			}

			schemeToExclude.addAll(query.getResultList());
		}

		final TypedQuery<ImportationScheme> query = em().createQuery("FROM ImportationScheme sm", ImportationScheme.class);
		final List<ImportationScheme> schemasList = query.getResultList();

		final List<ImportationSchemeDTO> schemaDTOList = new ArrayList<ImportationSchemeDTO>();
		if (!schemasList.isEmpty()) {
			for (final ImportationScheme importationScheme : schemasList) {
				if (!schemeToExclude.contains(importationScheme)) {
					schemaDTOList.add(mapper().map(importationScheme, new ImportationSchemeDTO()));
				}
			}
		}

		return new ListResult<ImportationSchemeDTO>(schemaDTOList);
	}

}
