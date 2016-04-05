package org.sigmah.offline.indexeddb;

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
	PROJECT_TEAM_MEMBERS,
	REMINDER("parentListId", "parentListId"),
	REPORT_REFERENCE("parentId", "parentId"),
	TRANSFERT(true, "type", "type",
			"fileVersionId", "fileVersion.id"),
	USER("organization", "organization",
			"orgUnit", "orgUnit"),
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
