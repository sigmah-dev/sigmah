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

import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class LayoutGroupJS extends JavaScriptObject {
	
	protected LayoutGroupJS() {
	}

	public static LayoutGroupJS toJavaScript(LayoutGroupDTO layoutGroupDTO) {
		final LayoutGroupJS layoutGroupJS = (LayoutGroupJS)JavaScriptObject.createObject();
		
		layoutGroupJS.setId(layoutGroupDTO.getId());
		layoutGroupJS.setTitle(layoutGroupDTO.getTitle());
		layoutGroupJS.setRow(layoutGroupDTO.getRow());
		layoutGroupJS.setColumn(layoutGroupDTO.getColumn());
		layoutGroupJS.setHasIterations(layoutGroupDTO.getHasIterations());
		layoutGroupJS.setIterationType(layoutGroupDTO.getIterationType());
		layoutGroupJS.setParentLayout(layoutGroupDTO.getParentLayout());
		layoutGroupJS.setConstraints(layoutGroupDTO.getConstraints());
		
		return layoutGroupJS;
	}
	
	public LayoutGroupDTO toDTO() {
		final LayoutGroupDTO layoutGroupDTO = new LayoutGroupDTO();
		
		layoutGroupDTO.setId(getId());
		layoutGroupDTO.setTitle(getTitle());
		layoutGroupDTO.setRow(getRow());
		layoutGroupDTO.setColumn(getColumn());
		layoutGroupDTO.setHasIterations(getHasIterations());
		layoutGroupDTO.setIterationType(getIterationType());
		
		final JsArray<LayoutConstraintJS> layoutConstraints = getConstraints();
		if(layoutConstraints != null) {
			final ArrayList<LayoutConstraintDTO> list = new ArrayList<LayoutConstraintDTO>();
			
			for(int index = 0; index < layoutConstraints.length(); index++) {
				list.add(layoutConstraints.get(index).toDTO());
			}
			
			layoutGroupDTO.setConstraints(list);
		}
		
		return layoutGroupDTO;
	}
	
	public native boolean getHasIterations() /*-{
		return this.hasIterations;
	}-*/;
	
	public native void setHasIterations(boolean hasIterations) /*-{
		this.hasIterations = hasIterations;
	}-*/;

	public native String getIterationType() /*-{
		return this.iterationType;
	}-*/;

	public native void setIterationType(String iterationType) /*-{
		this.iterationType = iterationType;
	}-*/;
	
	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;

	public native String getTitle() /*-{
		return this.title;
	}-*/;

	public native void setTitle(String title) /*-{
		this.title = title;
	}-*/;

	public native int getRow() /*-{
		return this.row;
	}-*/;

	public native void setRow(int row) /*-{
		this.row = row;
	}-*/;

	public native int getColumn() /*-{
		return this.column;
	}-*/;

	public native void setColumn(int column) /*-{
		this.column = column;
	}-*/;

	public native int getParentLayout() /*-{
		return this.parentLayout;
	}-*/;

	public void setParentLayout(LayoutDTO parentLayout) {
		if(parentLayout != null) {
			setParentLayout(parentLayout.getId());
		}
	}
	
	public native void setParentLayout(int parentLayout) /*-{
		this.parentLayout = parentLayout;
	}-*/;

	public native JsArray<LayoutConstraintJS> getConstraints() /*-{
		return this.constraints;
	}-*/;

	public void setConstraints(List<LayoutConstraintDTO> constraints) {
		if(constraints != null) {
			final JsArray<LayoutConstraintJS> array = (JsArray<LayoutConstraintJS>)JavaScriptObject.createArray();
			
			for(final LayoutConstraintDTO layoutConstraintDTO : constraints) {
				array.push(LayoutConstraintJS.toJavaScript(layoutConstraintDTO));
			}
			
			setConstraints(array);
		}
	}
	
	public native void setConstraints(JsArray<LayoutConstraintJS> constraints) /*-{
		this.constraints = constraints;
	}-*/;
}
