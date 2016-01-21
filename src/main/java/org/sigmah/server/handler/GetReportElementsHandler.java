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
import org.sigmah.server.domain.element.ReportElement;
import org.sigmah.server.domain.element.ReportListElement;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetReportElements;
import org.sigmah.shared.command.result.ReportElementsResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.element.ReportElementDTO;
import org.sigmah.shared.dto.element.ReportListElementDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for {@link GetReportElements} command
 * 
 * @author HUZHE (v1.3)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 */
public class GetReportElementsHandler extends AbstractCommandHandler<GetReportElements, ReportElementsResult> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(GetReportModelsHandler.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReportElementsResult execute(final GetReportElements cmd, final UserExecutionContext context) throws CommandException {

		// Query
		final TypedQuery<ReportElement> reportElementsQuery = em().createQuery("From ReportElement", ReportElement.class);
		final TypedQuery<ReportListElement> reportListElementsQuery = em().createQuery("From ReportListElement", ReportListElement.class);

		// Get results
		final List<ReportElement> reportElements = reportElementsQuery.getResultList();
		final List<ReportListElement> reportListElements = reportListElementsQuery.getResultList();

		final List<ReportElementDTO> reportElementsDTOs = new ArrayList<ReportElementDTO>();
		final List<ReportListElementDTO> reportListElementsDTOs = new ArrayList<ReportListElementDTO>();

		// Mapping
		for (final ReportElement r : reportElements) {
			final ReportElementDTO reportElementDTO = mapper().map(r, ReportElementDTO.class);
			reportElementDTO.setModelId(r.getModel().getId());
			reportElementsDTOs.add(reportElementDTO);

			LOG.debug("Id after mapping is ID: {}", reportElementDTO.getModelId());
		}

		for (final ReportListElement r : reportListElements) {
			final ReportListElementDTO reportListElementDTO = mapper().map(r, ReportListElementDTO.class);
			reportListElementDTO.setModelId(r.getModel().getId());
			reportListElementsDTOs.add(reportListElementDTO);

			LOG.debug("Id after mapping is ID: {}", reportListElementDTO.getModelId());
		}

		// Return the results
		return new ReportElementsResult(reportElementsDTOs, reportListElementsDTOs);
	}

}
