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

import org.sigmah.shared.command.UpdateProject;
import org.sigmah.shared.dto.element.event.ValueEventWrapper;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * JavaScript version of the {@link UpdateProject} command.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class UpdateProjectJS extends CommandJS {
	
	protected UpdateProjectJS() {
	}
	
	public static UpdateProjectJS toJavaScript(UpdateProject updateProject) {
		final UpdateProjectJS updateProjectJS = Values.createJavaScriptObject(UpdateProjectJS.class);
		
		updateProjectJS.setProjectId(updateProject.getProjectId());
		updateProjectJS.setValues(updateProject.getValues(), updateProject);
		
		return updateProjectJS;
	}
	
	public UpdateProject toUpdateProject() {
		final UpdateProject updateProject = new UpdateProject();
		
		updateProject.setProjectId(getProjectId());
		updateProject.setValueEventWrappers(getValuesAsList());
		
		return updateProject;
	}

	public native int getProjectId() /*-{
		return this.projectId;
	}-*/;

	public native void setProjectId(int projectId) /*-{
		this.projectId = projectId;
	}-*/;

	public native JsArray<ValueJS> getValues() /*-{
		return this.values;
	}-*/;

	public List<ValueEventWrapper> getValuesAsList() {
		if(getValues() != null) {
			final ArrayList<ValueEventWrapper> list = new ArrayList<ValueEventWrapper>();
			
			final JsArray<ValueJS> values = getValues();
			for(int index = 0; index < values.length(); index++) {
				final ValueJS valueJS = values.get(index);
				final ValueEventWrapper valueEventWrapper = valueJS.toValueEventWrapper();
				list.add(valueEventWrapper);
			}
			
			return list;
		}
		return null;
	}

	public void setValues(List<ValueEventWrapper> values, UpdateProject updateProject) {
		if(values != null) {
			final JsArray<ValueJS> array = (JsArray<ValueJS>) JavaScriptObject.createArray();
			
			for(final ValueEventWrapper value : values) {
				array.push(ValueJS.toJavaScript(updateProject, value));
			}
			
			setValues(array);
		}
	}
	
	public native void setValues(JsArray<ValueJS> values) /*-{
		this.values = values;
	}-*/;
}
