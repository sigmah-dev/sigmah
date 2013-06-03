package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.shared.command.DeletePrivacyGroups;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.dao.UserDAO;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.profile.PrivacyGroup;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class DeletePrivacyGroupsHandler implements CommandHandler<DeletePrivacyGroups>{
private static final Log log = LogFactory.getLog(DeleteFlexibleElementsHandler.class);
	
	private EntityManager em;

    @Inject
    public DeletePrivacyGroupsHandler(EntityManager em, UserDAO userDAO) {
        this.em = em;
    }

	@Override
	public CommandResult execute(DeletePrivacyGroups cmd, User user)
			throws CommandException {
		List<PrivacyGroupDTO> privacyGroupsDTOList = cmd.getPrivacyGroupsList();
		
		if(privacyGroupsDTOList!=null)
		{//Delete the privacy groups
			
			for(PrivacyGroupDTO model:privacyGroupsDTOList)
			{
			   PrivacyGroup privacyGroup = em.find(PrivacyGroup.class, model.getId());
			   if(privacyGroup != null)
				   log.debug("Deleting the privacy group ID: " + privacyGroup.getId()+ " Name: " + privacyGroup.getTitle());
			        em.remove(privacyGroup);
			        
			}
			
			//Commit the changes
			em.flush();
		}
		return null;
	}

}
