package org.sigmah.offline.js;

import org.sigmah.shared.dto.OrgUnitBannerDTO;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class OrgUnitBannerJS extends JavaScriptObject {
	
	protected OrgUnitBannerJS() {
	}
	
	public static OrgUnitBannerJS toJavaScript(OrgUnitBannerDTO orgUnitBannerDTO) {
		final OrgUnitBannerJS orgUnitBannerJS = Values.createJavaScriptObject(OrgUnitBannerJS.class);
		
		orgUnitBannerJS.setId(orgUnitBannerDTO.getId());
		orgUnitBannerJS.setLayout(orgUnitBannerDTO.getLayout());
		orgUnitBannerJS.setOrgUnitModel(orgUnitBannerDTO.getOrgUnitModel());
		
		return orgUnitBannerJS;
	}
	
	public OrgUnitBannerDTO toDTO() {
		final OrgUnitBannerDTO orgUnitBannerDTO = new OrgUnitBannerDTO();
		
		orgUnitBannerDTO.setId(getId());
		if(getLayout() != null) {
			orgUnitBannerDTO.setLayout(getLayout().toDTO());
		}
		
		return orgUnitBannerDTO;
	}

	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;

	public native LayoutJS getLayout() /*-{
		return this.layout;
	}-*/;

	public void setLayout(LayoutDTO layout) {
		if(layout != null) {
			setLayout(LayoutJS.toJavaScript(layout));
		}
	}
	
	public native void setLayout(LayoutJS layout) /*-{
		this.layout = layout;
	}-*/;

	public native int getOrgUnitModel() /*-{
		return this.orgUnitModel;
	}-*/;

	public void setOrgUnitModel(OrgUnitModelDTO orgUnitModel) {
		if(orgUnitModel != null) {
			setOrgUnitModel(orgUnitModel.getId());
		}
	}
	
	public native void setOrgUnitModel(int orgUnitModel) /*-{
		this.orgUnitModel = orgUnitModel;
	}-*/;
}
