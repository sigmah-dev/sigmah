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
	CONTACT,
	CONTACT_HISTORY("contactId", "contactId"),
	CONTACT_RELATIONSHIP("contactId", "contactId"),
	COUNTRY,
	COMMAND(true),
	COMPUTATION(
			Indexes.COMPUTATION_DEPENDENCIES, "dependencies",
			Indexes.COMPUTATION_CONTRIBUTION, "contribution"
	),
	FILE_DATA(true, 
			Indexes.FILE_DATA_FILEVERSIONID, "fileVersion.id"
	),
	HISTORY,
	LAYOUT_GROUP_ITERATION,
	LOG_FRAME,
	LOGO,
	MONITORED_POINT(
			Indexes.MONITORED_POINT_PARENTLISTID, "parentListId"
	),
	ORGANIZATION,
	ORG_UNIT,
	ORG_UNIT_MODEL,
	PERSONAL_CALENDAR,
	PAGE_ACCESS,
	PHASE,
	PHASE_MODEL,
	PROFILE,
	PROJECT(
			Indexes.PROJECT_ORGUNIT, "orgUnit",
			Indexes.PROJECT_REMINDERSLISTID, "remindersListId",
			Indexes.PROJECT_POINTSLISTID, "pointsListId",
			Indexes.PROJECT_PROJECTFUNDINGS, "projectFundings"
	),
	PROJECT_MODEL,
	PROJECT_REPORT(
			Indexes.PROJECT_REPORT_VERSIONID, "versionId"
	),
	PROJECT_TEAM_MEMBERS,
	REMINDER(
			Indexes.REMINDER_PARENTLISTID, "parentListId"
	),
	REPORT_REFERENCE(
			Indexes.REPORT_REFERENCE_PARENTID, "parentId"
	),
	TRANSFERT(true, 
			Indexes.TRANSFERT_TYPE, "type",
			Indexes.TRANSFERT_FILEVERSIONID, "fileVersion.id"),
	USER(
			Indexes.USER_ORGANIZATION, "organization",
			Indexes.USER_ORGUNIT, "orgUnits"
	),
	USER_UNITS_RESULT,
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
