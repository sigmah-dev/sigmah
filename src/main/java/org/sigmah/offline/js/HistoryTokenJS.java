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
		historyTokenJS.setComment(historyTokenDTO.getComment());
		
		return historyTokenJS;
	}
	
	public HistoryTokenDTO toDTO() {
		final HistoryTokenDTO historyTokenDTO = new HistoryTokenDTO();
		
		historyTokenDTO.setType(getTypeEnum());
		historyTokenDTO.setValue(getValue());
		historyTokenDTO.setComment(getComment());
		
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
	
	public native String getComment() /*-{
		return this.comment;
	}-*/;

	public native void setComment(String comment) /*-{
		this.comment = comment;
	}-*/;
	
}
