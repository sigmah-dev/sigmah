package org.sigmah.server.policy;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.server.policy.admin.ModelUtil;
import org.sigmah.shared.domain.OrgUnitBanner;
import org.sigmah.shared.domain.OrgUnitDetails;
import org.sigmah.shared.domain.OrgUnitModel;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.element.DefaultFlexibleElement;
import org.sigmah.shared.domain.element.DefaultFlexibleElementType;
import org.sigmah.shared.domain.layout.LayoutConstraint;
import org.sigmah.shared.domain.layout.LayoutGroup;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.domain.layout.Layout;
import com.google.inject.Inject;

/**
 * Handler for updating Org unit model command.
 * 
 * @author nrebiai
 * 
 */
public class OrgUnitModelPolicy implements EntityPolicy<OrgUnitModel>  {

    private final EntityManager em;
    private final Mapper mapper;
    private OrgUnitModelDTO orgUnitModel;
    private OrgUnitModel modelToUpdate;
    
	
	private final static Log log = LogFactory.getLog(OrgUnitModelPolicy.class);

    @Inject
    public OrgUnitModelPolicy(EntityManager em, Mapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }

    @Override
    public Object create(User user, PropertyMap properties) {
    	
    	orgUnitModel = (OrgUnitModelDTO) properties.get(AdminUtil.ADMIN_ORG_UNIT_MODEL);
    	//Only draft models can be changed
    	if(orgUnitModel != null && ProjectModelStatus.DRAFT.equals(orgUnitModel.getStatus())){
    		if(orgUnitModel.getId() !=  -1){
	    		//properties can only contain actual changes between old version and new one as verification has already been done
	    		update(user, orgUnitModel, properties);
	    		if(modelToUpdate != null){
	    			OrgUnitModelDTO orgUnitDTOUpdated = mapper.map(modelToUpdate, OrgUnitModelDTO.class);
	    			return orgUnitDTOUpdated;
	    		}   			
	    	}else{
				//Create new draft OrgUnitModel
	    		OrgUnitModel oM = createOrgUnitModel(null, properties, user);
				
				OrgUnitDetails oMDetails = new OrgUnitDetails();
				
				Layout oMDetailsLayout = new Layout();
				oMDetailsLayout.setColumnsCount(1);
				oMDetailsLayout.setRowsCount(4);
				oMDetails.setLayout(oMDetailsLayout);	
				oMDetails.setOrgUnitModel(oM);
				
				LayoutGroup detailsGroup = new LayoutGroup();
				detailsGroup.setColumn(0);
				detailsGroup.setRow(0);
				detailsGroup.setParentLayout(oMDetailsLayout);
				
				//Default flexible elements all in default details group
				int order = 0;
				for(DefaultFlexibleElementType e : DefaultFlexibleElementType.values()){
					if(!DefaultFlexibleElementType.START_DATE.equals(e) && 
							!DefaultFlexibleElementType.END_DATE.equals(e)
							&& !(DefaultFlexibleElementType.BUDGET.equals(e) && Boolean.FALSE.equals(oM.getHasBudget()))
							){
						DefaultFlexibleElement defaultElement = new DefaultFlexibleElement();
						defaultElement.setType(e);
						defaultElement.setValidates(false);
						defaultElement.setAmendable(false);
						em.persist(defaultElement);
						LayoutConstraint defaultLayoutConstraint = new LayoutConstraint();
						defaultLayoutConstraint.setParentLayoutGroup(detailsGroup);
						defaultLayoutConstraint.setElement(defaultElement);
						defaultLayoutConstraint.setSortOrder(order++);
						detailsGroup.addConstraint(defaultLayoutConstraint);
					}
				}
				
				List<LayoutGroup> detailsGroups = new ArrayList<LayoutGroup>();
				detailsGroups.add(detailsGroup);
				oMDetailsLayout.setGroups(detailsGroups);
				
				OrgUnitBanner oMBanner = new OrgUnitBanner();
				Layout oMBannerLayout = new Layout();
				oMBannerLayout.setColumnsCount(3);
				oMBannerLayout.setRowsCount(2);
				oMBanner.setLayout(oMBannerLayout);
				oMBanner.setOrgUnitModel(oM);
				
				LayoutGroup bannerGroup = new LayoutGroup();
				bannerGroup.setColumn(0);
				bannerGroup.setRow(0);
				bannerGroup.setParentLayout(oMBannerLayout);
				
				List<LayoutGroup> bannerGroups = new ArrayList<LayoutGroup>();
				bannerGroups.add(bannerGroup);
				oMBannerLayout.setGroups(bannerGroups);
				
				
				oM.setDetails(oMDetails);
				oM.setBanner(oMBanner);
				
				em.persist(oM);
				return mapper.map(oM, OrgUnitModelDTO.class);
	    	}
    	}
    	return null;
    }

	@Override
	public void update(User user, Object entity, PropertyMap changes) {
		OrgUnitModel model = null;
		if(orgUnitModel != null){
			model = em.find(OrgUnitModel.class, new Integer(orgUnitModel.getId()));		
		}
		if(model != null){
			if(changes.get(AdminUtil.PROP_OM_NAME) != null){//Update model
				model = createOrgUnitModel(model, changes, user);
				model = em.merge(model);
			}			
			/* ***********************************Flexible Element******************************************************/
			if(changes.get(AdminUtil.PROP_FX_FLEXIBLE_ELEMENT) != null){
				ModelUtil.persistFlexibleElement(em, mapper, changes, model);
				modelToUpdate = em.find(OrgUnitModel.class, model.getId());		
			}
						
		}
			
	}
	
	private OrgUnitModel createOrgUnitModel(OrgUnitModel oM, PropertyMap properties, User user){
		if(oM == null)
			oM = new OrgUnitModel();
		
		String oMName = null;
		if(properties.get(AdminUtil.PROP_OM_NAME) != null)
			oMName = (String) properties.get(AdminUtil.PROP_OM_NAME);
		String oMTitle = null;
		if(properties.get(AdminUtil.PROP_OM_TITLE) != null)
			oMTitle = (String) properties.get(AdminUtil.PROP_OM_TITLE);
		Boolean hasBudget = null;
		if(properties.get(AdminUtil.PROP_OM_HAS_BUDGET) != null)
			hasBudget = (Boolean) properties.get(AdminUtil.PROP_OM_HAS_BUDGET);
		Boolean containsProjects = null;
		if(properties.get(AdminUtil.PROP_OM_CONTAINS_PROJECTS) != null)
			containsProjects = (Boolean) properties.get(AdminUtil.PROP_OM_CONTAINS_PROJECTS);

		
		oM.setName(oMName);
		oM.setStatus(ProjectModelStatus.DRAFT);
		oM.setTitle(oMTitle);
		oM.setHasBudget(hasBudget);
		oM.setOrganization(user.getOrganization());
		oM.setCanContainProjects(containsProjects);
		return oM;
	}
}
