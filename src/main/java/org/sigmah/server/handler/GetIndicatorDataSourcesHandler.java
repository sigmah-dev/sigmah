package org.sigmah.server.handler;

import com.google.inject.persist.Transactional;
import java.util.ArrayList;
import java.util.List;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Indicator;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetIndicatorDataSources;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.IndicatorDataSourceDTO;

/**
 * Handler for {@link GetIndicatorDataSources} command
 * 
 * @author Alexander Bertram (akbertram@gmail.com)
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class GetIndicatorDataSourcesHandler extends AbstractCommandHandler<GetIndicatorDataSources, ListResult<IndicatorDataSourceDTO>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<IndicatorDataSourceDTO> execute(final GetIndicatorDataSources cmd, final UserExecutionContext context) throws CommandException {
		// TODO: verify authorization

		List<IndicatorDataSourceDTO> list = new ArrayList<IndicatorDataSourceDTO>();

		findIndicatorDataSources(cmd, list);

		return new ListResult<IndicatorDataSourceDTO>(list);
	}

	@Transactional
	protected void findIndicatorDataSources(final GetIndicatorDataSources cmd, List<IndicatorDataSourceDTO> list) {
		final Indicator indicator = em().find(Indicator.class, cmd.getIndicatorId());
		if (indicator.getDataSources() != null) {
			list.addAll(mapper().mapCollection(indicator.getDataSources(), IndicatorDataSourceDTO.class));
		}
	}
}
