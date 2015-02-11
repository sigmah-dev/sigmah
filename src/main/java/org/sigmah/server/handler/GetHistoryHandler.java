package org.sigmah.server.handler;

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
		final Date maxDate = cmd.getMaxDate();

		// Builds query.
		final StringBuilder sb = new StringBuilder();
		sb.append("SELECT h FROM HistoryToken h WHERE h.elementId = :elementId AND h.projectId = :projectId");
		if (maxDate != null) {
			sb.append(" AND h.date >= :maxDate");
		}
		sb.append(" ORDER BY h.date DESC");

		final TypedQuery<HistoryToken> query = em().createQuery(sb.toString(), HistoryToken.class);
		query.setParameter("elementId", elementId);
		query.setParameter("projectId", projectId);
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
