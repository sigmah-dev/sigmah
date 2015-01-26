package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.shared.command.DeleteProfiles;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.dao.UserDAO;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.profile.Profile;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class DeleteProfilesHandler implements CommandHandler<DeleteProfiles>{
private static final Log log = LogFactory.getLog(DeleteFlexibleElementsHandler.class);
	
	private EntityManager em;

    @Inject
    public DeleteProfilesHandler(EntityManager em, UserDAO userDAO) {
        this.em = em;
    }

	@Override
	public CommandResult execute(DeleteProfiles cmd, User user)
			throws CommandException {
		List<ProfileDTO> profilesDTOList = cmd.getProfilesList();
		
		if(profilesDTOList!=null)
		{//Delete the profiles
			
			for(ProfileDTO model:profilesDTOList)
			{
			   Profile profile = em.find(Profile.class, model.getId());
			   if(profile != null)
				   log.debug("Deleting the profile ID: "+profile.getId()+" Name: "+profile.getName());
			        em.remove(profile);
			        
			}
			
			//Commit the changes
			em.flush();
		}
		return null;
	}

}
