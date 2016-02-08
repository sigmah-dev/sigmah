package org.sigmah.shared.dto.referential;

/**
 * Status of an automated import.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public enum AutomatedImportStatus {
	
	UPDATED,
	ABIGUOUS,
	WAS_LOCKED,
	UNLOCK_FAILED,
	UNLOCKED_AND_UPDATED,
	NOT_FOUND,
	CREATED_AND_UPDATED,
	CREATION_FAILED,
	;
	
}
