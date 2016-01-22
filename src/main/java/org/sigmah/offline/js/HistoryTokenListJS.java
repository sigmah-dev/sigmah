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
