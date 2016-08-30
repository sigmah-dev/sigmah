package org.sigmah.offline.indexeddb;

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
