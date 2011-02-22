package org.sigmah.server.endpoint.gwtrpc.handler;

import javax.persistence.EntityManager;

import org.sigmah.shared.command.DeleteList;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.domain.Deleteable;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class DeleteListHandler implements CommandHandler<DeleteList>{
	
	private EntityManager em;

    @Inject
    public DeleteListHandler(EntityManager em) {
        this.em = em;
    }

	@Override
	public CommandResult execute(DeleteList cmd, User user)
			throws CommandException {
		if(cmd.getEntityClass() != null){
			for(Integer entityId : cmd.getEntities()){
				//Delete deleteCmd = new Delete(cmd.getEntityClass(), entityId);
				//new DeleteHandler(em).execute(deleteCmd, user);
				Deleteable entity = (Deleteable) em.find(cmd.getEntityClass(), entityId);
		        entity.delete();
			}		
		}
		return null;
	}

}
