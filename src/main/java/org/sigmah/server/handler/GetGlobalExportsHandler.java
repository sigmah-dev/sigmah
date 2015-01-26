package org.sigmah.server.handler;

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
