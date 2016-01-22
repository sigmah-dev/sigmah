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

import java.util.List;

import org.sigmah.shared.dto.category.CategoryElementDTO;
import org.sigmah.shared.dto.category.CategoryTypeDTO;
import org.sigmah.shared.dto.referential.CategoryIcon;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayInteger;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class CategoryTypeJS extends JavaScriptObject {
	
	protected CategoryTypeJS() {
	}
	
	public static CategoryTypeJS toJavaScript(CategoryTypeDTO categoryTypeDTO) {
		final CategoryTypeJS categoryTypeJS = Values.createJavaScriptObject(CategoryTypeJS.class);
		
		categoryTypeJS.setId(categoryTypeDTO.getId());
		categoryTypeJS.setLabel(categoryTypeDTO.getLabel());
		categoryTypeJS.setCategoryElements(categoryTypeDTO.getCategoryElementsDTO());
		categoryTypeJS.setIcon(categoryTypeDTO.getIcon());
				
		return categoryTypeJS;
	}
	
	public CategoryTypeDTO toDTO() {
		final CategoryTypeDTO categoryTypeDTO = new CategoryTypeDTO();
		
		categoryTypeDTO.setId(getId());
		categoryTypeDTO.setLabel(getLabel());
		categoryTypeDTO.setIcon(getIcon());
		
		return categoryTypeDTO;
	}

	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;

	public native String getLabel() /*-{
		return this.label;
	}-*/;

	public native void setLabel(String label) /*-{
		this.label = label;
	}-*/;

	public native JsArrayInteger getCategoryElements() /*-{
		return this.categoryElements;
	}-*/;

	public void setCategoryElements(List<CategoryElementDTO> categoryElements) {
		if(categoryElements != null) {
			final JsArrayInteger array = (JsArrayInteger) JavaScriptObject.createArray();
			
			for(final CategoryElementDTO categoryElementDTO : categoryElements) {
				array.push(categoryElementDTO.getId());
			}
			
			setCategoryElements(array);
		}
	}
	
	public native void setCategoryElements(JsArrayInteger categoryElements) /*-{
		this.categoryElements = categoryElements;
	}-*/;

	public native CategoryIcon getIcon() /*-{
		return this.icon;
	}-*/;

	public native void setIcon(CategoryIcon icon) /*-{
		this.icon = icon;
	}-*/;
}
