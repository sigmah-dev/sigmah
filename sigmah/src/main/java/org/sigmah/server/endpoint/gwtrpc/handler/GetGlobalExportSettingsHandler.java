/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.server.dao.GlobalExportDAO;
import org.sigmah.shared.command.GetGlobalExportSettings;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.domain.Organization;
import org.sigmah.shared.domain.ProjectModel;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.export.GlobalExportSettings;
import org.sigmah.shared.dto.GlobalExportSettingsDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

/*
 * Provides link{GlobalExportSettingsDTO} for given organization
 * @author sherzod
 */
public class GetGlobalExportSettingsHandler implements CommandHandler<GetGlobalExportSettings> {

	private static final Log log = LogFactory.getLog(GetGlobalExportSettingsHandler.class);
	
	private final GlobalExportDAO dao;
 	private final EntityManager em;
 	private final Mapper mapper;
	@Inject
    public GetGlobalExportSettingsHandler(
    		GlobalExportDAO dao, 
    		EntityManager em,
    		Mapper mapper) {
        this.dao = dao;
        this.em=em;
        this.mapper=mapper;        
     }
			
	@SuppressWarnings("unchecked")
	@Override
	public CommandResult execute(GetGlobalExportSettings cmd, User user)
			throws CommandException {		 		
 		
		final GlobalExportSettings settings=
			dao.getGlobalExportSettingsByOrganization(cmd.getOrganizationId());		
		
		final GlobalExportSettingsDTO result=
			mapper.map(settings, GlobalExportSettingsDTO.class);
		
		final List<ProjectModel> pModels=
			dao.getProjectModelsByOrganization(em.find(Organization.class, cmd.getOrganizationId()));
		final List<ProjectModelDTO> pModelDTOs=
			new ArrayList<ProjectModelDTO>();
		for(ProjectModel model:pModels){
			if(model.getStatus() != ProjectModelStatus.DRAFT) {
				pModelDTOs.add(mapper.map(model, ProjectModelDTO.class));
			}
		}
		result.setProjectModelsDTO(pModelDTOs);
		
		return result;
	}

}
