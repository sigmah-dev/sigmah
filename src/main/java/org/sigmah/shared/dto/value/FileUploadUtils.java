package org.sigmah.shared.dto.value;

/**
 * Constants to manage files upload.
 * 
 * @author tmi (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public final class FileUploadUtils {

	/**
	 * Provides only static methods.
	 */
	private FileUploadUtils() {
	}

	// --
	// File upload sizes.
	// --

	/**
	 * Max size for each uploaded file (20 MB).
	 */
	public static final int MAX_UPLOAD_FILE_SIZE = 20971520;

	/**
	 * Max size for each uploaded logo image (256 KB)
	 */
	public static final int MAX_UPLOAD_IMAGE_SIZE = 262144;

	// --
	// Properties keys constants.
	// --

	/**
	 * Uploaded file id.
	 */
	public static final String DOCUMENT_ID = "id";

	/**
	 * Uploaded file content.
	 */
	public static final String DOCUMENT_CONTENT = "file";

	/**
	 * Uploaded file name.
	 */
	public static final String DOCUMENT_NAME = "name";

	/**
	 * Version d'un document.
	 */
	public static final String DOCUMENT_VERSION = "version";

	/**
	 * Uploaded file author.
	 */
	public static final String DOCUMENT_AUTHOR = "author";

	/**
	 * Flexible element id to which the uploaded file belongs.
	 */
	public static final String DOCUMENT_FLEXIBLE_ELEMENT = "element";

	/**
	 * Project id to which the uploaded file belongs.
	 */
	public static final String DOCUMENT_PROJECT = "project";

	/**
	 * Comments of a version.
	 */
	public static final String DOCUMENT_COMMENTS = "comments";

	/**
	 * The expected date of a monitored point.
	 */
	public static final String MONITORED_POINT_DATE = "mpdate";

	/**
	 * The label of a monitored point.
	 */
	public static final String MONITORED_POINT_LABEL = "mplabel";

}
