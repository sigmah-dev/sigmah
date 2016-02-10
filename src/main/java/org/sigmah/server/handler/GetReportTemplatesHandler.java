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
import org.sigmah.server.domain.ReportDefinition;
import org.sigmah.server.domain.ReportSubscription;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetReportTemplates;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ReportDefinitionDTO;

/**
 * Handler for {@link GetReportTemplates} command
 * 
 * @author Alex Bertram
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class GetReportTemplatesHandler extends AbstractCommandHandler<GetReportTemplates, ListResult<ReportDefinitionDTO>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<ReportDefinitionDTO> execute(final GetReportTemplates cmd, final UserExecutionContext context) throws CommandException {

		final TypedQuery<ReportDefinition> query = em().createQuery("select r from ReportDefinition r", ReportDefinition.class);

		final List<ReportDefinition> results = query.getResultList();

		final List<ReportDefinitionDTO> dtos = new ArrayList<ReportDefinitionDTO>();

		for (final ReportDefinition template : results) {

			final ReportDefinitionDTO dto = new ReportDefinitionDTO();
			dto.setId(template.getId());
			dto.setDatabaseName(template.getDatabase() == null ? null : template.getDatabase().getName());
			dto.setOwnerName(template.getOwner().getName());
			dto.setAmOwner(template.getOwner().getId().equals(context.getUser().getId()));
			dto.setTitle(template.getTitle());
			dto.setFrequency(template.getFrequency());
			dto.setDay(template.getDay());
			dto.setDescription(template.getDescription());
			dto.setEditAllowed(dto.getAmOwner());

			dto.setSubscribed(false);
			for (final ReportSubscription sub : template.getSubscriptions()) { // TODO this is ridiculous.
				if (sub.getUser().getId().equals(context.getUser().getId())) {
					dto.setSubscribed(sub.isSubscribed());
					break;
				}
			}
			dtos.add(dto);
		}

		return new ListResult<ReportDefinitionDTO>(dtos);
	}
}
