package org.sigmah.offline.js;

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
