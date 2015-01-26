package org.sigmah.offline.js;

import org.sigmah.shared.dto.history.HistoryTokenDTO;
import org.sigmah.shared.dto.referential.ValueEventChangeType;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class HistoryTokenJS extends JavaScriptObject {
	
	protected HistoryTokenJS() {
	}
	
	public static HistoryTokenJS toJavaScript(HistoryTokenDTO historyTokenDTO) {
		final HistoryTokenJS historyTokenJS = Values.createJavaScriptObject(HistoryTokenJS.class);
		
		historyTokenJS.setType(historyTokenDTO.getType());
		historyTokenJS.setValue(historyTokenDTO.getValue());
		
		return historyTokenJS;
	}
	
	public HistoryTokenDTO toDTO() {
		final HistoryTokenDTO historyTokenDTO = new HistoryTokenDTO();
		
		historyTokenDTO.setType(getTypeEnum());
		historyTokenDTO.setValue(getValue());
		
		return historyTokenDTO;
	}

	public native String getValue() /*-{
		return this.value;
	}-*/;

	public native void setValue(String value) /*-{
		this.value = value;
	}-*/;

	public native String getType() /*-{
		return this.type;
	}-*/;
	
	public ValueEventChangeType getTypeEnum() {
		if(getType() != null) {
			return ValueEventChangeType.valueOf(getType());
		}
		return null;
	}

	public void setType(ValueEventChangeType type) {
		if(type != null) {
			setType(type.name());
		}
	}
	
	public native void setType(String type) /*-{
		this.type = type;
	}-*/;
	
}
