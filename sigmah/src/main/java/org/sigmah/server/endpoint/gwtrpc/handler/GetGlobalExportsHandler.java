/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.server.endpoint.gwtrpc.handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.server.dao.GlobalExportDAO;
import org.sigmah.shared.command.GetGlobalExports;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.GlobalExportListResult;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.export.GlobalExport;
import org.sigmah.shared.dto.GlobalExportDTO;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

/*
 * Provides list of link{GlobalExportDTO} for given date range
 * @author sherzod
 */
public class GetGlobalExportsHandler implements CommandHandler<GetGlobalExports> {

	private static final Log log = LogFactory.getLog(GetGlobalExportsHandler.class);
	
	private final GlobalExportDAO dao;
 	
	@Inject
    public GetGlobalExportsHandler(GlobalExportDAO dao, Mapper mapper) {
        this.dao = dao;
     }
			
	@SuppressWarnings("unchecked")
	@Override
	public CommandResult execute(GetGlobalExports cmd, User user)
			throws CommandException {
		List<GlobalExportDTO> globalExportDTOs = new ArrayList<GlobalExportDTO>();
	 	
		final List<GlobalExport> globalExports=dao.getGlobalExports(cmd.getFromData(), cmd.getToDate());
	
		final DateFormat dateFormat=new SimpleDateFormat("M.d.yyyy, h a");
		
		if(globalExports != null){
			for(final GlobalExport export : globalExports){
				GlobalExportDTO exportDTO = new GlobalExportDTO();
				exportDTO.setId(export.getId().intValue());
				exportDTO.setDate(dateFormat.format(export.getDate()));
				globalExportDTOs.add(exportDTO);
			}
		}
		
		return new GlobalExportListResult(globalExportDTOs);
	}
}
