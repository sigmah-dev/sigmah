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
