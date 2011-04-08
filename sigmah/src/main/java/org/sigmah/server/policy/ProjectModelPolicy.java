package org.sigmah.server.policy;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.server.policy.admin.ModelUtil;
import org.sigmah.shared.domain.PhaseModel;
import org.sigmah.shared.domain.ProjectBanner;
import org.sigmah.shared.domain.ProjectDetails;
import org.sigmah.shared.domain.ProjectModel;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.domain.ProjectModelType;
import org.sigmah.shared.domain.ProjectModelVisibility;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.element.DefaultFlexibleElement;
import org.sigmah.shared.domain.element.DefaultFlexibleElementType;
import org.sigmah.shared.domain.layout.LayoutConstraint;
import org.sigmah.shared.domain.layout.LayoutGroup;
import org.sigmah.shared.dto.PhaseModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.domain.layout.Layout;
import org.sigmah.shared.domain.logframe.LogFrameModel;

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
    
    //logFrame
    private String  log_frame_name = null;
    private Integer objectives_max = null;
	private Integer objectives_max_per_group = null;
	private Boolean objectives_enable_groups;
	private Integer objectives_max_groups = null;
	private Integer activities_max = null;
	private Boolean activities_enable_groups;
	private Integer activities_max_per_result = null;
	private Integer activities_max_groups = null;
	private Integer activities_max_per_group = null;
	private Integer results_max = null;
	private Boolean results_enable_groups;
	private Integer results_max_per_obj = null;
	private Integer results_max_groups = null;
	private Integer results_max_per_group = null;
	private Integer prerequisites_max = null;
	private Boolean prerequisites_enable_groups;
	private Integer prerequisites_max_groups = null;
	private Integer prerequisites_max_per_group = null;
	
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
    		if(projectModel.getId() !=  -1){
	    		//properties can only contain actual changes between old version and new one as verification has already been done
	    		update(user, projectModel, properties);
	    		if(modelToUpdate != null){
	    			ProjectModelDTO projectDTOUpdated = mapper.map(modelToUpdate, ProjectModelDTO.class);
	    			return projectDTOUpdated;
	    		}   			
	    	}else{
				//Create new draft ProjectModel
	    		ProjectModel pM = new ProjectModel();
	    		String pMName = (String) properties.get(AdminUtil.PROP_PM_NAME);
	    		ProjectModelType pMUse = (ProjectModelType)properties.get(AdminUtil.PROP_PM_USE);
	    		
	    		pM.setName(pMName);
	    		pM.setStatus(ProjectModelStatus.DRAFT);
	    		List<ProjectModelVisibility> visibilities = new ArrayList<ProjectModelVisibility>();
	    		ProjectModelVisibility v = new ProjectModelVisibility();
	    		v.setModel(pM);
	    		v.setType(pMUse);
	    		v.setOrganization(user.getOrganization());
	    		visibilities.add(v);
				pM.setVisibilities(visibilities);
				
				//Project model details
				ProjectDetails pMDetails = new ProjectDetails();
				
				Layout pMDetailsLayout = new Layout();
				pMDetailsLayout.setColumnsCount(1);
				pMDetailsLayout.setRowsCount(4);
				pMDetails.setLayout(pMDetailsLayout);	
				pMDetails.setProjectModel(pM);
				
				LayoutGroup detailsGroup = new LayoutGroup();
				detailsGroup.setTitle("Details");
				detailsGroup.setColumn(0);
				detailsGroup.setRow(0);
				detailsGroup.setParentLayout(pMDetailsLayout);
				
				//Default flexible elements all in default details group
				int order = 0;
				for(DefaultFlexibleElementType e : DefaultFlexibleElementType.values()){
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
				
				List<LayoutGroup> detailsGroups = new ArrayList<LayoutGroup>();
				detailsGroups.add(detailsGroup);
				pMDetailsLayout.setGroups(detailsGroups);
				
				//Banner and groups for banner
				ProjectBanner pMBanner = new ProjectBanner();
				Layout pMBannerLayout = new Layout();
				pMBannerLayout.setColumnsCount(3);
				pMBannerLayout.setRowsCount(2);
				pMBanner.setLayout(pMBannerLayout);
				pMBanner.setProjectModel(pM);
				
				List<LayoutGroup> bannerGroups = new ArrayList<LayoutGroup>();
				for(int i=0; i<3 ; i++){
					for(int j=0; j<2; j++){
						LayoutGroup bannerGroup = new LayoutGroup();
						bannerGroup.setColumn(i);
						bannerGroup.setRow(j);
						bannerGroup.setParentLayout(pMBannerLayout);
						bannerGroups.add(bannerGroup);
					}					
				}
				
				pMBannerLayout.setGroups(bannerGroups);
				
				
				pM.setProjectDetails(pMDetails);
				pM.setProjectBanner(pMBanner);
				
				
				
				em.persist(pM);
				//Add a root phase : one model has minimum one phase
				PhaseModel defaultRootPhase = new PhaseModel();
				defaultRootPhase.setName("Default root phase");
				defaultRootPhase.setParentProjectModel(pM);
				defaultRootPhase.setDisplayOrder(0);
				Layout phaseLayout = new Layout();
				phaseLayout.setColumnsCount(1);
				phaseLayout.setRowsCount(4);					
				
				LayoutGroup phaseGroup = new LayoutGroup();
				phaseGroup.setTitle("Default phase group");
				phaseGroup.setColumn(0);
				phaseGroup.setRow(0);
				phaseGroup.setParentLayout(phaseLayout);
				
				List<LayoutGroup> phaseGroups = new ArrayList<LayoutGroup>();
				phaseGroups.add(phaseGroup);
				phaseLayout.setGroups(phaseGroups);
				
				defaultRootPhase.setLayout(phaseLayout);
				em.persist(defaultRootPhase);
				
				LogFrameModel defaultLogFrame =  createLogFrame(pM);
				
				pM.setRootPhase(defaultRootPhase);
				pM.setLogFrameModel(defaultLogFrame);
				pM = em.merge(pM);
				return mapper.map(pM, ProjectModelDTO.class);
	    	}
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
			if(changes.get(AdminUtil.PROP_PM_NAME)!= null)
				model.setName((String) changes.get(AdminUtil.PROP_PM_NAME));
			if(changes.get(AdminUtil.PROP_PM_STATUS)!= null)
				model.setStatus((ProjectModelStatus)changes.get(AdminUtil.PROP_PM_STATUS));
			if(changes.get(AdminUtil.PROP_PM_USE)!= null){
				List<ProjectModelVisibility> visibilities = model.getVisibilities();
				for(ProjectModelVisibility v : visibilities){
					if(user.getOrganization().equals(v.getOrganization())){
						v.setType((ProjectModelType)changes.get(AdminUtil.PROP_PM_USE));
			    		em.merge(v);
					}
				}	    		
			}
			model = em.merge(model);
			/* ***********************************Log Frame Model*******************************************************/
			if(changes.get(AdminUtil.PROP_LOG_FRAME) != null && (Boolean)changes.get(AdminUtil.PROP_LOG_FRAME)){
				
				LogFrameModel logFrameModel = null;
				if(model.getLogFrameModel()!= null){
					logFrameModel = em.find(LogFrameModel.class, model.getLogFrameModel().getId());
				}else{
					logFrameModel = new LogFrameModel();
					logFrameModel.setProjectModel(model);
				}
				
				if(changes.get(AdminUtil.PROP_LOG_FRAME_NAME)!= null)
					log_frame_name = (String) changes.get(AdminUtil.PROP_LOG_FRAME_NAME);
				if(changes.get(AdminUtil.PROP_OBJ_MAX)!= null)
					objectives_max = (Integer) changes.get(AdminUtil.PROP_OBJ_MAX);				
				if(changes.get(AdminUtil.PROP_OBJ_MAX_PER_GROUP)!= null)
					objectives_max_per_group = (Integer) changes.get(AdminUtil.PROP_OBJ_MAX_PER_GROUP);
				if(changes.get(AdminUtil.PROP_OBJ_ENABLE_GROUPS)!= null)
					objectives_enable_groups = (Boolean) changes.get(AdminUtil.PROP_OBJ_ENABLE_GROUPS);
				if(changes.get(AdminUtil.PROP_OBJ_MAX_GROUPS)!= null)
					objectives_max_groups = (Integer) changes.get(AdminUtil.PROP_OBJ_MAX_GROUPS);
				if(changes.get(AdminUtil.PROP_A_MAX)!= null)
					activities_max = (Integer) changes.get(AdminUtil.PROP_A_MAX);
				if(changes.get(AdminUtil.PROP_A_ENABLE_GROUPS)!= null)
					activities_enable_groups = (Boolean) changes.get(AdminUtil.PROP_A_ENABLE_GROUPS);
				if(changes.get(AdminUtil.PROP_A_MAX_PER_RESULT)!= null)
					activities_max_per_result = (Integer) changes.get(AdminUtil.PROP_A_MAX_PER_RESULT);
				if(changes.get(AdminUtil.PROP_A_MAX_GROUPS)!= null)
					activities_max_groups = (Integer) changes.get(AdminUtil.PROP_A_MAX_GROUPS);
				if(changes.get(AdminUtil.PROP_A_MAX_PER_GROUP)!= null)
					activities_max_per_group = (Integer) changes.get(AdminUtil.PROP_A_MAX_PER_GROUP);
				if(changes.get(AdminUtil.PROP_R_MAX)!= null)
					results_max = (Integer) changes.get(AdminUtil.PROP_R_MAX);
				if(changes.get(AdminUtil.PROP_R_ENABLE_GROUPS)!= null)
					results_enable_groups = (Boolean) changes.get(AdminUtil.PROP_R_ENABLE_GROUPS);
				if(changes.get(AdminUtil.PROP_R_MAX_PER_OBJ)!= null)
					results_max_per_obj = (Integer) changes.get(AdminUtil.PROP_R_MAX_PER_OBJ);
				if(changes.get(AdminUtil.PROP_R_MAX_GROUPS)!= null)
					results_max_groups = (Integer) changes.get(AdminUtil.PROP_R_MAX_GROUPS);
				if(changes.get(AdminUtil.PROP_R_MAX_PER_GROUP)!= null)
					results_max_per_group = (Integer) changes.get(AdminUtil.PROP_R_MAX_PER_GROUP);
				if(changes.get(AdminUtil.PROP_P_MAX)!= null)
					prerequisites_max = (Integer) changes.get(AdminUtil.PROP_P_MAX);
				if(changes.get(AdminUtil.PROP_P_ENABLE_GROUPS)!= null)
					prerequisites_enable_groups = (Boolean) changes.get(AdminUtil.PROP_P_ENABLE_GROUPS);
				if(changes.get(AdminUtil.PROP_P_MAX_GROUPS)!= null)
					prerequisites_max_groups = (Integer) changes.get(AdminUtil.PROP_P_MAX_GROUPS);
				if(changes.get(AdminUtil.PROP_P_MAX_PER_GROUP)!= null)
					prerequisites_max_per_group = (Integer) changes.get(AdminUtil.PROP_P_MAX_PER_GROUP);
				
				logFrameModel.setName(log_frame_name);
				logFrameModel.setActivitiesGroupsMax(activities_max_groups);
				logFrameModel.setActivitiesMax(activities_max);
				logFrameModel.setActivitiesPerExpectedResultMax(activities_max_per_result);
				logFrameModel.setActivitiesPerGroupMax(activities_max_per_group);
				logFrameModel.setEnableActivitiesGroups(activities_enable_groups);
				
				logFrameModel.setEnableExpectedResultsGroups(results_enable_groups);
				logFrameModel.setExpectedResultsGroupsMax(results_max_groups);
				logFrameModel.setExpectedResultsMax(results_max);
				logFrameModel.setExpectedResultsPerGroupMax(results_max_per_group);
				logFrameModel.setExpectedResultsPerSpecificObjectiveMax(results_max_per_obj);
				
				logFrameModel.setSpecificObjectivesGroupsMax(objectives_max_groups);
				logFrameModel.setEnableSpecificObjectivesGroups(objectives_enable_groups);
				logFrameModel.setSpecificObjectivesMax(objectives_max);
				logFrameModel.setSpecificObjectivesPerGroupMax(objectives_max_per_group);
				
				logFrameModel.setPrerequisitesGroupsMax(prerequisites_max_groups);
				logFrameModel.setEnablePrerequisitesGroups(prerequisites_enable_groups);
				logFrameModel.setPrerequisitesMax(prerequisites_max);
				logFrameModel.setPrerequisitesPerGroupMax(prerequisites_max_per_group);
				
				if(model.getLogFrameModel()!= null){
					logFrameModel = em.merge(logFrameModel);
				}else{
					em.persist(logFrameModel);
				}
				modelToUpdate = model;
				modelToUpdate.setLogFrameModel(logFrameModel);
			}
						
			/* ***********************************Flexible Element******************************************************/
			
			
			if(changes.get(AdminUtil.PROP_FX_FLEXIBLE_ELEMENT) != null){			
				
				ModelUtil.persistFlexibleElement(em, mapper, changes, model);
				modelToUpdate = em.find(ProjectModel.class, model.getId());
				
				
			}
			
			// ***********************************Phases*****************************************************
			final PhaseModelDTO phaseDTOToSave = (PhaseModelDTO) changes.get(AdminUtil.PROP_PHASE_MODEL);
			
			final Integer displayOrder = (Integer) changes.get(AdminUtil.PROP_PHASE_ORDER);
			final Boolean root = (Boolean) changes.get(AdminUtil.PROP_PHASE_ROOT);
			final Integer numRows = (Integer) changes.get(AdminUtil.PROP_PHASE_ROWS);
			
			PhaseModel phaseToSave = new PhaseModel();
			
			if(phaseDTOToSave!= null){
				phaseToSave.setName(phaseDTOToSave.getName());
				if(displayOrder != null)
					phaseToSave.setDisplayOrder(displayOrder);
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
					if(numRows != null)
						phaseFound.getLayout().setRowsCount(numRows);
						
					if(displayOrder != null)
						phaseFound.setDisplayOrder(displayOrder);
					phaseToSave = em.merge(phaseFound);
					
				}else{//create new phase
					phaseToSave.setParentProjectModel(model);
					Layout phaseLayout = new Layout();
					phaseLayout.setColumnsCount(1);
					if(numRows != null)
						phaseLayout.setRowsCount(numRows);	
					else
						phaseLayout.setRowsCount(4);
					
					LayoutGroup phaseGroup = new LayoutGroup();
					phaseGroup.setTitle(phaseToSave.getName() + " default group");
					phaseGroup.setColumn(0);
					phaseGroup.setRow(0);
					phaseGroup.setParentLayout(phaseLayout);
					
					List<LayoutGroup> phaseGroups = new ArrayList<LayoutGroup>();
					phaseGroups.add(phaseGroup);
					phaseLayout.setGroups(phaseGroups);
					
					phaseToSave.setLayout(phaseLayout);
					em.persist(phaseToSave);					
				}
				model.addPhase(phaseToSave);
				if(root != null && root)
					model.setRootPhase(phaseToSave);
				else if(root != null && !root && phaseFound != null && model.getRootPhase().getId() == phaseFound.getId())//Only if phase was root and we want to change it
					model.setRootPhase(null);
				modelToUpdate = em.merge(model);
			}			
		}
			
	}
	
	private LogFrameModel createLogFrame(ProjectModel model){
		LogFrameModel logFrameModel = new LogFrameModel();
		logFrameModel.setName("Default log frame");
		logFrameModel.setActivitiesGroupsMax(3);
		logFrameModel.setActivitiesMax(3);
		logFrameModel.setActivitiesPerExpectedResultMax(3);
		logFrameModel.setActivitiesPerGroupMax(3);
		logFrameModel.setEnableActivitiesGroups(true);
		
		logFrameModel.setEnableExpectedResultsGroups(true);
		logFrameModel.setExpectedResultsGroupsMax(3);
		logFrameModel.setExpectedResultsMax(3);
		logFrameModel.setExpectedResultsPerGroupMax(3);
		logFrameModel.setExpectedResultsPerSpecificObjectiveMax(3);
		
		logFrameModel.setSpecificObjectivesGroupsMax(3);
		logFrameModel.setEnableSpecificObjectivesGroups(true);
		logFrameModel.setSpecificObjectivesMax(3);
		logFrameModel.setSpecificObjectivesPerGroupMax(3);
		
		logFrameModel.setPrerequisitesGroupsMax(3);
		logFrameModel.setEnablePrerequisitesGroups(true);
		logFrameModel.setPrerequisitesMax(3);
		logFrameModel.setPrerequisitesPerGroupMax(3);
		
		logFrameModel.setProjectModel(model);
		
		em.persist(logFrameModel);
		return logFrameModel;
	}
}
