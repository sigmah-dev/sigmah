package org.sigmah.shared.dto;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class OrgUnitModelDTO extends BaseModelData implements EntityDTO {

    private static final long serialVersionUID = -6438355456637422931L;

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

    // Site
    public Boolean getHasSite() {
        return (Boolean) get("hasSite");
    }

    public void setHasSite(Boolean hasSite) {
        set("hasSite", hasSite);
    }

    // Min level
    public Integer getMinLevel() {
        return (Integer) get("minLevel");
    }

    public void setMinLevel(Integer minLevel) {
        set("minLevel", minLevel);
    }

    // Max level
    public Integer getMaxLevel() {
        return (Integer) get("maxLevel");
    }

    public void setMaxLevel(Integer maxLevel) {
        set("maxLevel", maxLevel);
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
}
