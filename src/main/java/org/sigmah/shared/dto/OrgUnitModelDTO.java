package org.sigmah.shared.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class OrgUnitModelDTO extends BaseModelData implements EntityDTO {

    private static final long serialVersionUID = -6438355456637422931L;
    
	/**
	 * Localizes an flexible element in the organizational unit model.
	 * 
	 * @author kma
	 * 
	 */
    protected static class LocalizedElement {
    	
        private final FlexibleElementDTO element;

        protected LocalizedElement(FlexibleElementDTO element) {
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
    }
    
    private transient HashMap<Class<? extends FlexibleElementDTO>, List<LocalizedElement>> localizedElements;

    @Override
    public String getEntityName() {
        return "OrgUnitModel";
    }

    // Id
    @Override
    public int getId() {
    	if(get("id") != null)
    		return (Integer) get("id");
    	else
    		return -1;
    }

    public void setId(int id) {
        set("id", id);
    }

    // Name
    public String getName() {
        return get("name");
    }

    public void setName(String name) {
        set("name", name);
    }

    // Title
    public String getTitle() {
        return get("title");
    }

    public void setTitle(String title) {
        set("title", title);
    }

    // Banner
    public OrgUnitBannerDTO getBanner() {
        return get("banner");
    }

    public void setBanner(OrgUnitBannerDTO banner) {
        set("banner", banner);
    }

    // Details
    public OrgUnitDetailsDTO getDetails() {
        return get("details");
    }

    public void setDetails(OrgUnitDetailsDTO details) {
        set("details", details);
    }

    // Budget
    public Boolean getHasBudget() {
        return (Boolean) get("hasBudget");
    }

    public void setHasBudget(Boolean hasBudget) {
        set("hasBudget", hasBudget);
    }

    // Can contain projects
    public Boolean getCanContainProjects() {
        return (Boolean) get("canContainProjects");
    }

    public void setCanContainProjects(Boolean canContainProjects) {
        set("canContainProjects", canContainProjects);
    }
    
    public ProjectModelStatus getStatus() {
        return (ProjectModelStatus) get("status");
    }

    public void setStatus(ProjectModelStatus status) {
        set("status", status);
    }
    
    public void setTopOrgUnitModel(boolean is) {
        set("topModel", is);
    }
    
    public boolean isTopOrgUnitModel() {
        final Boolean b = get("topModel");
        return b != null ? b : false;
    }
    
    public List<FlexibleElementDTO> getAllElements(){
    	List<FlexibleElementDTO> allElements = new ArrayList<FlexibleElementDTO>();
    	List<FlexibleElementDTO> bannerElements = new ArrayList<FlexibleElementDTO>();
    	
    	//banner
		if(this.getBanner().getLayout()!=null){
			for(LayoutGroupDTO lg : getBanner().getLayout().getLayoutGroupsDTO()){
				for(LayoutConstraintDTO lc : lg.getLayoutConstraintsDTO()){
					FlexibleElementDTO f = lc.getFlexibleElementDTO();
					f.setBannerConstraint(lc);
					bannerElements.add(f);			
				}
			}
		}
		
		//Details
		OrgUnitDetailsDTO d = getDetails();
		d.setName();
		setDetails(d);
		if(getDetails().getLayout()!=null){
			for(LayoutGroupDTO lg : getDetails().getLayout().getLayoutGroupsDTO()){
				for(LayoutConstraintDTO lc : lg.getLayoutConstraintsDTO()){
					FlexibleElementDTO f = lc.getFlexibleElementDTO();
					f.setGroup(lg);
					f.setConstraint(lc);
					f.setContainerModel(getDetails());
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
	 * (details page). The banner is ignored cause the elements in it are
	 * read-only.
	 * 
	 * @param clazz
	 *            The class of the searched flexible elements.
	 * @return The elements localized for the given class, or <code>null</code>
	 *         if there is no element of this class.
	 */
    public List<LocalizedElement> getLocalizedElements(Class<? extends FlexibleElementDTO> clazz) {

        if (localizedElements == null) {

            localizedElements = new HashMap<Class<? extends FlexibleElementDTO>, List<LocalizedElement>>();

            // Details
            for (final LayoutGroupDTO group : getDetails().getLayout().getLayoutGroupsDTO()) {

                // For each constraint
                for (final LayoutConstraintDTO constraint : group.getLayoutConstraintsDTO()) {

                    // Gets the element and its class
                    final FlexibleElementDTO element = constraint.getFlexibleElementDTO();
                    List<LocalizedElement> elements = localizedElements.get(element.getClass());

                    // First element for this class
                    if (elements == null) {
                        elements = new ArrayList<LocalizedElement>();
                        localizedElements.put(element.getClass(), elements);
                    }

                    // Maps the element.
                    elements.add(new LocalizedElement(element));
                }
            }            
        }
        return localizedElements.get(clazz);
    }
}
