package org.sigmah.shared.dto.value;

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
	 * Locally generated file id.
	 */
	public static final String GENERATED_ID = "generatedId";

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
