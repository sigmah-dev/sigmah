package org.sigmah.server.policy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.category.CategoryType;
import org.sigmah.shared.dto.category.CategoryElementDTO;
import org.sigmah.shared.dto.category.CategoryTypeDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.inject.Inject;
/**
 * Create privacy group policy.
 * 
 * @author nrebiai
 * 
 */
public class CategoryPolicy implements EntityPolicy<CategoryType> {
	
	private final Mapper mapper;
	private final EntityManager em;
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(CategoryPolicy.class);
	
	
	@Inject
    public CategoryPolicy(EntityManager em, Mapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }

	@SuppressWarnings("unchecked")
	@Override
	public Object create(User executingUser, PropertyMap properties) {

		CategoryType categoryToPersist  = null;
				
		CategoryTypeDTO categoyTypeDTO = (CategoryTypeDTO) properties.get(AdminUtil.PROP_CATEGORY_TYPE);
		CategoryElementDTO categoyElementDTO = (CategoryElementDTO) properties.get(AdminUtil.PROP_CATEGORY_ELEMENT);
		
		//save categoryType
		if(categoyTypeDTO != null){
			//FIXME verif id
		}
		
		//save categoryElement
		
		CategoryTypeDTO categoryPersisted = null;
		if(categoryToPersist != null){
			categoryPersisted = mapper.map(categoryToPersist, CategoryTypeDTO.class);
		}
		
		return categoryPersisted;
	}

	@Override
	public void update(User user, Object entityId, PropertyMap changes) {
		// TODO Auto-generated method stub
		
	}

	public BaseModelData createDraft(Map<String, Object> properties) {
		// TODO Auto-generated method stub
		return null;
	}

}
