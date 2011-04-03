package org.sigmah.server.policy;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.category.CategoryElement;
import org.sigmah.shared.domain.category.CategoryIcon;
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

	@Override
	public Object create(User executingUser, PropertyMap properties) {

		CategoryType categoryToPersist  = null;
		CategoryElement categoryElementToPersist = null;
				
		String name = (String) properties.get(AdminUtil.PROP_CATEGORY_TYPE_NAME);
		CategoryIcon icon = (CategoryIcon) properties.get(AdminUtil.PROP_CATEGORY_TYPE_ICON);
		
		String label = (String) properties.get(AdminUtil.PROP_CATEGORY_ELEMENT_NAME);
		String color = (String) properties.get(AdminUtil.PROP_CATEGORY_ELEMENT_COLOR);		
		CategoryTypeDTO category = (CategoryTypeDTO) properties.get(AdminUtil.PROP_CATEGORY_TYPE);
		
		
		
		//save categoryType
		if(name != null && icon != null){
			final Query query = em.createQuery("SELECT c FROM CategoryType c WHERE c.label = :name ORDER BY c.id");
			query.setParameter("name", name);
			try{
				if(query.getSingleResult() != null){
					categoryToPersist = (CategoryType) query.getSingleResult();
					categoryToPersist.setLabel(name);
					categoryToPersist.setIcon(icon);
					categoryToPersist = em.merge(categoryToPersist);
				}else{
					categoryToPersist = new CategoryType();
					categoryToPersist.setLabel(name);
					categoryToPersist.setIcon(icon);
					em.persist(categoryToPersist);
				}
			}catch(Exception e){
				categoryToPersist = new CategoryType();
				categoryToPersist.setLabel(name);
				categoryToPersist.setIcon(icon);
				em.persist(categoryToPersist);
			}
		}
		
		//save categoryElement
		if(label != null && color != null && category != null){			
			CategoryType parentType = em.find(CategoryType.class, category.getId());
			if(parentType != null){
				final Query query = em.createQuery("SELECT c FROM CategoryElement c " +
						"WHERE c.label = :name AND c.parentType = :category ORDER BY c.id");
				query.setParameter("name", name);
				query.setParameter("category", parentType);
				try{
					if(query.getSingleResult() != null){
						categoryElementToPersist = (CategoryElement) query.getSingleResult();
						categoryElementToPersist.setLabel(label);
						categoryElementToPersist.setColor(color);
						categoryElementToPersist.setParentType(parentType);
						categoryElementToPersist = em.merge(categoryElementToPersist);
					}else{
						categoryElementToPersist = new CategoryElement();
						categoryElementToPersist.setLabel(label);
						categoryElementToPersist.setColor(color);
						categoryElementToPersist.setParentType(parentType);
						em.persist(categoryElementToPersist);
					}
				}catch(Exception e){
					categoryElementToPersist = new CategoryElement();
					categoryElementToPersist.setLabel(label);
					categoryElementToPersist.setColor(color);
					categoryElementToPersist.setParentType(parentType);
					em.persist(categoryElementToPersist);
				}
			}
		}
		
		if(properties.get(AdminUtil.PROP_CATEGORY_TYPE) == null){
			CategoryTypeDTO categoryPersisted = null;
			if(categoryToPersist != null){
				categoryPersisted = mapper.map(categoryToPersist, CategoryTypeDTO.class);
			}
			return categoryPersisted;
		}else{
			CategoryElementDTO categoryElementPersisted = null;
			if(categoryElementToPersist != null){
				categoryElementPersisted = mapper.map(categoryElementToPersist, CategoryElementDTO.class);
			}
			return categoryElementPersisted;
		}	
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
