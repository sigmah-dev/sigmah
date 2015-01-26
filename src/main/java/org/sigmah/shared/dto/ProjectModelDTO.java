/*
 * All Sigmah code is released under the GNU General Public License v3 See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sigmah.client.page.admin.model.common.element.ElementTypeEnum;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.domain.ProjectModelType;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.logframe.LogFrameModelDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ProjectModelDTO extends BaseModelData implements EntityDTO {

    private static final long serialVersionUID = 8508466949743884046L;

    /**
     * Localizes an flexible element in the project model.
     * 
     * @author tmi
     * 
     */
    protected static class LocalizedElement {

        private final PhaseModelDTO phaseModel;
        private final FlexibleElementDTO element;

        protected LocalizedElement(PhaseModelDTO phaseModel, FlexibleElementDTO element) {
            this.phaseModel = phaseModel;
            this.element = element;
        }

        /**
         * Gets the flexible element.
         * 
         * @return The flexible element.
         */
        public FlexibleElementDTO getElement() {
            return element;
        }

        /**
         * Get the phase model in which the element is displayed, or
         * <code>null</code> if the element is in the details page.
         * 
         * @return The phase model of the element or <code>null</code>.
         */
        public PhaseModelDTO getPhaseModel() {
            return phaseModel;
        }
    }

    private transient HashMap<Class<? extends FlexibleElementDTO>, List<LocalizedElement>> localizedElements;

    @Override
    public String getEntityName() {
        return "ProjectModel";
    }

    // Project model id
    public int getId() {
    	if(get("id") != null)
    		return (Integer) get("id");
    	else
    		return -1;
    }

    public void setId(int id) {
        set("id", id);
    }

    // Project model name
    public String getName() {
        return get("name");
    }

    public void setName(String name) {
        set("name", name);
    }

    // Root phase model DTO
    public PhaseModelDTO getRootPhaseModelDTO() {
        return get("rootPhaseModelDTO");
    }

    public void setRootPhaseModelDTO(PhaseModelDTO rootPhaseModelDTO) {
        set("rootPhaseModelDTO", rootPhaseModelDTO);
    }

    // Phase models list
    public List<PhaseModelDTO> getPhaseModelsDTO() {
        return get("phaseModelsDTO");
    }

    public void setPhaseModelsDTO(List<PhaseModelDTO> phaseModelsDTO) {
        set("phaseModelsDTO", phaseModelsDTO);
    }

    // Reference to the project banner
    public ProjectBannerDTO getProjectBannerDTO() {
        return get("projectBannerDTO");
    }

    public void setProjectBannerDTO(ProjectBannerDTO projectBannerDTO) {
        set("projectBannerDTO", projectBannerDTO);
    }

    // Reference to the project details
    public ProjectDetailsDTO getProjectDetailsDTO() {
        return get("projectDetailsDTO");
    }

    public void setProjectDetailsDTO(ProjectDetailsDTO projectDetailsDTO) {
        set("projectDetailsDTO", projectDetailsDTO);
    }

    // Project visibilities
    public List<ProjectModelVisibilityDTO> getVisibilities() {
        return get("visibilities");
    }

    public void setVisibilities(List<ProjectModelVisibilityDTO> visibilities) {
        set("visibilities", visibilities);
    }
    
    //logFrame
    public LogFrameModelDTO getLogFrameModelDTO() {
        return get("logFrameModelDTO");
    }

    public void setLogFrameModelDTO(LogFrameModelDTO logFrameModelDTO) {
        set("logFrameModelDTO", logFrameModelDTO);
    }

    /**
     * Gets the type of this model for the given organization. If this model
     * isn't visible for this organization, <code>null</code> is returned.
     * 
     * @param organizationId
     *            The organization.
     * @return The type of this model for the given organization,
     *         <code>null</code> otherwise.
     */
    public ProjectModelType getVisibility(int organizationId) {

        if (getVisibilities() == null) {
            return null;
        }

        for (final ProjectModelVisibilityDTO visibility : getVisibilities()) {
            if (visibility.getOrganizationId() == organizationId) {
                return visibility.getType();
            }
        }

        return null;
    }
   
    public void setStatus(ProjectModelStatus status) {
    	set("status",status);
	}
    
    public  ProjectModelStatus getStatus() {
    	return get("status");
	}

   public List<FlexibleElementDTO> getGlobalExportElements(){
    	List<FlexibleElementDTO> allElements = new ArrayList<FlexibleElementDTO>();
    	
    	List<LayoutGroupDTO> layoutGroupDTOs=new ArrayList<LayoutGroupDTO>();
    	//add phase groups
    	for(PhaseModelDTO phaseDTO : getPhaseModelsDTO()){
    		layoutGroupDTOs.addAll(phaseDTO.getLayoutDTO().getLayoutGroupsDTO());
			 
    	}
    	//add details groups
    	if(getProjectDetailsDTO().getLayoutDTO()!=null){
    		layoutGroupDTOs.addAll(getProjectDetailsDTO().getLayoutDTO().getLayoutGroupsDTO());
    	}
    	
    	//gather elements
    	for(LayoutGroupDTO lg : layoutGroupDTOs){
			for(LayoutConstraintDTO lc : lg.getLayoutConstraintsDTO()){
				FlexibleElementDTO element = lc.getFlexibleElementDTO();
				ElementTypeEnum type=element.getElementType();
				
 				if(ElementTypeEnum.DEFAULT.equals(type)||
 						ElementTypeEnum.CHECKBOX.equals(type)||
 						ElementTypeEnum.TEXT_AREA.equals(type)||
 						ElementTypeEnum.TRIPLETS.equals(type)||
 						ElementTypeEnum.QUESTION.equals(type)){                	
					allElements.add(element);
				}
			     
				
			}
		}
    	    	 	
		return allElements;		      
    }
    public List<FlexibleElementDTO> getAllElements(){
    	List<FlexibleElementDTO> allElements = new ArrayList<FlexibleElementDTO>();
    	List<FlexibleElementDTO> bannerElements = new ArrayList<FlexibleElementDTO>();
    	
    	//banner
		if(this.getProjectBannerDTO().getLayoutDTO()!=null){
			for(LayoutGroupDTO lg : getProjectBannerDTO().getLayoutDTO().getLayoutGroupsDTO()){
				for(LayoutConstraintDTO lc : lg.getLayoutConstraintsDTO()){
					FlexibleElementDTO f = lc.getFlexibleElementDTO();
					f.setBannerConstraint(lc);
					bannerElements.add(f);			
				}
			}
		}
		
    	//phases
		for(PhaseModelDTO phaseDTO : getPhaseModelsDTO()){
			for(LayoutGroupDTO lg : phaseDTO.getLayoutDTO().getLayoutGroupsDTO()){
				for(LayoutConstraintDTO lc : lg.getLayoutConstraintsDTO()){
					FlexibleElementDTO f = lc.getFlexibleElementDTO();
					f.setGroup(lg);
					f.setConstraint(lc);
					f.setContainerModel(phaseDTO);
					for(FlexibleElementDTO bf : bannerElements){
						if(f.getId()== bf.getId()){
							f.setBannerConstraint(bf.getBannerConstraint());
						}
					}				
					allElements.add(f);
				}
			}
		}
		//Project Details
		ProjectDetailsDTO p = getProjectDetailsDTO();
		p.setName();
		setProjectDetailsDTO(p);
		if(getProjectDetailsDTO().getLayoutDTO()!=null){
			for(LayoutGroupDTO lg : getProjectDetailsDTO().getLayoutDTO().getLayoutGroupsDTO()){
				for(LayoutConstraintDTO lc : lg.getLayoutConstraintsDTO()){
					FlexibleElementDTO f = lc.getFlexibleElementDTO();
					f.setGroup(lg);
					f.setConstraint(lc);
					f.setContainerModel(getProjectDetailsDTO());
					for(FlexibleElementDTO bf : bannerElements){
						if(f.getId()== bf.getId()){
							f.setBannerConstraint(bf.getBannerConstraint());
						}
					}
					allElements.add(f);					
				}
			}
		}
		
		
		return allElements;
    }
    /**
     * Gets all the flexible elements instances of the given class in this model
     * (phases and details page). The banner is ignored cause the elements in it
     * are read-only.
     * 
     * @param clazz
     *            The class of the searched flexible elements.
     * @return The elements localized for the given class, or <code>null</code>
     *         if there is no element of this class.
     */
    public List<LocalizedElement> getLocalizedElements(Class<? extends FlexibleElementDTO> clazz) {

        if (localizedElements == null) {

            localizedElements = new HashMap<Class<? extends FlexibleElementDTO>, List<LocalizedElement>>();

            // Details.
            for (final LayoutGroupDTO group : getProjectDetailsDTO().getLayoutDTO().getLayoutGroupsDTO()) {

                // For each constraint.
                for (final LayoutConstraintDTO constraint : group.getLayoutConstraintsDTO()) {

                    // Gets the element and its class.
                    final FlexibleElementDTO element = constraint.getFlexibleElementDTO();
                    List<LocalizedElement> elements = localizedElements.get(element.getClass());

                    // First element for this class.
                    if (elements == null) {
                        elements = new ArrayList<LocalizedElement>();
                        localizedElements.put(element.getClass(), elements);
                    }

                    // Maps the element.
                    elements.add(new LocalizedElement(null, element));
                }
            }

            // For each phase.
            for (final PhaseModelDTO phaseModel : getPhaseModelsDTO()) {
                // For each group.
                for (final LayoutGroupDTO group : phaseModel.getLayoutDTO().getLayoutGroupsDTO()) {
                    // For each constraint.
                    for (final LayoutConstraintDTO constraint : group.getLayoutConstraintsDTO()) {

                        // Gets the element and its class.
                        final FlexibleElementDTO element = constraint.getFlexibleElementDTO();
                        List<LocalizedElement> elements = localizedElements.get(element.getClass());

                        // First element for this class.
                        if (elements == null) {
                            elements = new ArrayList<LocalizedElement>();
                            localizedElements.put(element.getClass(), elements);
                        }

                        // Maps the element.
                        elements.add(new LocalizedElement(phaseModel, element));
                    }
                }
            }
        }

        return localizedElements.get(clazz);
    }
}
