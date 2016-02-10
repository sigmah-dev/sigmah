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


import java.util.List;

import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.importation.ImportationSchemeModel;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetImportationSchemeModels;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;

/**
 * {@link GetImportationSchemeModels} command exectution
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * @author Maxime Lombard (mlombard@ideia.fr) v2.0
 */
public class GetImportationSchemeModelsHandler extends AbstractCommandHandler<GetImportationSchemeModels, ListResult<ImportationSchemeModelDTO>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<ImportationSchemeModelDTO> execute(final GetImportationSchemeModels cmd, final UserExecutionContext context) throws CommandException {

		final TypedQuery<ImportationSchemeModel> query;

		if (cmd.getImportationSchemeId() == null && cmd.getProjectModelId() == null && cmd.getOrgUnitModelId() == null) {
			query = em().createQuery("FROM ImportationSchemeModel", ImportationSchemeModel.class);

		} else {

			final StringBuilder builder = new StringBuilder("SELECT sm FROM ImportationSchemeModel sm LEFT JOIN sm.projectModel pm LEFT JOIN sm.orgUnitModel oum WHERE ");

			if (cmd.getImportationSchemeId() != null) {
				builder.append(" sm.importationScheme.id = ").append(cmd.getImportationSchemeId());
			} else if (cmd.getProjectModelId() != null) {
				builder.append(" sm.projectModel.id = ").append(cmd.getProjectModelId());
			} else if (cmd.getOrgUnitModelId() != null) {
				builder.append(" sm.orgUnitModel.id = ").append(cmd.getOrgUnitModelId());
			}
            
            builder.append(" and sm.importationScheme.dateDeleted is null "
                    + "and (pm is null OR pm.dateDeleted is null) "
                    + "and (oum is null OR oum.dateDeleted is null)");
            
			query = em().createQuery(builder.toString(), ImportationSchemeModel.class);
		}

		final List<ImportationSchemeModel> importationSchemeModelsList = query.getResultList();

		return new ListResult<ImportationSchemeModelDTO>(mapper().mapCollection(importationSchemeModelsList, ImportationSchemeModelDTO.class));
	}

}
