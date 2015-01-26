package org.sigmah.offline.js;

import org.sigmah.shared.dto.OrgUnitDetailsDTO;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class OrgUnitDetailsJS extends JavaScriptObject {
	
	protected OrgUnitDetailsJS() {
	}
	
	public static OrgUnitDetailsJS toJavaScript(OrgUnitDetailsDTO orgUnitDetailsDTO) {
		final OrgUnitDetailsJS orgUnitDetailsJS = Values.createJavaScriptObject(OrgUnitDetailsJS.class);
		
		orgUnitDetailsJS.setId(orgUnitDetailsDTO.getId());
		orgUnitDetailsJS.setLayout(orgUnitDetailsDTO.getLayout());
		orgUnitDetailsJS.setOrgUnitModel(orgUnitDetailsDTO.getOrgUnitModel());
		
		return orgUnitDetailsJS;
	}
	
	public OrgUnitDetailsDTO toDTO() {
		final OrgUnitDetailsDTO orgUnitDetailsDTO = new OrgUnitDetailsDTO();
		
		orgUnitDetailsDTO.setId(getId());
		if(getLayout() != null) {
			orgUnitDetailsDTO.setLayout(getLayout().toDTO());
		}
		
		return orgUnitDetailsDTO;
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
