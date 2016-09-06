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

/**
 * List of indexes used in the schema {@link Store}.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public final class Indexes {
	
	/**
	 * Private constructor.
	 */
	private Indexes() {
		// Empty.
	}
	
	public static final String COMPUTATION_DEPENDENCIES = "dependencies";
	public static final String COMPUTATION_CONTRIBUTION = "contribution";
	
	public static final String FILE_DATA_FILEVERSIONID = "fileVersionId";
	
	public static final String MONITORED_POINT_PARENTLISTID = "parentListId";
	
	public static final String PROJECT_ORGUNIT = "orgUnit";
	public static final String PROJECT_REMINDERSLISTID = "remindersListId";
	public static final String PROJECT_POINTSLISTID = "pointsListId";
	public static final String PROJECT_PROJECTFUNDINGS = "projectFundings";
	
	public static final String PROJECT_REPORT_VERSIONID = "versionId";
	
	public static final String REMINDER_PARENTLISTID = "parentListId";
	
	public static final String REPORT_REFERENCE_PARENTID = "parentId";
	
	public static final String TRANSFERT_TYPE = "type";
	public static final String TRANSFERT_FILEVERSIONID = "fileVersionId";
	
	public static final String USER_ORGANIZATION = "organization";
	public static final String USER_ORGUNIT = "orgUnit";
	
}
