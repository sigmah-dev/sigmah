package org.sigmah.server.endpoint.gwtrpc.handler;

import javax.persistence.EntityManager;

import org.sigmah.shared.command.DeleteCategories;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.category.CategoryElement;
import org.sigmah.shared.domain.category.CategoryType;
import org.sigmah.shared.dto.category.CategoryElementDTO;
import org.sigmah.shared.dto.category.CategoryTypeDTO;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class DeleteCategoriesHandler implements CommandHandler<DeleteCategories>{
	
	//private static final Log log = LogFactory.getLog(DeleteCategoriesHandler.class);

	private EntityManager em;

    @Inject
    public DeleteCategoriesHandler(EntityManager em) {
        this.em = em;
    }

	@Override
	public CommandResult execute(DeleteCategories cmd, User executingUser)
			throws CommandException {
		
		if(cmd.getCategoryTypes() != null){
			for(CategoryTypeDTO categoryDTO : cmd.getCategoryTypes()){
				CategoryType category = em.find(CategoryType.class, categoryDTO.getId());
				if(category != null)
					em.remove(category);
			}			
		}
		
		if(cmd.getCategoryElements() != null){
			for(CategoryElementDTO categoryDTO : cmd.getCategoryElements()){
				CategoryElement categoryElement = em.find(CategoryElement.class, categoryDTO.getId());
				if(categoryElement != null)
					em.remove(categoryElement);
			}			
		}
		
		return null;
	}

}
