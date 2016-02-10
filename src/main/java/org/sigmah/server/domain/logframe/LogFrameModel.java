package org.sigmah.server.domain.logframe;

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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Logframe model domain entity.
 * </p>
 * <p>
 * Contains some attributes to parameterize a log frame.
 * </p>
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.LOGFRAME_MODEL_TABLE)
public class LogFrameModel extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -8714555958028249713L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.LOGFRAME_MODEL_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.LOGFRAME_MODEL_COLUMN_NAME, nullable = false, length = EntityConstants.LOGFRAME_MODEL_NAME_MAX_LENGTH)
	@NotNull
	@Size(max = EntityConstants.LOGFRAME_MODEL_NAME_MAX_LENGTH)
	private String name;

	// Specific objectives parameters.

	@Column(name = EntityConstants.LOGFRAME_MODEL_COLUMN_SO_ENABLE_GROUPS)
	private Boolean enableSpecificObjectivesGroups;

	@Column(name = EntityConstants.LOGFRAME_MODEL_COLUMN_SO_MAX)
	private Integer specificObjectivesMax;

	@Column(name = EntityConstants.LOGFRAME_MODEL_COLUMN_SO_GP_MAX)
	private Integer specificObjectivesGroupsMax;

	@Column(name = EntityConstants.LOGFRAME_MODEL_COLUMN_SO_PER_GP_MAX)
	private Integer specificObjectivesPerGroupMax;

	// Expected results parameters.

	@Column(name = EntityConstants.LOGFRAME_MODEL_COLUMN_ER_ENABLE_GROUPS)
	private Boolean enableExpectedResultsGroups;

	@Column(name = EntityConstants.LOGFRAME_MODEL_COLUMN_ER_MAX)
	private Integer expectedResultsMax;

	@Column(name = EntityConstants.LOGFRAME_MODEL_COLUMN_ER_GP_MAX)
	private Integer expectedResultsGroupsMax;

	@Column(name = EntityConstants.LOGFRAME_MODEL_COLUMN_ER_PER_GP_MAX)
	private Integer expectedResultsPerGroupMax;

	@Column(name = EntityConstants.LOGFRAME_MODEL_COLUMN_ER_PER_SO_MAX)
	private Integer expectedResultsPerSpecificObjectiveMax;

	// Activities parameters.

	@Column(name = EntityConstants.LOGFRAME_MODEL_COLUMN_A_ENABLE_GROUPS)
	private Boolean enableActivitiesGroups;

	@Column(name = EntityConstants.LOGFRAME_MODEL_COLUMN_A_MAX)
	private Integer activitiesMax;

	@Column(name = EntityConstants.LOGFRAME_MODEL_COLUMN_A_GP_MAX)
	private Integer activitiesGroupsMax;

	@Column(name = EntityConstants.LOGFRAME_MODEL_COLUMN_A_PER_GP_MAX)
	private Integer activitiesPerGroupMax;

	@Column(name = EntityConstants.LOGFRAME_MODEL_COLUMN_A_PER_ER_MAX)
	private Integer activitiesPerExpectedResultMax;

	// Prerequisites parameters.

	@Column(name = EntityConstants.LOGFRAME_MODEL_COLUMN_P_ENABLE_GROUPS)
	private Boolean enablePrerequisitesGroups;

	@Column(name = EntityConstants.LOGFRAME_MODEL_COLUMN_P_MAX)
	private Integer prerequisitesMax;

	@Column(name = EntityConstants.LOGFRAME_MODEL_COLUMN_P_GP_MAX)
	private Integer prerequisitesGroupsMax;

	@Column(name = EntityConstants.LOGFRAME_MODEL_COLUMN_P_PER_GP_MAX)
	private Integer prerequisitesPerGroupMax;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	// Trick: using '@ManyToOne' to avoid automatic load of the object (see '@OneToOne' lazy issue).
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.PROJECT_MODEL_COLUMN_ID)
	private ProjectModel projectModel;

	public LogFrameModel() {
	}

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Reset the identifiers of the object.
	 */
	public void resetImport() {
		this.id = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("name", name);

		builder.append("enableSpecificObjectivesGroups", enableSpecificObjectivesGroups);
		builder.append("specificObjectivesMax", specificObjectivesMax);
		builder.append("specificObjectivesGroupsMax", specificObjectivesGroupsMax);
		builder.append("specificObjectivesPerGroupMax", specificObjectivesPerGroupMax);

		builder.append("enableExpectedResultsGroups", enableExpectedResultsGroups);
		builder.append("expectedResultsMax", expectedResultsMax);
		builder.append("expectedResultsGroupsMax", expectedResultsGroupsMax);
		builder.append("expectedResultsPerGroupMax", expectedResultsPerGroupMax);
		builder.append("expectedResultsPerSpecificObjectiveMax", expectedResultsPerSpecificObjectiveMax);

		builder.append("enableActivitiesGroups", enableActivitiesGroups);
		builder.append("activitiesMax", activitiesMax);
		builder.append("activitiesGroupsMax", activitiesGroupsMax);
		builder.append("activitiesPerGroupMax", activitiesPerGroupMax);
		builder.append("activitiesPerExpectedResultMax", activitiesPerExpectedResultMax);

		builder.append("enablePrerequisitesGroups", enablePrerequisitesGroups);
		builder.append("prerequisitesMax", prerequisitesMax);
		builder.append("prerequisitesGroupsMax", prerequisitesGroupsMax);
		builder.append("prerequisitesPerGroupMax", prerequisitesPerGroupMax);
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public ProjectModel getProjectModel() {
		return projectModel;
	}

	public void setProjectModel(ProjectModel projectModel) {
		this.projectModel = projectModel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getEnableSpecificObjectivesGroups() {
		return enableSpecificObjectivesGroups;
	}

	public void setEnableSpecificObjectivesGroups(Boolean enableSpecificObjectivesGroups) {
		this.enableSpecificObjectivesGroups = enableSpecificObjectivesGroups;
	}

	public Integer getSpecificObjectivesMax() {
		return specificObjectivesMax;
	}

	public void setSpecificObjectivesMax(Integer specificObjectivesMax) {
		this.specificObjectivesMax = specificObjectivesMax;
	}

	public Integer getSpecificObjectivesGroupsMax() {
		return specificObjectivesGroupsMax;
	}

	public void setSpecificObjectivesGroupsMax(Integer specificObjectivesGroupsMax) {
		this.specificObjectivesGroupsMax = specificObjectivesGroupsMax;
	}

	public Integer getSpecificObjectivesPerGroupMax() {
		return specificObjectivesPerGroupMax;
	}

	public void setSpecificObjectivesPerGroupMax(Integer specificObjectivesPerGroupMax) {
		this.specificObjectivesPerGroupMax = specificObjectivesPerGroupMax;
	}

	public Boolean getEnableExpectedResultsGroups() {
		return enableExpectedResultsGroups;
	}

	public void setEnableExpectedResultsGroups(Boolean enableExpectedResultsGroups) {
		this.enableExpectedResultsGroups = enableExpectedResultsGroups;
	}

	public Integer getExpectedResultsMax() {
		return expectedResultsMax;
	}

	public void setExpectedResultsMax(Integer expectedResultsMax) {
		this.expectedResultsMax = expectedResultsMax;
	}

	public Integer getExpectedResultsGroupsMax() {
		return expectedResultsGroupsMax;
	}

	public void setExpectedResultsGroupsMax(Integer expectedResultsGroupsMax) {
		this.expectedResultsGroupsMax = expectedResultsGroupsMax;
	}

	public Integer getExpectedResultsPerGroupMax() {
		return expectedResultsPerGroupMax;
	}

	public void setExpectedResultsPerGroupMax(Integer expectedResultsPerGroupMax) {
		this.expectedResultsPerGroupMax = expectedResultsPerGroupMax;
	}

	public Integer getExpectedResultsPerSpecificObjectiveMax() {
		return expectedResultsPerSpecificObjectiveMax;
	}

	public void setExpectedResultsPerSpecificObjectiveMax(Integer expectedResultsPerSpecificObjectiveMax) {
		this.expectedResultsPerSpecificObjectiveMax = expectedResultsPerSpecificObjectiveMax;
	}

	public Boolean getEnableActivitiesGroups() {
		return enableActivitiesGroups;
	}

	public void setEnableActivitiesGroups(Boolean enableActivitiesGroups) {
		this.enableActivitiesGroups = enableActivitiesGroups;
	}

	public Integer getActivitiesMax() {
		return activitiesMax;
	}

	public void setActivitiesMax(Integer activitiesMax) {
		this.activitiesMax = activitiesMax;
	}

	public Integer getActivitiesGroupsMax() {
		return activitiesGroupsMax;
	}

	public void setActivitiesGroupsMax(Integer activitiesGroupsMax) {
		this.activitiesGroupsMax = activitiesGroupsMax;
	}

	public Integer getActivitiesPerGroupMax() {
		return activitiesPerGroupMax;
	}

	public void setActivitiesPerGroupMax(Integer activitiesPerGroupMax) {
		this.activitiesPerGroupMax = activitiesPerGroupMax;
	}

	public Integer getActivitiesPerExpectedResultMax() {
		return activitiesPerExpectedResultMax;
	}

	public void setActivitiesPerExpectedResultMax(Integer activitiesPerExpectedResultMax) {
		this.activitiesPerExpectedResultMax = activitiesPerExpectedResultMax;
	}

	public Boolean getEnablePrerequisitesGroups() {
		return enablePrerequisitesGroups;
	}

	public void setEnablePrerequisitesGroups(Boolean enablePrerequisitesGroups) {
		this.enablePrerequisitesGroups = enablePrerequisitesGroups;
	}

	public Integer getPrerequisitesMax() {
		return prerequisitesMax;
	}

	public void setPrerequisitesMax(Integer prerequisitesMax) {
		this.prerequisitesMax = prerequisitesMax;
	}

	public Integer getPrerequisitesGroupsMax() {
		return prerequisitesGroupsMax;
	}

	public void setPrerequisitesGroupsMax(Integer prerequisitesGroupsMax) {
		this.prerequisitesGroupsMax = prerequisitesGroupsMax;
	}

	public Integer getPrerequisitesPerGroupMax() {
		return prerequisitesPerGroupMax;
	}

	public void setPrerequisitesPerGroupMax(Integer prerequisitesPerGroupMax) {
		this.prerequisitesPerGroupMax = prerequisitesPerGroupMax;
	}

}
