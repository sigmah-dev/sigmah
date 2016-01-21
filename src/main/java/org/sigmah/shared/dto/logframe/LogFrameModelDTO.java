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

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * DTO mapping class for entity logframe.LogFrameModel.
 * 
 * @author tmi (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class LogFrameModelDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -7816999376877639326L;

	/**
	 * The default visibility policy for groups if the corresponding attribute is missing.
	 */
	private static final boolean DEFAULT_VISIBILITY_GROUP_POLICY = false;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "logframe.LogFrameModel";

	// DTO attributes keys.
	public static final String NAME = "name";

	public static final String ENABLE_SPECIFIC_OBJECTIVES_GROUPS = "enableSpecificObjectivesGroups";
	public static final String SPECIFIC_OBJECTIVES_MAX = "specificObjectivesMax";
	public static final String SPECIFIC_OBJECTIVES_GROUPS_MAX = "specificObjectivesGroupsMax";
	public static final String SPECIFIC_OBJECTIVES_PER_GROUP_MAX = "specificObjectivesPerGroupMax";

	public static final String ENABLE_EXPECTED_RESULTS_GROUPS = "enableExpectedResultsGroups";
	public static final String EXPECTED_RESULTS_MAX = "expectedResultsMax";
	public static final String EXPECTED_RESULTS_GROUPS_MAX = "expectedResultsGroupsMax";
	public static final String EXPECTED_RESULTS_PER_GROUP_MAX = "expectedResultsPerGroupMax";
	public static final String EXPECTED_RESULTS_PER_SPECIFIC_OBJECTIVE_MAX = "expectedResultsPerSpecificObjectiveMax";

	public static final String ENABLE_ACTIVITIES_GROUPS = "enableActivitiesGroups";
	public static final String ACTIVITIES_MAX = "activitiesMax";
	public static final String ACTIVITIES_GROUPS_MAX = "activitiesGroupsMax";
	public static final String ACTIVITIES_PER_GROUP_MAX = "activitiesPerGroupMax";
	public static final String ACTIVITIES_PER_EXPECTED_RESULT_MAX = "activitiesPerExpectedResultMax";

	public static final String ENABLE_PREREQUISITES_GROUPS = "enablePrerequisitesGroups";
	public static final String PREREQUISITES_MAX = "prerequisitesMax";
	public static final String PREREQUISITES_GROUPS_MAX = "prerequisitesGroupsMax";
	public static final String PREREQUISITES_PER_GROUP_MAX = "prerequisitesPerGroupMax";

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
		builder.append(NAME, getName());

		builder.append(ENABLE_SPECIFIC_OBJECTIVES_GROUPS, getEnableSpecificObjectivesGroups());
		builder.append(SPECIFIC_OBJECTIVES_MAX, getSpecificObjectivesMax());
		builder.append(SPECIFIC_OBJECTIVES_GROUPS_MAX, getSpecificObjectivesGroupsMax());
		builder.append(SPECIFIC_OBJECTIVES_PER_GROUP_MAX, getSpecificObjectivesPerGroupMax());

		builder.append(ENABLE_EXPECTED_RESULTS_GROUPS, getEnableExpectedResultsGroups());
		builder.append(EXPECTED_RESULTS_MAX, getExpectedResultsMax());
		builder.append(EXPECTED_RESULTS_GROUPS_MAX, getExpectedResultsGroupsMax());
		builder.append(EXPECTED_RESULTS_PER_GROUP_MAX, getExpectedResultsPerGroupMax());
		builder.append(EXPECTED_RESULTS_PER_SPECIFIC_OBJECTIVE_MAX, getExpectedResultsPerSpecificObjectiveMax());

		builder.append(ENABLE_ACTIVITIES_GROUPS, getEnableActivitiesGroups());
		builder.append(ACTIVITIES_MAX, getActivitiesMax());
		builder.append(ACTIVITIES_GROUPS_MAX, getActivitiesGroupsMax());
		builder.append(ACTIVITIES_PER_GROUP_MAX, getActivitiesPerGroupMax());
		builder.append(ACTIVITIES_PER_EXPECTED_RESULT_MAX, getActivitiesPerExpectedResultMax());

		builder.append(ENABLE_PREREQUISITES_GROUPS, getEnablePrerequisitesGroups());
		builder.append(PREREQUISITES_MAX, getPrerequisitesMax());
		builder.append(PREREQUISITES_GROUPS_MAX, getPrerequisitesGroupsMax());
		builder.append(PREREQUISITES_PER_GROUP_MAX, getPrerequisitesPerGroupMax());
	}

	/**
	 * Returns the {@code Boolean} value for the given {@code property}.
	 * 
	 * @param property
	 *          The property name.
	 * @return The {@code Boolean} value for the given {@code property}, or {@link #DEFAULT_VISIBILITY_GROUP_POLICY} if
	 *         value is {@code null}.
	 * @see #DEFAULT_VISIBILITY_GROUP_POLICY
	 */
	private Boolean getEnableProperty(final String property) {
		final Boolean booleanValue = get(property);
		return booleanValue != null ? booleanValue : DEFAULT_VISIBILITY_GROUP_POLICY;
	}

	// --
	// NAME.
	// --

	public String getName() {
		return get(NAME);
	}

	public void setName(String name) {
		set(NAME, name);
	}

	// --
	// SPECIFIC OBJECTIVES PARAMETERS.
	// --

	public Boolean getEnableSpecificObjectivesGroups() {
		return getEnableProperty(ENABLE_SPECIFIC_OBJECTIVES_GROUPS);
	}

	public void setEnableSpecificObjectivesGroups(Boolean enableSpecificObjectivesGroups) {
		set(ENABLE_SPECIFIC_OBJECTIVES_GROUPS, enableSpecificObjectivesGroups);
	}

	public Integer getSpecificObjectivesMax() {
		return get(SPECIFIC_OBJECTIVES_MAX);
	}

	public void setSpecificObjectivesMax(Integer specificObjectivesMax) {
		set(SPECIFIC_OBJECTIVES_MAX, specificObjectivesMax);
	}

	public Integer getSpecificObjectivesGroupsMax() {
		return get(SPECIFIC_OBJECTIVES_GROUPS_MAX);
	}

	public void setSpecificObjectivesGroupsMax(Integer specificObjectivesGroupsMax) {
		set(SPECIFIC_OBJECTIVES_GROUPS_MAX, specificObjectivesGroupsMax);
	}

	public Integer getSpecificObjectivesPerGroupMax() {
		return get(SPECIFIC_OBJECTIVES_PER_GROUP_MAX);
	}

	public void setSpecificObjectivesPerGroupMax(Integer specificObjectivesPerGroupMax) {
		set(SPECIFIC_OBJECTIVES_PER_GROUP_MAX, specificObjectivesPerGroupMax);
	}

	// --
	// EXPECTED RESULTS PARAMETERS.
	// --

	public Boolean getEnableExpectedResultsGroups() {
		return getEnableProperty(ENABLE_EXPECTED_RESULTS_GROUPS);
	}

	public void setEnableExpectedResultsGroups(Boolean enableExpectedResultsGroups) {
		set(ENABLE_EXPECTED_RESULTS_GROUPS, enableExpectedResultsGroups);
	}

	public Integer getExpectedResultsMax() {
		return get(EXPECTED_RESULTS_MAX);
	}

	public void setExpectedResultsMax(Integer expectedResultsMax) {
		set(EXPECTED_RESULTS_MAX, expectedResultsMax);
	}

	public Integer getExpectedResultsGroupsMax() {
		return get(EXPECTED_RESULTS_GROUPS_MAX);
	}

	public void setExpectedResultsGroupsMax(Integer expectedResultsGroupsMax) {
		set(EXPECTED_RESULTS_GROUPS_MAX, expectedResultsGroupsMax);
	}

	public Integer getExpectedResultsPerGroupMax() {
		return get(EXPECTED_RESULTS_PER_GROUP_MAX);
	}

	public void setExpectedResultsPerGroupMax(Integer expectedResultsPerGroupMax) {
		set(EXPECTED_RESULTS_PER_GROUP_MAX, expectedResultsPerGroupMax);
	}

	public Integer getExpectedResultsPerSpecificObjectiveMax() {
		return get(EXPECTED_RESULTS_PER_SPECIFIC_OBJECTIVE_MAX);
	}

	public void setExpectedResultsPerSpecificObjectiveMax(Integer expectedResultsPerSpecificObjectiveMax) {
		set(EXPECTED_RESULTS_PER_SPECIFIC_OBJECTIVE_MAX, expectedResultsPerSpecificObjectiveMax);
	}

	// --
	// ACTIVITIES PARAMETERS.
	// --

	public Boolean getEnableActivitiesGroups() {
		return getEnableProperty(ENABLE_ACTIVITIES_GROUPS);
	}

	public void setEnableActivitiesGroups(Boolean enableActivitiesGroups) {
		set(ENABLE_ACTIVITIES_GROUPS, enableActivitiesGroups);
	}

	public Integer getActivitiesMax() {
		return get(ACTIVITIES_MAX);
	}

	public void setActivitiesMax(Integer activitiesMax) {
		set(ACTIVITIES_MAX, activitiesMax);
	}

	public Integer getActivitiesGroupsMax() {
		return get(ACTIVITIES_GROUPS_MAX);
	}

	public void setActivitiesGroupsMax(Integer activitiesGroupsMax) {
		set(ACTIVITIES_GROUPS_MAX, activitiesGroupsMax);
	}

	public Integer getActivitiesPerGroupMax() {
		return get(ACTIVITIES_PER_GROUP_MAX);
	}

	public void setActivitiesPerGroupMax(Integer activitiesPerGroupMax) {
		set(ACTIVITIES_PER_GROUP_MAX, activitiesPerGroupMax);
	}

	public Integer getActivitiesPerExpectedResultMax() {
		return get(ACTIVITIES_PER_EXPECTED_RESULT_MAX);
	}

	public void setActivitiesPerExpectedResultMax(Integer activitiesPerExpectedResultMax) {
		set(ACTIVITIES_PER_EXPECTED_RESULT_MAX, activitiesPerExpectedResultMax);
	}

	// --
	// PREREQUISITES PARAMETERS.
	// --

	public Boolean getEnablePrerequisitesGroups() {
		return getEnableProperty(ENABLE_PREREQUISITES_GROUPS);
	}

	public void setEnablePrerequisitesGroups(Boolean enablePrerequisitesGroups) {
		set(ENABLE_PREREQUISITES_GROUPS, enablePrerequisitesGroups);
	}

	public Integer getPrerequisitesMax() {
		return get(PREREQUISITES_MAX);
	}

	public void setPrerequisitesMax(Integer prerequisitesMax) {
		set(PREREQUISITES_MAX, prerequisitesMax);
	}

	public Integer getPrerequisitesGroupsMax() {
		return get(PREREQUISITES_GROUPS_MAX);
	}

	public void setPrerequisitesGroupsMax(Integer prerequisitesGroupsMax) {
		set(PREREQUISITES_GROUPS_MAX, prerequisitesGroupsMax);
	}

	public Integer getPrerequisitesPerGroupMax() {
		return get(PREREQUISITES_PER_GROUP_MAX);
	}

	public void setPrerequisitesPerGroupMax(Integer prerequisitesPerGroupMax) {
		set(PREREQUISITES_PER_GROUP_MAX, prerequisitesPerGroupMax);
	}

}
