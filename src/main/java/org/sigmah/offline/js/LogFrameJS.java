package org.sigmah.offline.js;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.shared.dto.logframe.LogFrameDTO;
import org.sigmah.shared.dto.logframe.LogFrameGroupDTO;
import org.sigmah.shared.dto.logframe.LogFrameModelDTO;
import org.sigmah.shared.dto.logframe.PrerequisiteDTO;
import org.sigmah.shared.dto.logframe.SpecificObjectiveDTO;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class LogFrameJS extends JavaScriptObject {
	
	protected LogFrameJS() {
	}
	
	public static LogFrameJS toJavaScript(LogFrameDTO logFrameDTO) {
		final LogFrameJS logFrameJS = Values.createJavaScriptObject(LogFrameJS.class);
		
		logFrameJS.setId(logFrameDTO.getId());
		logFrameJS.setLogFrameModel(logFrameDTO.getLogFrameModel());
		logFrameJS.setMainObjective(logFrameDTO.getMainObjective());
		logFrameJS.setSpecificObjectives(logFrameDTO.getSpecificObjectives());
		logFrameJS.setPrerequisites(logFrameDTO.getPrerequisites());
		logFrameJS.setGroups(logFrameDTO.getGroups());
		
		return logFrameJS;
	}
	
	public LogFrameDTO toDTO() {
		final LogFrameDTO logFrameDTO = new LogFrameDTO();
		
		logFrameDTO.setId(getId());
		logFrameDTO.setLogFrameModel(getLogFrameModelDTO());
		logFrameDTO.setMainObjective(getMainObjective());
		
		logFrameDTO.setGroups(getGroups(logFrameDTO));
		final HashMap<Integer, LogFrameGroupDTO> groupMap = new HashMap<Integer, LogFrameGroupDTO>();
		if(logFrameDTO.getGroups() != null) {
			for(final LogFrameGroupDTO group : logFrameDTO.getGroups()) {
				groupMap.put(group.getId(), group);
			}
		}
		
		logFrameDTO.setSpecificObjectives(getSpecificObjectives(logFrameDTO, groupMap));
		logFrameDTO.setPrerequisites(getPrerequisites(logFrameDTO, groupMap));
		
		return logFrameDTO;
	}

	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;

	public native LogFrameModelJS getLogFrameModel() /*-{
		return this.logFrameModel;
	}-*/;

	public native void setLogFrameModel(LogFrameModelJS logFrameModel) /*-{
		this.logFrameModel = logFrameModel;
	}-*/;
	
	public LogFrameModelDTO getLogFrameModelDTO() {
		if(getLogFrameModel() != null) {
			return getLogFrameModel().toDTO();
		}
		return null;
	}
	
	public void setLogFrameModel(LogFrameModelDTO logFrameModel) {
		if(logFrameModel != null) {
			setLogFrameModel(LogFrameModelJS.toJavaScript(logFrameModel));
		}
	}

	public native String getMainObjective() /*-{
		return this.mainObjective;
	}-*/;

	public native void setMainObjective(String mainObjective) /*-{
		this.mainObjective = mainObjective;
	}-*/;

	public native JsArray<SpecificObjectiveJS> getSpecificObjectives() /*-{
		return this.specificObjectives;
	}-*/;

	public native void setSpecificObjectives(JsArray<SpecificObjectiveJS> specificObjectives) /*-{
		this.specificObjectives = specificObjectives;
	}-*/;
	
	public List<SpecificObjectiveDTO> getSpecificObjectives(LogFrameDTO logFrameDTO, Map<Integer, LogFrameGroupDTO> groupMap) {
		final JsArray<SpecificObjectiveJS> specificObjectives = getSpecificObjectives();
		if(specificObjectives != null) {
			final ArrayList<SpecificObjectiveDTO> list = new ArrayList<SpecificObjectiveDTO>();
			
			for(int index = 0; index < specificObjectives.length(); index++) {
				final SpecificObjectiveJS specificObjectiveJS = specificObjectives.get(index);
				final SpecificObjectiveDTO specificObjectiveDTO = specificObjectiveJS.toDTO(groupMap);
				specificObjectiveDTO.setParentLogFrame(logFrameDTO);
				if(specificObjectiveJS.hasGroup()) {
					specificObjectiveDTO.setGroup(groupMap.get(specificObjectiveJS.getGroup()));
				}
				list.add(specificObjectiveDTO);
			}
			
			return list;
		}
		return null;
	}

	public void setSpecificObjectives(List<SpecificObjectiveDTO> specificObjectives) {
		if(specificObjectives != null) {
			final JsArray<SpecificObjectiveJS> array = (JsArray<SpecificObjectiveJS>) JavaScriptObject.createArray();
			
			for(final SpecificObjectiveDTO specificObjective : specificObjectives) {
				array.push(SpecificObjectiveJS.toJavaScript(specificObjective));
			}
			
			setSpecificObjectives(array);
		}
	}

	public native JsArray<PrerequisiteJS> getPrerequisites() /*-{
		return this.prerequisites;
	}-*/;

	public native void setPrerequisites(JsArray<PrerequisiteJS> prerequisites) /*-{
		this.prerequisites = prerequisites;
	}-*/;

	public List<PrerequisiteDTO> getPrerequisites(LogFrameDTO logFrameDTO, Map<Integer, LogFrameGroupDTO> groupMap) {
		final JsArray<PrerequisiteJS> prerequisites = getPrerequisites();
		if(prerequisites != null) {
			final ArrayList<PrerequisiteDTO> list = new ArrayList<PrerequisiteDTO>();
			
			for(int index = 0; index < prerequisites.length(); index++) {
				final PrerequisiteJS prerequisiteJS = prerequisites.get(index);
				final PrerequisiteDTO prerequisiteDTO = prerequisiteJS.toDTO();
				prerequisiteDTO.setParentLogFrame(logFrameDTO);
				if(prerequisiteJS.hasGroup()) {
					prerequisiteDTO.setGroup(groupMap.get(prerequisiteJS.getGroup()));
				}
				list.add(prerequisiteDTO);
			}
			
			return list;
		}
		return null;
	}
	
	public void setPrerequisites(List<PrerequisiteDTO> prerequisites) {
		if(prerequisites != null) {
			final JsArray<PrerequisiteJS> array = (JsArray<PrerequisiteJS>) JavaScriptObject.createArray();
			
			for(final PrerequisiteDTO prerequisite : prerequisites) {
				array.push(PrerequisiteJS.toJavaScript(prerequisite));
			}
			
			setPrerequisites(array);
		}
	}

	public native JsArray<LogFrameGroupJS> getGroups() /*-{
		return this.groups;
	}-*/;

	public native void setGroups(JsArray<LogFrameGroupJS> groups) /*-{
		this.groups = groups;
	}-*/;
	
	
	public List<LogFrameGroupDTO> getGroups(LogFrameDTO logFrameDTO) {
		final JsArray<LogFrameGroupJS> groups = getGroups();
		if(groups != null) {
			final ArrayList<LogFrameGroupDTO> list = new ArrayList<LogFrameGroupDTO>();
			
			for(int index = 0; index < groups.length(); index++) {
				final LogFrameGroupDTO group = groups.get(index).toDTO();
				group.setParentLogFrame(logFrameDTO);
				list.add(group);
			}
			
			return list;
		}
		return null;
	}

	public void setGroups(List<LogFrameGroupDTO> groups) {
		if(groups != null) {
			final JsArray<LogFrameGroupJS> array = (JsArray<LogFrameGroupJS>) JavaScriptObject.createArray();
			
			for(final LogFrameGroupDTO group : groups) {
				array.push(LogFrameGroupJS.toJavaScript(group));
			}
			
			setGroups(array);
		}
	}
}
