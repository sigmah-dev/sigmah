package org.sigmah.offline.js;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.shared.dto.history.HistoryTokenDTO;
import org.sigmah.shared.dto.history.HistoryTokenListDTO;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsDate;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class HistoryTokenListJS extends JavaScriptObject {
	
	protected HistoryTokenListJS() {
	}
	
	public static HistoryTokenListJS toJavaScript(HistoryTokenListDTO historyTokenListDTO) {
		final HistoryTokenListJS historyTokenListJS = Values.createJavaScriptObject(HistoryTokenListJS.class);
		
		historyTokenListJS.setDate(Values.toJsDate(historyTokenListDTO.getDate()));
		historyTokenListJS.setUserEmail(historyTokenListDTO.getUserEmail());
		historyTokenListJS.setUserFirstName(historyTokenListDTO.getUserFirstName());
		historyTokenListJS.setUserName(historyTokenListDTO.getUserName());
		historyTokenListJS.setTokens(historyTokenListDTO.getTokens());
		
		return historyTokenListJS;
	}
	
	public HistoryTokenListDTO toDTO() {
		final HistoryTokenListDTO historyTokenListDTO = new HistoryTokenListDTO();
		
		historyTokenListDTO.setDate(Values.toDate(getDate()));
		historyTokenListDTO.setUserEmail(getUserEmail());
		historyTokenListDTO.setUserFirstName(getUserFirstName());
		historyTokenListDTO.setUserName(getUserName());
		historyTokenListDTO.setTokens(getTokenList());
		
		return historyTokenListDTO;
	}

	public native JsDate getDate() /*-{
		return this.date;
	}-*/;

	public native void setDate(JsDate date) /*-{
		this.date = date;
	}-*/;

	public native String getUserEmail() /*-{
		return this.email;
	}-*/;

	public native void setUserEmail(String email) /*-{
		this.email = email;
	}-*/;

	public native String getUserFirstName() /*-{
		return this.firstName;
	}-*/;

	public native void setUserFirstName(String firstName) /*-{
		this.firstName = firstName;
	}-*/;

	public native String getUserName() /*-{
		return this.name;
	}-*/;

	public native void setUserName(String name) /*-{
		this.name = name;
	}-*/;

	public native JsArray<HistoryTokenJS> getTokens() /*-{
		return this.tokens;
	}-*/;
	
	public List<HistoryTokenDTO> getTokenList() {
		final List<HistoryTokenDTO> list;
		
		final JsArray<HistoryTokenJS> tokens = getTokens();
		if(tokens != null) {
			list = new ArrayList<HistoryTokenDTO>();
			
			final int size = tokens.length();
			for(int index = 0; index < size; index++) {
				list.add(tokens.get(index).toDTO());
			}
			
		} else {
			list = null;
		}
		
		return list;
	}

	public void setTokens(List<HistoryTokenDTO> tokens) {
		if(tokens != null) {
			final JsArray<HistoryTokenJS> array = (JsArray<HistoryTokenJS>) JavaScriptObject.createArray();
			
			for(final HistoryTokenDTO token : tokens) {
				array.push(HistoryTokenJS.toJavaScript(token));
			}
			
			setTokens(array);
		}
	}
	
	public native void setTokens(JsArray<HistoryTokenJS> tokens) /*-{
		this.tokens = tokens;
	}-*/;
	
}
