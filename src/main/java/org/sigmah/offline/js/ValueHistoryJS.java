package org.sigmah.offline.js;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.shared.command.GetHistory;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.history.HistoryTokenListDTO;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class ValueHistoryJS extends JavaScriptObject {
	
	protected ValueHistoryJS() {
	}
	
	public static ValueHistoryJS toJavaScript(GetHistory getHistory, ListResult<HistoryTokenListDTO> historyResult) {
		final ValueHistoryJS valueHistoryJS = Values.createJavaScriptObject(ValueHistoryJS.class);
		
		valueHistoryJS.setId(toIdentifier(getHistory));
		valueHistoryJS.setElementId(getHistory.getElementId());
		valueHistoryJS.setProjectId(getHistory.getProjectId());
		valueHistoryJS.setTokens(historyResult.getList());
		
		return valueHistoryJS;
	}

	public static String toIdentifier(GetHistory getHistory) {
		return toIdentifier(getHistory.getProjectId(), getHistory.getElementId());
	}
	
	public static String toIdentifier(int projectId, int elementId) {
		final StringBuilder stringBuilder = new StringBuilder();
		return stringBuilder.append(projectId)
				.append('-')
				.append(elementId)
				.toString();
	}
	
	public ListResult<HistoryTokenListDTO> toHistoryResult() {
		final ListResult<HistoryTokenListDTO> historyResult = new ListResult<HistoryTokenListDTO>();
		
		historyResult.setList(getTokenList());
		
		return historyResult;
	}
	
	public native String getId() /*-{
		return this.id;
	}-*/;

	public native void setId(String id) /*-{
		this.id = id;
	}-*/;

	public native int getElementId() /*-{
		return this.elementId;
	}-*/;

	public native void setElementId(int elementId) /*-{
		this.elementId = elementId;
	}-*/;

	public native int getProjectId() /*-{
		return this.projectId;
	}-*/;

	public native void setProjectId(int projectId) /*-{
		this.projectId = projectId;
	}-*/;
	
	public native JsArray<HistoryTokenListJS> getTokens() /*-{
		return this.tokens;
	}-*/;
	
	public List<HistoryTokenListDTO> getTokenList() {
		final List<HistoryTokenListDTO> list;
		
		final JsArray<HistoryTokenListJS> tokens = getTokens();
		if(tokens != null) {
			list = new ArrayList<HistoryTokenListDTO>();
			
			final int size = tokens.length();
			for(int index = 0; index < size; index++) {
				list.add(tokens.get(index).toDTO());
			}
			
		} else {
			list = null;
		}
		
		return list;
	}

	public void setTokens(List<HistoryTokenListDTO> tokens) {
		if(tokens != null) {
			final JsArray<HistoryTokenListJS> array = (JsArray<HistoryTokenListJS>) JavaScriptObject.createArray();
			
			for(final HistoryTokenListDTO token : tokens) {
				array.push(HistoryTokenListJS.toJavaScript(token));
			}
			
			setTokens(array);
		}
	}
	
	public native void setTokens(JsArray<HistoryTokenListJS> tokens) /*-{
		this.tokens = tokens;
	}-*/;
}
