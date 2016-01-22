package org.sigmah.shared.dto.reminder;

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

import java.util.Date;
import java.util.List;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.base.mapping.CustomMappingField;
import org.sigmah.shared.dto.base.mapping.IsMappingMode;
import org.sigmah.shared.dto.base.mapping.MappingField;
import org.sigmah.shared.dto.value.FileDTO;

/**
 * DTO mapping class for entity reminder.MonitoredPoint.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class MonitoredPointDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1259632326368572850L;

	/**
	 * DTO corresponding domain entity name.
	 */
	public static final String ENTITY_NAME = "reminder.MonitoredPoint";

	// DTO attributes keys.
	public static final String PARENT_LIST_ID = "parentListId";
	public static final String LABEL = "label";
	public static final String EXPECTED_DATE = "expectedDate";
	public static final String COMPLETION_DATE = "completionDate";
	public static final String DELETED = "deleted";
	public static final String COMPLETED = "completed";
	public static final String HISTORY = "history";
	public static final String FILE = "file";

	/**
	 * Project name& code used for reminder in dashboard.
	 */
    public static final String PROJECT_ID = "projectId";
    public static final String PROJECT_NAME = "projectName";
    public static final String PROJECT_CODE = "projectCode";   
    

	/**
	 * Mapping configurations.
	 * 
	 */
	public static enum Mode implements IsMappingMode {

		/**
		 * Base mapping <b>without</b> monitored point history or file.
		 */
		BASE(new MappingField(HISTORY), new MappingField(FILE)),

		/**
		 * Base mapping <b>with</b> monitored point history, but <b>not<b/> file.
		 */
		WITH_HISTORY(new MappingField(FILE)),

		/**
		 * Full mapping <b>with</b> monitored point history <b>and</b> file.
		 */
		FULL(),

		;

		private final CustomMappingField[] customFields;
		private final MappingField[] excludedFields;

		private Mode(final MappingField... excludedFields) {
			this.customFields = new CustomMappingField[] {
				new CustomMappingField("parentList.id", PARENT_LIST_ID)
			};
			this.excludedFields = excludedFields;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getMapId() {
			return name();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CustomMappingField[] getCustomFields() {
			return customFields;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MappingField[] getExcludedFields() {
			return excludedFields;
		}

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
		builder.append(LABEL, getLabel());
		builder.append(EXPECTED_DATE, getExpectedDate());
		builder.append(COMPLETION_DATE, getCompletionDate());
		builder.append(COMPLETED, isCompleted());
		builder.append(DELETED, getDeleted());
	}
	
	public Integer getParentListId() {
		return get(PARENT_LIST_ID);
	}
	
	public void setParentListId(Integer parentListId) {
		set(PARENT_LIST_ID, parentListId);
	}

	public boolean isCompleted() {
		return getCompletionDate() != null;
	}

	// Label
	public String getLabel() {
		return get(LABEL);
	}

	public void setLabel(String label) {
		set(LABEL, label);
	}

	// Expected date
	public Date getExpectedDate() {
		return get(EXPECTED_DATE);
	}

	public void setExpectedDate(Date expectedDate) {
		set(EXPECTED_DATE, expectedDate);
	}

	// Completion date
	public Date getCompletionDate() {
		return get(COMPLETION_DATE);
	}

	public void setCompletionDate(Date completionDate) {
		set(COMPLETION_DATE, completionDate);
		setIsCompleted();
	}

	public void setIsCompleted() {
		set(COMPLETED, getCompletionDate() != null);
	}

	public boolean getIsCompleted() {
		return (Boolean) get(COMPLETED);
	}

	// File
	public FileDTO getFile() {
		return get(FILE);
	}

	public void setFile(FileDTO file) {
		set(FILE, file);
	}

	// Deleted
	public Boolean getDeleted() {
		return (Boolean) get(DELETED);
	}

	public void setDeleted(Boolean isDeleted) {
		set(DELETED, isDeleted);
	}

	// History
	public List<MonitoredPointHistoryDTO> getHistory() {
		return get(HISTORY);
	}

	public void setHistory(List<MonitoredPointHistoryDTO> history) {
		set(HISTORY, history);
	}

    // ProjectId
	public Integer getProjectId() {
		return get(PROJECT_ID);
	}

	public void setProjectId(Integer projectId) {
		set(PROJECT_ID, projectId);
	}
    
    // ProjectName
	public String getProjectName() {
		return get(PROJECT_NAME);
	}

	public void setProjectName(String projectName) {
		set(PROJECT_NAME, projectName);
	}

    // ProjectCode
	public String getProjectCode() {
		return get(PROJECT_CODE);
	}

	public void setProjectCode(String projectCode) {
		set(PROJECT_CODE, projectCode);
	}

}
