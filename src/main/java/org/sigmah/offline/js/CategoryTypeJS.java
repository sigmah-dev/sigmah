package org.sigmah.offline.js;

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
