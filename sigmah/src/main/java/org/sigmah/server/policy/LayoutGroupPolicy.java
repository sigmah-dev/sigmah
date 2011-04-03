package org.sigmah.server.policy;

import java.util.Map;

import javax.persistence.EntityManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.shared.domain.OrgUnitDetails;
import org.sigmah.shared.domain.OrgUnitModel;
import org.sigmah.shared.domain.PhaseModel;
import org.sigmah.shared.domain.ProjectDetails;
import org.sigmah.shared.domain.ProjectModel;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.layout.Layout;
import org.sigmah.shared.domain.layout.LayoutGroup;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.inject.Inject;
/**
 * Create layout group policy.
 * 
 * @author nrebiai
 * 
 */
public class LayoutGroupPolicy implements EntityPolicy<LayoutGroup> {
	
	private final Mapper mapper;
	private final EntityManager em;
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(LayoutGroupPolicy.class);
	
	
	@Inject
    public LayoutGroupPolicy(EntityManager em, Mapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }

	@SuppressWarnings("unchecked")
	@Override
	public Object create(User executingUser, PropertyMap properties) {

		LayoutGroup groupToPersist  = null;
				
		LayoutGroupDTO layoutGroupDTOToPersist = (LayoutGroupDTO) properties.get(AdminUtil.PROP_NEW_GROUP_LAYOUT);
		ProjectModelDTO projectModelDTO = (ProjectModelDTO) properties.get(AdminUtil.ADMIN_PROJECT_MODEL);
		OrgUnitModelDTO orgUnitModelDTO = (OrgUnitModelDTO) properties.get(AdminUtil.ADMIN_ORG_UNIT_MODEL);
		
		groupToPersist = mapper.map(layoutGroupDTOToPersist, LayoutGroup.class);
		
		//update
		if(layoutGroupDTOToPersist.getId() > 0){
			groupToPersist = em.merge(groupToPersist);
			
		}else{
			//Save group
			if(groupToPersist != null){
				groupToPersist.setId(null);// id = -1				
				em.persist(groupToPersist);
			}
		}
		
		
		LayoutGroupDTO groupPersisted = null;
		if(groupToPersist != null){
			groupPersisted = mapper.map(groupToPersist, LayoutGroupDTO.class);
		}
		
		return groupPersisted;
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
