package org.sigmah.shared.dto.logframe;

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
import java.util.Collection;
import java.util.List;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.referential.LogFrameGroupType;

/**
 * DTO mapping class for entity logframe.LogFrame.
 * 
 * @author tmi (v1.3)
 * @author HUZHE(zhe.hu32@gmail.com) (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class LogFrameDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -2994539648384496954L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "logframe.LogFrame";

	// Map keys.
	public static final String MAIN_OBJECTIVE = "mainObjective";
	public static final String GROUPS = "groups";
	public static final String PREREQUISITES = "prerequisites";
	public static final String SPECIFIC_OBJECTIVES = "specificObjectives";
	public static final String MODEL = "model";

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public LogFrameDTO() {
		setSpecificObjectives(new ArrayList<SpecificObjectiveDTO>());
		setPrerequisites(new ArrayList<PrerequisiteDTO>());
		setGroups(new ArrayList<LogFrameGroupDTO>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(MAIN_OBJECTIVE, getMainObjective());
		builder.append(GROUPS, getGroups());
		builder.append(PREREQUISITES, getPrerequisites());
		builder.append(SPECIFIC_OBJECTIVES, getSpecificObjectives());
		builder.append(MODEL, getLogFrameModel());
	}

	/**
	 * Gets all the groups of the given type. If the type is <code>null</code>, all groups will be returned.
	 * 
	 * @param type
	 *          The type.
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
	 * Get all groups that are not deleted of a given type.If the type is <code>null</code>, all groups will be returned.
	 * 
	 * @param type
	 *          The LogFrameGroupType value.
	 * @return A collection of LogFrameGroupDTO.
	 */
	public Collection<LogFrameGroupDTO> getAllGroupsNotDeleted(LogFrameGroupType type) {

		return getAllGroups(type);
	}

	/**
	 * Gets the only default group of this type. The default group is never <code>null</code>. An empty project log frame
	 * contains always one group of each type.
	 * 
	 * @param type
	 *          The type.
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
	 *          The group label.
	 * @param type
	 *          The group type.
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
	 *          The objective to remove.
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
			max = prerequisite.getCode() > max ? prerequisite.getCode() : max;
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
	 *          The prerequisite to remove.
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

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	// Log frame model.
	public LogFrameModelDTO getLogFrameModel() {
		return get(MODEL);
	}

	public void setLogFrameModel(LogFrameModelDTO model) {
		set(MODEL, model);
	}

	// Log main objective.
	public String getMainObjective() {
		return get(MAIN_OBJECTIVE);
	}

	public void setMainObjective(String mainObjective) {
		set(MAIN_OBJECTIVE, mainObjective);
	}

	// Log frame specific objectives.
	public List<SpecificObjectiveDTO> getSpecificObjectives() {
		return get(SPECIFIC_OBJECTIVES);
	}

	public void setSpecificObjectives(List<SpecificObjectiveDTO> specificObjectivesDTO) {
		set(SPECIFIC_OBJECTIVES, specificObjectivesDTO);
	}

	// Log frame prerequisites.
	public List<PrerequisiteDTO> getPrerequisites() {
		return get(PREREQUISITES);
	}

	public void setPrerequisites(List<PrerequisiteDTO> prerequisitesDTO) {
		set(PREREQUISITES, prerequisitesDTO);
	}

	// Log frame group.
	public List<LogFrameGroupDTO> getGroups() {
		return get(GROUPS);
	}

	public void setGroups(List<LogFrameGroupDTO> groupsDTO) {
		set(GROUPS, groupsDTO);
	}

}
