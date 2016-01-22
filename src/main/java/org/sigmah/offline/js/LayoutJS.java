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

import org.sigmah.shared.dto.layout.LayoutDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class LayoutJS extends JavaScriptObject {
	
	protected LayoutJS() {
	}
	
	public static LayoutJS toJavaScript(LayoutDTO layoutDTO) {
		final LayoutJS layoutJS = (LayoutJS)JavaScriptObject.createObject();

		layoutJS.setId(layoutDTO.getId());
		layoutJS.setRowsCount(layoutDTO.getRowsCount());
		layoutJS.setColumnsCount(layoutDTO.getColumnsCount());
		layoutJS.setGroups(layoutDTO.getGroups());
		
		return layoutJS;
	}
	
	public LayoutDTO toDTO() {
		final LayoutDTO layoutDTO = new LayoutDTO();
		
		layoutDTO.setId(getId());
		layoutDTO.setRowsCount(getRowsCount());
		layoutDTO.setColumnsCount(getColumnsCount());
		
		final JsArray<LayoutGroupJS> layoutGroups = getGroups();
		if(layoutGroups != null) {
			final ArrayList<LayoutGroupDTO> list = new ArrayList<LayoutGroupDTO>();
			
			final int length = layoutGroups.length();
			for(int index = 0; index < length; index++) {
				list.add(layoutGroups.get(index).toDTO());
			}
			
			layoutDTO.setGroups(list);
		}
		
		return layoutDTO;
	}

	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;

	public native int getRowsCount() /*-{
		return this.rowsCount;
	}-*/;

	public native void setRowsCount(int rowsCount) /*-{
		this.rowsCount = rowsCount;
	}-*/;

	public native int getColumnsCount() /*-{
		return this.columnsCount;
	}-*/;

	public native void setColumnsCount(int columnsCount) /*-{
		this.columnsCount = columnsCount;
	}-*/;

	public native JsArray<LayoutGroupJS> getGroups() /*-{
		return this.groups;
	}-*/;

	public void setGroups(List<LayoutGroupDTO> groups) {
		if(groups != null) {
			final JsArray<LayoutGroupJS> array = (JsArray<LayoutGroupJS>) JavaScriptObject.createArray();
			
			for(final LayoutGroupDTO layoutGroupDTO : groups) {
				array.push(LayoutGroupJS.toJavaScript(layoutGroupDTO));
			}
			
			setGroups(array);
		}
	}
	
	public native void setGroups(JsArray<LayoutGroupJS> groups) /*-{
		this.groups = groups;
	}-*/;
}
