package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.server.dao.GlobalExportDAO;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.element.FlexibleElement;
import org.sigmah.shared.domain.export.GlobalExportSettings;
import org.sigmah.shared.dto.UpdateGlobalExportSettings;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class UpdateGlobalExportSettingsHandler implements CommandHandler<UpdateGlobalExportSettings> {

	private static final Log log = LogFactory.getLog(UpdateGlobalExportSettingsHandler.class);
	
	private final GlobalExportDAO dao;
 	private final EntityManager em;
 	private final Mapper mapper;
	@Inject
    public UpdateGlobalExportSettingsHandler(
    		GlobalExportDAO dao, 
    		EntityManager em,
    		Mapper mapper) {
        this.dao = dao;
        this.em=em;
        this.mapper=mapper;        
     }
			
	@SuppressWarnings("unchecked")
	@Override
	public CommandResult execute(UpdateGlobalExportSettings cmd, User user)
			throws CommandException {
		
		final GlobalExportSettings settings=
			dao.getGlobalExportSettingsByOrganization(cmd.getOrganizationId());	
		
		settings.setAutoDeleteFrequency(cmd.getAutoDeleteFrequency());
		settings.setAutoExportFrequency(cmd.getAutoExportFrequency());
		settings.setExportFormat(cmd.getExportFormat());
		em.merge(settings);
		
		final Map<Integer,Boolean> fieldsMap=cmd.getFieldsMap();
		for(Integer elementid:fieldsMap.keySet()){
			FlexibleElement element=em.find(FlexibleElement.class, new Long(elementid.longValue()));
			element.setGloballyExportable(fieldsMap.get(elementid));
			em.merge(element);
		}
		 
		
		return null;
	}
}
