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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.HistoryToken;
import org.sigmah.server.domain.User;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetHistory;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.history.HistoryTokenDTO;
import org.sigmah.shared.dto.history.HistoryTokenListDTO;

/**
 * Handler for {@link GetHistory} command
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class GetHistoryHandler extends AbstractCommandHandler<GetHistory, ListResult<HistoryTokenListDTO>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<HistoryTokenListDTO> execute(final GetHistory cmd, final UserExecutionContext context) throws CommandException {

		// Gets query parameters.
		final int elementId = cmd.getElementId();
		final int projectId = cmd.getProjectId();
		final Integer iterationId = cmd.getIterationId();
		final Date maxDate = cmd.getMaxDate();

		// Builds query.
		final StringBuilder sb = new StringBuilder();
		sb.append("SELECT h FROM HistoryToken h WHERE h.elementId = :elementId AND h.projectId = :projectId");
		if (iterationId != null) {
			sb.append(" AND h.layoutGroupIterationId = :iterationId");
		}
		if (maxDate != null) {
			sb.append(" AND h.date >= :maxDate");
		}
		sb.append(" ORDER BY h.date DESC");

		final TypedQuery<HistoryToken> query = em().createQuery(sb.toString(), HistoryToken.class);
		query.setParameter("elementId", elementId);
		query.setParameter("projectId", projectId);
		if (iterationId != null) {
			query.setParameter("iterationId", iterationId);
		}
		if (maxDate != null) {
			query.setParameter("maxDate", maxDate);
		}

		// Retrieves query results and map results.
		final List<HistoryToken> tokens = query.getResultList();

		final HashMap<Date, HistoryTokenListDTO> mappedTokensDTO = new HashMap<Date, HistoryTokenListDTO>();
		final ArrayList<HistoryTokenListDTO> tokensDTO = new ArrayList<HistoryTokenListDTO>();

		if (tokens != null) {
			for (final HistoryToken token : tokens) {

				HistoryTokenListDTO list = mappedTokensDTO.get(token.getDate());

				if (list == null) {
					list = new HistoryTokenListDTO();
					list.setDate(token.getDate());

					final User owner = token.getUser();
					if (owner != null) {
						list.setUserEmail(owner.getEmail());
						list.setUserFirstName(owner.getFirstName());
						list.setUserName(owner.getName());
					}

					mappedTokensDTO.put(token.getDate(), list);
					tokensDTO.add(list);
				}

				if (list.getTokens() == null) {
					list.setTokens(new ArrayList<HistoryTokenDTO>());
				}

				final String coreVersionName = token.getCoreVersion() != null ? token.getCoreVersion().getVersion() + ". " + token.getCoreVersion().getName() : null;
				
				list.getTokens().add(new HistoryTokenDTO(token.getValue(), token.getType(), token.getComment(), coreVersionName));
			}
		}

		return new ListResult<HistoryTokenListDTO>(tokensDTO);
	}
}
