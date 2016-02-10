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

import org.sigmah.shared.dto.element.ReportListElementDTO;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class ReportListElementJS extends FlexibleElementJS {

	protected ReportListElementJS() {
	}
	
	public static ReportListElementJS toJavaScript(ReportListElementDTO reportListElementDTO) {
		final ReportListElementJS reportListElementJS = Values.createJavaScriptObject(ReportListElementJS.class);
		
		reportListElementJS.setModelId(reportListElementDTO.getModelId());
		
		return reportListElementJS;
	}
	
	protected ReportListElementDTO toReportListElementDTO() {
		final ReportListElementDTO reportListElementDTO = new ReportListElementDTO();
		reportListElementDTO.setModelId(getModelIdInteger());
		return reportListElementDTO;
	}
	
	public native boolean hasModelId() /*-{
		return typeof this.modelId != 'undefined';
	}-*/;
	
	public native int getModelId() /*-{
		return this.modelId;
	}-*/;
	
	public Integer getModelIdInteger() {
		if(hasModelId()) {
			return getModelId();
		}
		return null;
	}
	
	public native void setModelId(int modelId) /*-{
		this.modelId = modelId;
	}-*/;
	
	public void setModelId(Integer modelId) {
		if(modelId != null) {
			setModelId(modelId.intValue());
		}
	}
}
