package org.sigmah.offline.js;

import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class LayoutConstraintJS extends JavaScriptObject {
	
	protected LayoutConstraintJS() {
	}
	
	public static LayoutConstraintJS toJavaScript(LayoutConstraintDTO layoutConstraintDTO) {
		final LayoutConstraintJS layoutConstraintJS = (LayoutConstraintJS)JavaScriptObject.createObject();
		
		layoutConstraintJS.setId(layoutConstraintDTO.getId());
		layoutConstraintJS.setSortOrder(layoutConstraintDTO.getSortOrder());
		layoutConstraintJS.setFlexibleElement(layoutConstraintDTO.getFlexibleElementDTO());
		
		return layoutConstraintJS;
	}
	
	public LayoutConstraintDTO toDTO() {
		final LayoutConstraintDTO layoutConstraintDTO = new LayoutConstraintDTO();
		
		layoutConstraintDTO.setId(getId());
		layoutConstraintDTO.setSortOrder(getSortOrder());
		layoutConstraintDTO.setFlexibleElementDTO(getFlexibleElement().toDTO());
		
		return layoutConstraintDTO;
	}

	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;

	public native int getSortOrder() /*-{
		return this.sortOrder;
	}-*/;

	public native void setSortOrder(int sortOrder) /*-{
		this.sortOrder = sortOrder;
	}-*/;

	public native FlexibleElementJS getFlexibleElement() /*-{
		return this.flexibleElement;
	}-*/;

	public void setFlexibleElement(FlexibleElementDTO flexibleElement) {
		if(flexibleElement != null) {
			setFlexibleElement(FlexibleElementJS.toJavaScript(flexibleElement));
		}
	}
	public native void setFlexibleElement(FlexibleElementJS flexibleElement) /*-{
		this.flexibleElement = flexibleElement;
	}-*/;
}
