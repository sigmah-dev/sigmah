package org.sigmah.server.endpoint.gwtrpc.handler;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.shared.command.DeactivateUsers;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.dao.UserDAO;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class DeactivateUsersHandler implements CommandHandler<DeactivateUsers>{
	
	private static final Log log = LogFactory.getLog(DeactivateUsersHandler.class);
	
	private final UserDAO userDAO;
	private EntityManager em;

    @Inject
    public DeactivateUsersHandler(EntityManager em, UserDAO userDAO) {
        this.em = em;
        this.userDAO = userDAO;
    }

	@Override
	public CommandResult execute(DeactivateUsers cmd, User executiongUser)
			throws CommandException {
		
		if(cmd.getUsers() != null){
			for(UserDTO userDTO : cmd.getUsers()){
				log.debug("DeactivateUsersHandler user " + userDTO.getId() + " mail" + userDTO.getEmail());
				User user = em.find(User.class, userDTO.getId());
				if(user == null)
					user = userDAO.findUserByEmail(userDTO.getEmail());
				user.setActive(!userDTO.getActive());
				user = em.merge(user);
			}			
		}
		
		return null;
	}

}
