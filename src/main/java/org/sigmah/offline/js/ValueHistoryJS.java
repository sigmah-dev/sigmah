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
