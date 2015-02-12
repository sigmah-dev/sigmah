package org.sigmah.offline.js;

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
