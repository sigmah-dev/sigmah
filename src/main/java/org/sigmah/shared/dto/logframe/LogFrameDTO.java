package org.sigmah.shared.dto.logframe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.sigmah.shared.domain.logframe.LogFrameGroupType;
import org.sigmah.shared.dto.EntityDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * DTO mapping class for entity logframe.LogFrame.
 * 
 * @author tmi
 * 
 */
public class LogFrameDTO extends BaseModelData implements EntityDTO {

	private static final long serialVersionUID = -2994539648384496954L;

	public LogFrameDTO() {
		setSpecificObjectives(new ArrayList<SpecificObjectiveDTO>());
		setPrerequisites(new ArrayList<PrerequisiteDTO>());
		setGroups(new ArrayList<LogFrameGroupDTO>());
	}
	
	
	@Override
	public String getEntityName() {
		return "logframe.LogFrame";
	}

	// Log frame id.
	@Override
	public int getId() {
		final Integer id = (Integer) get("id");
		return id != null ? id : -1;		
	}

	public void setId(int id) {
		set("id", id);
	}

	// Log frame model.
	public LogFrameModelDTO getLogFrameModel() {
		return get("model");
	}

	public void setLogFrameModel(LogFrameModelDTO model) {
		set("model", model);
	}

	// Log main objective.
	public String getMainObjective() {
		return get("mainObjective");
	}

	public void setMainObjective(String mainObjective) {
		set("mainObjective", mainObjective);
	}

	// Log frame specific objectives.
	public List<SpecificObjectiveDTO> getSpecificObjectives() {
		return get("specificObjectives");
	}		

	public void setSpecificObjectives(
			List<SpecificObjectiveDTO> specificObjectivesDTO) {
		set("specificObjectives", specificObjectivesDTO);
	}

	// Log frame prerequisites.
	public List<PrerequisiteDTO> getPrerequisites() {
		return get("prerequisites");
	}

	public void setPrerequisites(List<PrerequisiteDTO> prerequisitesDTO) {
		set("prerequisites", prerequisitesDTO);
	}


	// Log frame group.
	public List<LogFrameGroupDTO> getGroups() {
		return get("groups");
	}

	public void setGroups(List<LogFrameGroupDTO> groupsDTO) {
		set("groups", groupsDTO);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("LogFrameDTO [");
		sb.append("entity name = ");
		sb.append(getEntityName());
		sb.append(" ; id = ");
		sb.append(getId());
		sb.append(" ; main objective = ");
		sb.append(getMainObjective());
		sb.append(" ; groups = (\n");
		if (getGroups() != null) {
			for (final LogFrameGroupDTO g : getGroups()) {
				sb.append(g);
				sb.append("\n");
			}
		}
		sb.append(" ; prerequisites = (\n");
		if (getPrerequisites() != null) {
			for (final PrerequisiteDTO p : getPrerequisites()) {
				sb.append(p);
				sb.append("\n");
			}
		}
		sb.append(")\n");
		sb.append("specific objectives = (\n");
		if (getSpecificObjectives() != null) {
			for (final SpecificObjectiveDTO o : getSpecificObjectives()) {
				sb.append(o);
				sb.append("\n");
			}
		}
		sb.append(")\n");
		sb.append(" ; model = (\n");
		sb.append(getLogFrameModel());
		sb.append(")]\n");

		return sb.toString();
	}

	/**
	 * Gets all the groups of the given type. If the type is <code>null</code>,
	 * all groups will be returned.
	 * 
	 * @param type
	 *            The type.
	 * @return The groups with the given type.
	 */
	public Collection<LogFrameGroupDTO> getAllGroups(LogFrameGroupType type) {

		// Lists of groups.
		final List<LogFrameGroupDTO> returnedGroups = new ArrayList<LogFrameGroupDTO>();
		final List<LogFrameGroupDTO> groups = getGroups();

		// Retrieves groups.
		if (groups != null) {
			for (final LogFrameGroupDTO g : groups) {

				// Adds the group if it has the correct type.
				if (type == null || type == g.getType()) {

					returnedGroups.add(g);

				}
			}
		}

		return returnedGroups;
	}

	/**
	 * Get all groups that are not deleted of a given type.If the type is <code>null</code>,
	 * all groups will be returned.
	 * 
	 * @param 
	 *      The Type
	 * @return
	 *      A Collection<LogFrameGroupDTO>    
	 * @author 
	 *      HUZHE(zhe.hu32@gmail.com)
	 */
	public Collection<LogFrameGroupDTO> getAllGroupsNotDeleted(LogFrameGroupType type) {

		return (List<LogFrameGroupDTO>) getAllGroups(type);
	}

	
	/**
	 * Gets the only default group of this type. The default group is never
	 * <code>null</code>. An empty project log frame contains always one group
	 * of each type.
	 * 
	 * @param type
	 *            The type.
	 * @return The only default group for the given type.
	 */
	public LogFrameGroupDTO getDefaultGroup(LogFrameGroupType type) {

		LogFrameGroupDTO group = null;

		// Lists of groups.
		final List<LogFrameGroupDTO> groups = getGroups();

		// Retrieves group.
		if (groups != null) {
			for (final LogFrameGroupDTO g : groups) {

				// Stops at the first group with the given type.
				if (type == null || type == g.getType()) {
					group = g;
					break;
				}
			}
		}

		return group;
	}

	/**
	 * Creates and adds a new group.
	 * 
	 * @param label
	 *            The group label.
	 * @param type
	 *            The group type.
	 * @return The created group.
	 */
	public LogFrameGroupDTO addGroup(String label, LogFrameGroupType type) {

		// Creates the groups.
		final LogFrameGroupDTO group = new LogFrameGroupDTO();
		group.setLabel(label);
		group.setType(type);
		group.setParentLogFrame(this);

		// Adds it.
		getGroups().add(group);
		
		return group;
	}

	/**
	 * Remove a group from the log frame
	 * 
	 * @param group
	 * @return if successful,return true
	 * 
	 * @author HUZHE
	 */
	public boolean removeGroup(LogFrameGroupDTO group) {
		return getGroups().remove(group);
	}

	/**
	 * Adds a new specific objective to this log frame.
	 * 
	 * @return The new specific objective.
	 */
	public SpecificObjectiveDTO addSpecificObjective() {

		List<SpecificObjectiveDTO> specificObjectives = getSpecificObjectives();

		// Retrieves the higher code.
		int max = 0;
		for (final SpecificObjectiveDTO objective : specificObjectives) {
			max = objective.getCode() > max ? objective.getCode() : max;
		}

		// Creates the new objective.
		final SpecificObjectiveDTO newObjective = new SpecificObjectiveDTO();
		newObjective.setCode(max + 1);
		newObjective.setParentLogFrame(this);

		// Adds it to the local list.
		specificObjectives.add(newObjective);

		return newObjective;
	}

	/**
	 * Removes a specific objective from this log frame.
	 * 
	 * @param objective
	 *            The objective to remove.
	 * @return If the objective has been removed.
	 */
	public boolean removeSpecificObjective(SpecificObjectiveDTO objective) {
		return getSpecificObjectives().remove(objective);
	}

	/**
	 * Adds a new prerequisite to this log frame.
	 * 
	 * @return The new prerequisite.
	 */
	public PrerequisiteDTO addPrerequisite() {

		List<PrerequisiteDTO> prerequisites = getPrerequisites();

		// Retrieves the higher code.
		int max = 0;
		for (final PrerequisiteDTO prerequisite : prerequisites) {
			max = prerequisite.getCode() > max ? prerequisite.getCode()
					: max;
		}

		// Creates the new objective.
		final PrerequisiteDTO newPrerequisite = new PrerequisiteDTO();
		newPrerequisite.setCode(max + 1);
		newPrerequisite.setParentLogFrame(this);

		// Adds it to the local list.
		prerequisites.add(newPrerequisite);

		return newPrerequisite;
	}

	/**
	 * Removes a prerequisite from this log frame.
	 * 
	 * @param prerequisite
	 *            The prerequisite to remove.
	 * @return If the prerequisite has been removed.
	 */
	public boolean removePrerequisite(PrerequisiteDTO prerequisite) {
		return getPrerequisites().remove(prerequisite);
	}

	/**
	 * Returns all the expected results of this log frame.
	 * 
	 * @return All the expected results of this log frame.
	 */
	public List<ExpectedResultDTO> getAllExpectedResultsDTO() {

		final ArrayList<ExpectedResultDTO> results = new ArrayList<ExpectedResultDTO>();

		// Retrieves the expected results for each objective.
		for (final SpecificObjectiveDTO objective : getSpecificObjectives()) {
			results.addAll(objective.getExpectedResults());
		}

		return results;
	}

	/**
	 * Returns all the activities of this log frame.
	 * 
	 * @return All the activities of this log frame.
	 */
	public List<LogFrameActivityDTO> getAllActivitiesDTO() {

		final ArrayList<LogFrameActivityDTO> activities = new ArrayList<LogFrameActivityDTO>();

		// Retrieves the activities for each expected result.
		for (final ExpectedResultDTO result : getAllExpectedResultsDTO()) {
			activities.addAll(result.getActivities());
		}

		return activities;
	}
}
