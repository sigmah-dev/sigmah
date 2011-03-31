package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.shared.command.DeleteFlexibleElements;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.dao.UserDAO;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.element.FlexibleElement;
import org.sigmah.shared.domain.layout.LayoutConstraint;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class DeleteFlexibleElementsHandler implements CommandHandler<DeleteFlexibleElements>{
	
	private static final Log log = LogFactory.getLog(DeleteFlexibleElementsHandler.class);
	
	private EntityManager em;

    @Inject
    public DeleteFlexibleElementsHandler(EntityManager em, UserDAO userDAO) {
        this.em = em;
    }

	@Override
	public CommandResult execute(DeleteFlexibleElements cmd, User executiongUser)
			throws CommandException {
		
		if(cmd.getFlexibleElements() != null){
			for(FlexibleElementDTO flexEltDTO : cmd.getFlexibleElements()){
				
				FlexibleElement flexElt = em.find(FlexibleElement.class, new Integer(flexEltDTO.getId()).longValue());
				
				Query query = em.createQuery("Select l from LayoutConstraint l Where l.element = :flexibleElement");
				query.setParameter("flexibleElement", flexElt);
				for(LayoutConstraint layout : (List<LayoutConstraint>)query.getResultList()){
					em.remove(layout);
				}
				log.debug("DeactivateUsersHandler flexElt " + flexEltDTO.getId() + " name" + flexEltDTO.getLabel());
				
				em.remove(flexElt);
			}			
		}
		
		return null;
	}

}
