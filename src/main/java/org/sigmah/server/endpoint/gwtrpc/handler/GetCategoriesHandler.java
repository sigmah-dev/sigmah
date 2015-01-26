package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.shared.command.GetCategories;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CategoriesListResult;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.category.CategoryType;
import org.sigmah.shared.dto.category.CategoryTypeDTO;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

/**
 * 
 * @author nrebiai
 * 
 */
public class GetCategoriesHandler implements CommandHandler<GetCategories> {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(GetCategoriesHandler.class);
	
	private final EntityManager em;
	private final Mapper mapper;
	
	@Inject
    public GetCategoriesHandler(EntityManager em, Mapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }
			
	@SuppressWarnings("unchecked")
	@Override
	public CommandResult execute(GetCategories cmd, User user)
			throws CommandException {
		List<CategoryTypeDTO> categories = new ArrayList<CategoryTypeDTO>();
		
		final Query query = em.createQuery("SELECT c FROM CategoryType c WHERE c.organization.id = :orgid ORDER BY c.id");
		query.setParameter("orgid", user.getOrganization().getId());
		
		final List<CategoryType> resultCategories = (List<CategoryType>) query.getResultList();
		
		if(resultCategories != null){
			for(final CategoryType oneCategory : resultCategories){
				CategoryTypeDTO categoryDTO = mapper.map(oneCategory, CategoryTypeDTO.class);				
				categories.add(categoryDTO);
			}
		}
		
		return new CategoriesListResult(categories);
	}

}
