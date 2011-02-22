package org.sigmah.server.policy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.profile.PrivacyGroup;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.inject.Inject;
/**
 * Create user policy.
 * 
 * @author nrebiai
 * 
 */
public class PrivacyGroupPolicy implements EntityPolicy<PrivacyGroup> {
	
	private final Mapper mapper;
	private final EntityManager em;
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(PrivacyGroupPolicy.class);
	
	
	@Inject
    public PrivacyGroupPolicy(EntityManager em, Mapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }

	@SuppressWarnings("unchecked")
	@Override
	public Object create(User executingUser, PropertyMap properties) {

		PrivacyGroup pgToPersist  = null;
				
		//get User that need to be saved from properties	
		Number code = (Number)properties.get("code");
		String name = properties.get("name");
		
		//Save user
		if(code != null && name != null){
			
			List<PrivacyGroup> privacyGroups = new ArrayList<PrivacyGroup>();
			
			final Query query = em.createQuery("SELECT p FROM PrivacyGroup p WHERE p.code = :code ORDER BY p.id");
			query.setParameter("code", new Integer(code.intValue()));
			
			privacyGroups.addAll(query.getResultList());
			
			if(privacyGroups.size() != 0){
					pgToPersist = privacyGroups.get(0);
					pgToPersist.setCode(code.intValue());
					pgToPersist.setTitle(name);
					pgToPersist = em.merge(pgToPersist);				
			}else{
				pgToPersist = new PrivacyGroup();
				pgToPersist.setCode(code.intValue());
				pgToPersist.setTitle(name);
				em.persist(pgToPersist);
			}
		}
		
		PrivacyGroupDTO pgPersisted = null;
		if(pgToPersist != null){
			pgPersisted = mapper.map(pgToPersist, PrivacyGroupDTO.class);
		}
		
		return pgPersisted;
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
