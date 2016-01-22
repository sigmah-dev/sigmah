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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.sigmah.server.dao.GlobalExportDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.export.GlobalExport;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.shared.command.GetGlobalExports;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.GlobalExportDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Provides list of link{GlobalExportDTO} for given date range
 * 
 * @author sherzod
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class GetGlobalExportsHandler extends AbstractCommandHandler<GetGlobalExports, ListResult<GlobalExportDTO>> {

	/**
	 * Logger.
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(GetGlobalExportsHandler.class);

	private final GlobalExportDAO dao;

	@Inject
	public GetGlobalExportsHandler(GlobalExportDAO dao, Mapper mapper) {
		this.dao = dao;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<GlobalExportDTO> execute(final GetGlobalExports cmd, final UserExecutionContext context) throws CommandException {
		List<GlobalExportDTO> globalExportDTOs = new ArrayList<GlobalExportDTO>();

		final List<GlobalExport> globalExports = dao.getGlobalExports(cmd.getFromData(), cmd.getToDate());

		final DateFormat dateFormat = new SimpleDateFormat(cmd.getDateFormat());

		if (globalExports != null) {
			for (final GlobalExport export : globalExports) {
				GlobalExportDTO exportDTO = new GlobalExportDTO();
				exportDTO.setId(export.getId().intValue());
				exportDTO.setDate(dateFormat.format(export.getDate()));
				globalExportDTOs.add(exportDTO);
			}
		}

		return new ListResult<GlobalExportDTO>(globalExportDTOs);
	}
}
