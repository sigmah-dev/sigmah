package org.sigmah.offline.js;

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
