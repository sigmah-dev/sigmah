package org.sigmah.offline.js;

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
