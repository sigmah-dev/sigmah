package org.sigmah.server.policy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.client.page.admin.model.common.ProjectModelForm;
import org.sigmah.client.page.admin.model.common.element.ElementForm;
import org.sigmah.client.page.admin.model.common.element.ElementTypeEnum;
import org.sigmah.shared.domain.PhaseModel;
import org.sigmah.shared.domain.ProjectBanner;
import org.sigmah.shared.domain.ProjectDetails;
import org.sigmah.shared.domain.ProjectModel;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.domain.ProjectModelType;
import org.sigmah.shared.domain.ProjectModelVisibility;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.element.*;
import org.sigmah.shared.domain.layout.LayoutConstraint;
import org.sigmah.shared.domain.layout.LayoutGroup;
import org.sigmah.shared.domain.profile.PrivacyGroup;
import org.sigmah.shared.dto.PhaseModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.report.ReportModelDTO;
import org.sigmah.shared.domain.layout.Layout;

import com.google.inject.Inject;

/**
 * Handler for updating Project model command.
 * 
 * @author nrebiai
 * 
 */
public class ProjectModelPolicy implements EntityPolicy<ProjectModel>  {

    private final EntityManager em;
    private final Mapper mapper;
    private ProjectModelDTO projectModel;
    private ProjectModel modelToUpdate;
    
    @SuppressWarnings("unused")
	private final static Log log = LogFactory.getLog(ProjectModelPolicy.class);

    @Inject
    public ProjectModelPolicy(EntityManager em, Mapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }

    @Override
    public Object create(User user, PropertyMap properties) {
    	
    	projectModel = (ProjectModelDTO) properties.get(AdminUtil.ADMIN_PROJECT_MODEL);
    	//Only draft models can be changed
    	if(projectModel != null && ProjectModelStatus.DRAFT.equals(projectModel.getStatus())){
    		//properties can only contain actual changes between old version and new one as verification has already been done
    		update(user, projectModel, properties);
    		if( modelToUpdate != null){
    			log.debug("@ProjectModePolicy : Model updated");
    			ProjectModelDTO projectDTOUpdated = mapper.map(modelToUpdate, ProjectModelDTO.class);
    			return projectDTOUpdated;
    		}   			
    	}else{
    		//Create new draft ProjectModel
    		ProjectModel pM = new ProjectModel();
    		String pMName = (String) properties.get(ProjectModelForm.PM_NAME_PROPERTY);
    		ProjectModelType pMUse = (ProjectModelType)properties.get(ProjectModelForm.PM_USE_PROPERTY);
    		
    		pM.setName(pMName);
    		pM.setStatus(ProjectModelStatus.DRAFT);
    		List<ProjectModelVisibility> visibilities = new ArrayList<ProjectModelVisibility>();
    		ProjectModelVisibility v = new ProjectModelVisibility();
    		v.setModel(pM);
    		v.setType(pMUse);
    		v.setOrganization(user.getOrganization());
    		visibilities.add(v);
			pM.setVisibilities(visibilities);
			
			ProjectDetails pMDetails = new ProjectDetails();
			Layout pMDetailsLayout = new Layout();
			pMDetailsLayout.setColumnsCount(1);
			pMDetailsLayout.setRowsCount(4);
			pMDetails.setLayout(pMDetailsLayout);	
			pMDetails.setProjectModel(pM);
			
			ProjectBanner pMBanner = new ProjectBanner();
			Layout pMBannerLayout = new Layout();
			pMBannerLayout.setColumnsCount(3);
			pMBannerLayout.setRowsCount(2);
			pMBanner.setLayout(pMBannerLayout);
			pMBanner.setProjectModel(pM);
			
			
			pM.setProjectDetails(pMDetails);
			pM.setProjectBanner(pMBanner);
			
			em.persist(pM);
			
			
			return mapper.map(pM, ProjectModelDTO.class);
    	}
    	return null;
    }

	@Override
	public void update(User user, Object entity, PropertyMap changes) {
		ProjectModel model = null;
		if(projectModel != null){
			model = em.find(ProjectModel.class, new Integer(projectModel.getId()).longValue());		
		}
		if(model != null){
			/* ***********************************Flexible Element******************************************************/
			//Common attributes
			String name = changes.get(ElementForm.FX_NAME);		
			ElementTypeEnum type = (ElementTypeEnum) changes.get(ElementForm.FX_TYPE);
			Boolean isCompulsory = null;
			if(changes.get(ElementForm.FX_IS_COMPULSARY)!=null)
				isCompulsory = (Boolean) changes.get(ElementForm.FX_IS_COMPULSARY);
			PrivacyGroupDTO pg = null;
			if(changes.get(ElementForm.FX_PRIVACY_GROUP)!=null)
				pg = (PrivacyGroupDTO) changes.get(ElementForm.FX_PRIVACY_GROUP);
			Boolean amend = null;
			if(changes.get(ElementForm.FX_AMENDABLE)!=null)
				amend = (Boolean) changes.get(ElementForm.FX_AMENDABLE);
			
			
			//Position
			LayoutGroupDTO group = null;
			if(changes.get(ElementForm.FX_GROUP)!= null)
				group = (LayoutGroupDTO) changes.get(ElementForm.FX_GROUP);
			Integer order = null;
			if(changes.get(ElementForm.FX_ORDER_IN_GROUP)!=null)
				order = (Integer) changes.get(ElementForm.FX_ORDER_IN_GROUP);
			Boolean inBanner = null;
			if(changes.get(ElementForm.FX_IN_BANNER)!=null)
				inBanner = (Boolean) changes.get(ElementForm.FX_IN_BANNER);
			Integer posB = null;
			if(changes.get(ElementForm.FX_POS_IN_BANNER)!=null)
				posB = (Integer) changes.get(ElementForm.FX_POS_IN_BANNER);
			
			//FIXME
			@SuppressWarnings("unchecked")
			HashMap<String, Object> oldLayoutFields = (HashMap<String, Object>) changes.get(ElementForm.FX_OLD_FIELDS);
			LayoutConstraintDTO oldLayoutConstraintDTO = (LayoutConstraintDTO) oldLayoutFields.get(ElementForm.FX_LC);
			LayoutGroupDTO oldGroup = (LayoutGroupDTO) oldLayoutFields.get(ElementForm.FX_GROUP);
			LayoutConstraintDTO oldBannerLayoutConstraintDTO = (LayoutConstraintDTO) oldLayoutFields.get(ElementForm.FX_LC_BANNER);
			ElementTypeEnum oldType = (ElementTypeEnum)oldLayoutFields.get(ElementForm.FX_TYPE);
			
			//Specific attributes
			Character textType = (Character) changes.get(ElementForm.FX_TEXT_TYPE);
			Integer maxLimit = null;
			if(changes.get(ElementForm.FX_MAX_LIMIT)!=null)
				maxLimit = (Integer) changes.get(ElementForm.FX_MAX_LIMIT);
			Integer minLimit = null;
			if(changes.get(ElementForm.FX_MIN_LIMIT)!=null)
				minLimit = (Integer) changes.get(ElementForm.FX_MIN_LIMIT);
			Integer length = null;
			if(changes.get(ElementForm.FX_LENGTH)!=null)
				length = (Integer) changes.get(ElementForm.FX_LENGTH);	
			Boolean decimal = null;
			if(changes.get(ElementForm.FX_DECIMAL)!=null)
				decimal = (Boolean) changes.get(ElementForm.FX_DECIMAL);
			ReportModelDTO reportModel = null;
			if(changes.get(ElementForm.FX_REPORT_MODEL)!=null)
				reportModel = (ReportModelDTO) changes.get(ElementForm.FX_REPORT_MODEL);
			
			FlexibleElementDTO flexibleEltDTO = null;
			if(changes.get(ElementForm.FX_FLEXIBLE_ELEMENT) != null){
				flexibleEltDTO = (FlexibleElementDTO) changes.get(ElementForm.FX_FLEXIBLE_ELEMENT);
				
				FlexibleElement flexibleElt = null;
				if(flexibleEltDTO.getId()!= 0){
					flexibleElt = em.find(FlexibleElement.class, new Integer(flexibleEltDTO.getId()).longValue());
				}else{
					flexibleElt = (FlexibleElement)createNewFlexibleElement(oldType, type, flexibleElt);
				}
								
				log.debug("Saving : (" + name + "," + type + "," + group + "," + order + "," + inBanner + "," + posB + "," + isCompulsory + "," + pg + "," + amend + ")");
				log.debug("Also Saving : (" + maxLimit + "," + minLimit + "," + textType + "," + length + "," + decimal + "," + reportModel + ")");
				
				
				Boolean basicChanges = false;	
				if(flexibleElt != null){//update flexible element					
					////////////////// First, basic attributes
					if(name!=null){
						flexibleElt.setLabel(name);
						basicChanges = true;
					}					
					if(amend != null){
						flexibleElt.setAmendable(amend);
						basicChanges = true;
					}					
					if(isCompulsory != null){
						flexibleElt.setValidates(isCompulsory);
						basicChanges = true;
					}					
					if(pg != null){					
						PrivacyGroup pgToPersist = em.find(PrivacyGroup.class, pg.getId());
						if(pgToPersist != null){
							flexibleElt.setPrivacyGroup(pgToPersist);
							basicChanges = true;
						}						
					}		
					if(basicChanges && flexibleElt.getId() != null)
						flexibleElt = em.merge(flexibleElt);
					else
						em.persist(flexibleElt);
				}
				
				//////////////////Position : Change layout_constraint, reorder
				FlexibleElement oldFX = flexibleElt;
				//LayoutGroup parentLayoutGroup = em.find(LayoutGroup.class, new Integer(oldGroup.getId()).longValue());
				if(group != null){		//group changed	
					LayoutGroup  parentLayoutGroup = em.find(LayoutGroup.class, new Integer(group.getId()).longValue());												
					LayoutConstraint newLayoutConstraint = new LayoutConstraint();					
					if(parentLayoutGroup != null){						
						newLayoutConstraint.setElement(flexibleElt);
						newLayoutConstraint.setParentLayoutGroup(parentLayoutGroup);
						newLayoutConstraint.setSortOrder(new Integer(parentLayoutGroup.getConstraints().size()));
						
						if(oldLayoutConstraintDTO!=null){//Merge
							newLayoutConstraint.setId(new Integer(oldLayoutConstraintDTO.getId()).longValue());
							newLayoutConstraint = em.merge(newLayoutConstraint);
						}else{//Persist
							em.persist(newLayoutConstraint);
						}
					}
				}				
							
	            //////////////////Banner
				if(inBanner != null){//Fact of being or not in banner has changed
					if(inBanner){//New to banner
						changeBanner(posB, model, flexibleElt);
					}else{//delete from banner
						if(oldBannerLayoutConstraintDTO != null){
							LayoutConstraint oldBannerLayoutConstraint = mapper.map(oldBannerLayoutConstraintDTO, LayoutConstraint.class);
							em.remove(oldBannerLayoutConstraint);
						}					
					}
				}else{//same state on banner 
					if(posB!= null){//Position has changed means surely element was already in banner so there's an old banner layout constraint
						LayoutConstraint oldBannerLayoutConstraint = mapper.map(oldBannerLayoutConstraintDTO, LayoutConstraint.class);
						changePositionInBanner(posB, model, flexibleElt, oldBannerLayoutConstraint);
					}
				}
				
				
				//////////////////Type
				if(type != null){					
					flexibleElt = (FlexibleElement)createNewFlexibleElement(oldType, type, flexibleElt);						
					log.debug("new type " + flexibleElt.getClass());
				}
				em.flush();
				em.clear();
				flexibleElt = em.find(FlexibleElement.class, flexibleElt.getId());
				//////////////////Specific changes
				Boolean specificChanges = false;
				
				if(ElementTypeEnum.FILES_LIST.equals(type) || (ElementTypeEnum.FILES_LIST.equals(oldType) && type == null)){		
					FilesListElement filesListElement = (FilesListElement) flexibleElt;
					//FilesListElement filesListElement = em.find(FilesListElement.class, flexibleElt.getId());
					if(filesListElement != null){
						if(maxLimit != null){
							filesListElement.setLimit(maxLimit);
							specificChanges = true;
						}
						if(specificChanges){
							filesListElement = em.merge(filesListElement);
							flexibleElt = filesListElement;
						}
					}			
				}else if(ElementTypeEnum.TEXT_AREA.equals(type) || (ElementTypeEnum.TEXT_AREA.equals(oldType) && type == null)){
					TextAreaElement textAreaElement = (TextAreaElement) flexibleElt;
					if(textAreaElement != null){
						if(maxLimit != null){
							((TextAreaElement)flexibleElt).setMaxValue(new Integer(maxLimit).longValue());
							specificChanges = true;
						}
						if(minLimit != null){
							((TextAreaElement)flexibleElt).setMinValue(new Integer(minLimit).longValue());
							specificChanges = true;
						}
						if(length != null){
							((TextAreaElement)flexibleElt).setLength(length);
							specificChanges = true;
						}
						if(decimal != null){
							((TextAreaElement)flexibleElt).setIsDecimal(decimal);
							specificChanges = true;
						}
						if(textType != null){
							((TextAreaElement)flexibleElt).setType(textType);
							specificChanges = true;
						}
						if(specificChanges){
							flexibleElt = em.merge((TextAreaElement)flexibleElt);						
						}						
					}
					
				}else if(ElementTypeEnum.REPORT.equals(type) || (ElementTypeEnum.REPORT.equals(oldType) && type == null)){
					ReportElement reportElement = em.find(ReportElement.class, flexibleElt.getId());
					if(reportElement != null){
						if(reportModel != null){
							((ReportElement)flexibleElt).setModelId(reportModel.getId());
							specificChanges = true;
						}
						
						if(specificChanges){
							flexibleElt = em.merge((ReportElement)flexibleElt);						
						}						
					}					
				}else if(ElementTypeEnum.REPORT_LIST.equals(type) || (ElementTypeEnum.REPORT_LIST.equals(oldType) && type == null)){
					ReportListElement reportElement = em.find(ReportListElement.class, flexibleElt.getId());
					if(reportElement != null){
						if(reportModel != null){
							((ReportListElement)flexibleElt).setModelId(reportModel.getId());
							specificChanges = true;
						}
						
						if(specificChanges){
							flexibleElt = em.merge((ReportListElement)flexibleElt);						
						}						
					}
					
				}
				em.flush();
				em.clear();
				modelToUpdate = em.find(ProjectModel.class, model.getId());
				
				
			}
			
			// ***********************************Phases*****************************************************
			final PhaseModelDTO phaseDTOToSave = (PhaseModelDTO) changes.get("phase");
			PhaseModel phaseToSave = new PhaseModel();
			
			if(phaseDTOToSave!= null){
				log.debug("updating phases");
				phaseToSave.setName(phaseDTOToSave.getName());
				//successors
				for(PhaseModelDTO sucDTO : phaseDTOToSave.getSuccessorsDTO()){
					if(sucDTO.getId()!=0){
						PhaseModel suc = em.find(PhaseModel.class, Long.valueOf(String.valueOf(sucDTO.getId())));
						phaseToSave.getSuccessors().add(suc);
					}			
				}
				
				PhaseModel phaseFound = null;
				
				for(PhaseModel phase : model.getPhases()){
					if(phaseDTOToSave.getId() == phase.getId()){
						phaseFound = phase;
					}
				}

				if(phaseFound != null){//phase is old
					phaseFound.setName(phaseToSave.getName());
					phaseFound.setSuccessors(phaseToSave.getSuccessors());	
					phaseToSave = phaseFound;
				}
				
				if(phaseToSave != null){			
					phaseToSave = em.merge(phaseToSave);
					model.addPhase(phaseToSave);
					modelToUpdate = em.merge(model);
				}
			}			
		}
			
	}
	
	private void changeBanner(Integer posB, ProjectModel model, FlexibleElement flexibleElt){
		LayoutGroup bannerGroup = model.getProjectBanner().getLayout().getGroups().get(0);
		
		LayoutConstraint newLayoutConstraint = null;
		boolean positionTaken = false;
		for(LayoutConstraint lc : bannerGroup.getConstraints()){
			if(posB.equals(lc.getSortOrder())){
				positionTaken = true;
				newLayoutConstraint = lc;
				lc.setElement(flexibleElt);
				
				newLayoutConstraint = em.merge(lc);
			}
		}
		if(!positionTaken){
			
			newLayoutConstraint = new LayoutConstraint();
			
			newLayoutConstraint.setElement(flexibleElt);
			newLayoutConstraint.setParentLayoutGroup(bannerGroup);
			newLayoutConstraint.setSortOrder(new Integer(posB));
			
			em.persist(newLayoutConstraint);
		}
	}
	
	private void changePositionInBanner(Integer posB, ProjectModel model, FlexibleElement flexibleElt, LayoutConstraint oldBannerLayoutConstraint){
		LayoutGroup bannerGroup = model.getProjectBanner().getLayout().getGroups().get(0);

		//Delete any constraint that places another flexible element in the same position
		for(LayoutConstraint lc : bannerGroup.getConstraints()){
			if(posB.equals(lc.getSortOrder())){
				em.remove(lc);
			}
		}
		oldBannerLayoutConstraint.setElement(flexibleElt);
		oldBannerLayoutConstraint.setParentLayoutGroup(bannerGroup);
		oldBannerLayoutConstraint.setSortOrder(posB);
		em.merge(oldBannerLayoutConstraint);
	}
	
	private String retrieveTable(String className){
		String table = null;
		int bI = className.lastIndexOf(".") + 1;
		table = className.substring(bI);
		
		try{
			Class c = (Class<FlexibleElement>) Class.forName(className);
			Table a = (Table) c.getAnnotation(Table.class);
			table = a.name();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return table;
	}
	
	private void changeOldType(ElementTypeEnum type, FlexibleElement flexibleElement){			
		String oldflexTable = retrieveTable(ElementTypeEnum.getClassName(type));
		if(oldflexTable != null){
			em.createNativeQuery("Delete from "+ oldflexTable  + " where " +
            "id_flexible_element = :flexId")
            .setParameter("flexId", flexibleElement.getId())
            .executeUpdate();
		}
	}
	
	private Object createNewFlexibleElement(ElementTypeEnum oldType, ElementTypeEnum type, FlexibleElement flexibleElement){
		
		String flexTable = null;
			
		Class c;
		Object newElement = null;
		try {
			c = Class.forName(ElementTypeEnum.getClassName(type));
			newElement = c.newInstance();								
			
			if(flexibleElement.getId() != null && oldType !=  null){	
				log.debug("Old Type " + oldType + " " + flexibleElement.getClass());
				((FlexibleElement)newElement).setLabel(flexibleElement.getLabel());
				((FlexibleElement)newElement).setPrivacyGroup(flexibleElement.getPrivacyGroup());
				((FlexibleElement)newElement).setValidates(flexibleElement.isValidates());
				((FlexibleElement)newElement).setAmendable(flexibleElement.isAmendable());
				((FlexibleElement)newElement).setId(flexibleElement.getId());
				flexTable = retrieveTable(c.getName());
				//Update Type
				if(flexTable != null){					
					changeOldType(oldType, flexibleElement);						
					em.createNativeQuery("INSERT INTO "+ flexTable + " (id_flexible_element) " +
	                "Values (:flexId)")
	                .setParameter("flexId", flexibleElement.getId())
	                .executeUpdate();
				}
			}									
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		return newElement;
	}
}
