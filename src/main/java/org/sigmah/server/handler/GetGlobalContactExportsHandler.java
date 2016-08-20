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

import com.google.inject.Inject;
import org.sigmah.server.dao.GlobalExportDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.export.GlobalContactExport;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.shared.command.GetGlobalContactExports;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.GlobalContactExportDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetGlobalContactExportsHandler extends AbstractCommandHandler<GetGlobalContactExports, ListResult<GlobalContactExportDTO>> {

	/**
	 * Logger.
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(GetGlobalContactExportsHandler.class);

	private final GlobalExportDAO dao;

	@Inject
	public GetGlobalContactExportsHandler(GlobalExportDAO dao, Mapper mapper) {
		this.dao = dao;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<GlobalContactExportDTO> execute(final GetGlobalContactExports cmd, final UserExecutionContext context) throws CommandException {
		List<GlobalContactExportDTO> globalExportDTOs = new ArrayList<GlobalContactExportDTO>();

		final List<GlobalContactExport> globalExports = dao.getGlobalContactExports(cmd.getFromData(), cmd.getToDate());

		final DateFormat dateFormat = new SimpleDateFormat(cmd.getDateFormat());

		if (globalExports != null) {
			for (final GlobalContactExport export : globalExports) {
				GlobalContactExportDTO exportDTO = new GlobalContactExportDTO();
				exportDTO.setId(export.getId().intValue());
				exportDTO.setDate(dateFormat.format(export.getDate()));
				globalExportDTOs.add(exportDTO);
			}
		}

		return new ListResult<GlobalContactExportDTO>(globalExportDTOs);
	}
}
