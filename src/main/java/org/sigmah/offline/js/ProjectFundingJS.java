package org.sigmah.offline.js;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
