package org.sigmah.offline.indexeddb;

import java.util.Map;

/**
 * List of every store used by Sigmah.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public enum Store implements Schema {
	
	AUTHENTICATION,
	CATEGORY_TYPE,
	CATEGORY_ELEMENT,
	COUNTRY,
	COMMAND(true),
	FILE_DATA(true, "fileVersionId", "fileVersion.id"),
	HISTORY,
	LOG_FRAME,
	LOGO,
	MONITORED_POINT("parentListId", "parentListId"),
	ORGANIZATION,
	ORG_UNIT,
	ORG_UNIT_MODEL,
	PERSONAL_CALENDAR,
	PAGE_ACCESS,
	PHASE,
	PHASE_MODEL,
	PROJECT("orgUnit", "orgUnit",
			"remindersListId", "remindersListId",
			"pointsListId", "pointsListId"),
	PROJECT_MODEL,
	PROJECT_REPORT("versionId", "versionId"),
	REMINDER("parentListId", "parentListId"),
	REPORT_REFERENCE("parentId", "parentId"),
	TRANSFERT(true, "type", "type",
			"fileVersionId", "fileVersion.id"),
	USER("organization", "organization"),
	VALUE;
	
	private final boolean autoIncrement;
	private final boolean enabled;
	private final Map<String, String> indexes;

	private Store(String... indexes) {
		this(false, true, indexes);
	}

	private Store(boolean autoIncrement, String... indexes) {
		this(autoIncrement, true, indexes);
	}

	private Store(boolean autoIncrement, boolean enabled, String... indexes) {
		this.autoIncrement = autoIncrement;
		this.enabled = enabled;
		this.indexes = Stores.toIndexMap(indexes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> getIndexes() {
		return indexes;
	}
	
}
