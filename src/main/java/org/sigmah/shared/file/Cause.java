package org.sigmah.shared.file;

/**
 * List of reason that caused a file download or a file upload action to failed.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public enum Cause {
	EMPTY_FILE,
	FILE_TOO_LARGE,
	BAD_REQUEST,
	SERVER_ERROR,
	BLOB_READ_ERROR,
	CACHE_ERROR;
}
