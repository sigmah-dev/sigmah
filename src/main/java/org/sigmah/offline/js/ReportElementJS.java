package org.sigmah.offline.js;

import org.sigmah.shared.dto.element.ReportElementDTO;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class ReportElementJS extends FlexibleElementJS {

	protected ReportElementJS() {
	}
	
	public static ReportElementJS toJavaScript(ReportElementDTO reportElementDTO) {
		final ReportElementJS reportElementJS = Values.createJavaScriptObject(ReportElementJS.class);
		
		reportElementJS.setModelId(reportElementDTO.getModelId());
		
		return reportElementJS;
	}
	
	@Override
	protected ReportElementDTO createDTO() {
		final ReportElementDTO reportElementDTO = new ReportElementDTO();
		reportElementDTO.setModelId(getModelIdInteger());
		return reportElementDTO;
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
