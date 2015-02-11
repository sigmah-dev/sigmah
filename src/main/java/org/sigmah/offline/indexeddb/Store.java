package org.sigmah.offline.indexeddb;

/**
 * List of every store used by Sigmah.
 * <p/>
 * A store is like an IndexedDB table. Each store is made to store one type of
 * object.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public enum Store {
	AUTHENTICATION,
	CATEGORY_TYPE,
	CATEGORY_ELEMENT,
	COUNTRY,
	COMMAND(true),
	FILE_DATA(true),
	HISTORY,
	LOG_FRAME,
	LOGO,
	MONITORED_POINT,
	ORGANIZATION,
	ORG_UNIT,
	ORG_UNIT_MODEL,
	PERSONAL_CALENDAR,
	PAGE_ACCESS,
	PHASE,
	PHASE_MODEL,
	PROJECT,
	PROJECT_MODEL,
	PROJECT_REPORT,
	REMINDER,
	REPORT_REFERENCE,
	TRANSFERT(true),
	USER,
	VALUE;
	
	private final boolean autoIncrement;

	private Store() {
		autoIncrement = false;
	}

	private Store(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}
}
