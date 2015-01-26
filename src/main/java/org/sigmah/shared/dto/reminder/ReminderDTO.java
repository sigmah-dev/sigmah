package org.sigmah.shared.dto.reminder;

import java.util.Date;
import java.util.List;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.base.mapping.CustomMappingField;
import org.sigmah.shared.dto.base.mapping.IsMappingMode;
import org.sigmah.shared.dto.base.mapping.MappingField;

/**
 * DTO mapping class for entity reminder.Reminder.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ReminderDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 6733872261309621888L;

	/**
	 * DTO corresponding domain entity name.
	 */
	public static final String ENTITY_NAME = "reminder.Reminder";

	// DTO attributes keys.
	public static final String PARENT_LIST_ID = "parentListId";
	public static final String LABEL = "label";
	public static final String EXPECTED_DATE = "expectedDate";
	public static final String COMPLETION_DATE = "completionDate";
	public static final String DELETED = "deleted";
	public static final String COMPLETED = "completed";
	public static final String HISTORY = "history";

	/**
	 * Project id used by creation/edition service.
	 */
	public static final String PROJECT_ID = "projectId";

	/**
	 * Mapping configurations.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static enum Mode implements IsMappingMode {

		/**
		 * Base mapping without reminder history.
		 */
		BASE(new MappingField(HISTORY)),

		/**
		 * Base mapping <b>with</b> reminder history.
		 */
		WITH_HISTORY(),

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

	// Deleted
	public Boolean getDeleted() {
		return (Boolean) get(DELETED);
	}

	public void setDeleted(Boolean isDeleted) {
		set(DELETED, isDeleted);
	}

	public void setIsCompleted() {
		set(COMPLETED, getCompletionDate() != null);
	}

	public boolean getIsCompleted() {
		return (Boolean) get(COMPLETED);
	}

	// History
	public List<ReminderHistoryDTO> getHistory() {
		return get(HISTORY);
	}

	public void setHistory(List<ReminderHistoryDTO> history) {
		set(HISTORY, history);
	}

}
