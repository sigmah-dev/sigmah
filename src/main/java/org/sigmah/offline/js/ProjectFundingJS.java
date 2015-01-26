package org.sigmah.offline.js;

import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectFundingDTO;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class ProjectFundingJS extends JavaScriptObject {
	
	protected ProjectFundingJS() {
	}
	
	public static ProjectFundingJS toJavaScript(ProjectFundingDTO projectFundingDTO) {
		final ProjectFundingJS projectFundingJS = Values.createJavaScriptObject(ProjectFundingJS.class);
		
		projectFundingJS.setId(projectFundingDTO.getId());
		projectFundingJS.setFunding(projectFundingDTO.getFunding());
		projectFundingJS.setFunded(projectFundingDTO.getFunded());
		projectFundingJS.setPercentage(projectFundingDTO.getPercentage());
		
		return projectFundingJS;
	}
	
	public ProjectFundingDTO toDTO() {
		final ProjectFundingDTO projectFundingDTO = new ProjectFundingDTO();
		
		projectFundingDTO.setId(getId());
		if(hasPercentage()) {
			projectFundingDTO.setPercentage(getPercentage());
		}
		
		return projectFundingDTO;
	}

	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;

	public native int getFunding() /*-{
		return this.funding;
	}-*/;

	public void setFunding(ProjectDTO funding) {
		if(funding != null) {
			setFunding(funding.getId());
		}
	}
	
	public native void setFunding(int funding) /*-{
		this.funding = funding;
	}-*/;

	public native int getFunded() /*-{
		return this.funded;
	}-*/;
	
	public void setFunded(ProjectDTO funded) {
		if(funded != null) {
			setFunded(funded.getId());
		}
	}
	
	public native void setFunded(int funded) /*-{
		this.funded = funded;
	}-*/;

	public native boolean hasPercentage() /*-{
		return typeof this.percentage != 'undefined';
	}-*/;
	
	public native double getPercentage() /*-{
		return this.percentage;
	}-*/;

	public void setPercentage(Double percentage) {
		if(percentage != null) {
			setPercentage(percentage.doubleValue());
		}
	}
	
	public native void setPercentage(double percentage) /*-{
		this.percentage = percentage;
	}-*/;
}
