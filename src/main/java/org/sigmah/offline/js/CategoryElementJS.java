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

import org.sigmah.shared.dto.category.CategoryElementDTO;
import org.sigmah.shared.dto.category.CategoryTypeDTO;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class CategoryElementJS extends JavaScriptObject {
	
	protected CategoryElementJS() {
	}
	
	public static CategoryElementJS toJavaScript(CategoryElementDTO categoryElementDTO) {
		final CategoryElementJS categoryElementJS = Values.createJavaScriptObject(CategoryElementJS.class);
		
		categoryElementJS.setId(categoryElementDTO.getId());
		categoryElementJS.setLabel(categoryElementDTO.getLabel());
		categoryElementJS.setParentCategory(categoryElementDTO.getParentCategoryDTO());
		categoryElementJS.setColor(categoryElementDTO.getColor());
		categoryElementJS.setIconHtml(categoryElementDTO.getIconHtml());
		
		return categoryElementJS;
	}
	
	public CategoryElementDTO toDTO() {
		final CategoryElementDTO categoryElementDTO = new CategoryElementDTO();
		
		categoryElementDTO.setId(getId());
		categoryElementDTO.setLabel(getLabel());
		categoryElementDTO.setColor(getColor());
		categoryElementDTO.setIconHtml(getIconHtml());
		
		return categoryElementDTO;
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

	public native int getParentCategory() /*-{
		return this.parentCategory;
	}-*/;

	public void setParentCategory(CategoryTypeDTO parentCategory) {
		if(parentCategory != null) {
			setParentCategory(parentCategory.getId());
		}
	}
	
	public native void setParentCategory(int parentCategory) /*-{
		this.parentCategory = parentCategory;
	}-*/;

	public native String getColor() /*-{
		return this.color;
	}-*/;

	public native void setColor(String color) /*-{
		this.color = color;
	}-*/;

	public native String getIconHtml() /*-{
		return this.iconHtml;
	}-*/;

	public native void setIconHtml(String iconHtml) /*-{
		this.iconHtml = iconHtml;
	}-*/;
}
