package org.sigmah.shared.servlet;

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


import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.util.ExportUtils.ExportDataVersion;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;

/**
 * <p>
 * Additional servlet shared parameters.
 * </p>
 * <p>
 * <b>How to add a new Servlet Method to an <u>existing</u> Servlet?</b>
 * <ol>
 * <li>Declare the new method in {@link ServletMethod} with its unique java name.</li>
 * <li>Implement the new method in corresponding server {@code HttpServlet}.</li>
 * </ol>
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @see com.google.gwt.http.client.Response
 */
public final class ServletConstants {

	/**
	 * <p>
	 * Additional servlet(s) enumeration.
	 * </p>
	 * <p>
	 * If a new servlet is served by the application, it must be added here in order to be accessed by client-side.
	 * </p>
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static enum Servlet {

		/**
		 * Upload/Download servlet.
		 */
		FILE,

		/**
		 * Servlet choosing the right HTML5 manifest depending on the client browser and language.
		 */
		MANIFEST,

		/**
		 * Export servlet.
		 */
		EXPORT,

		/**
		 * Import Servlet
		 */
		IMPORT,
		// Add other servlets here.
		;

		/**
		 * Gets the servlet path name.
		 * 
		 * @return the servlet path name.
		 */
		public String getPathName() {
			return name().toLowerCase();
		}

		/**
		 * Gets the full servlet URL.<br>
		 * This method must be executed after application is initialized (use of {@link com.google.gwt.core.client.GWT}
		 * class).
		 * 
		 * @return The full servlet URL (with host and context).
		 */
		public String getUrl() {
			return GWT.getHostPageBaseURL() + GWT.getModuleName() + '/' + getPathName();
		}

		/**
		 * Returns the {@code Servlet} value corresponding to the given {@code pathName}.
		 * 
		 * @param pathName
		 *          The request URI (composed with {@code /module_name/path_name}).
		 * @return the {@code Servlet} value corresponding to the given {@code pathName}, or {@code null}.
		 */
		public static Servlet fromPathName(final String pathName) {

			if (pathName == null) {
				return null;
			}

			for (final Servlet s : Servlet.values()) {
				if (ClientUtils.equalsIgnoreCase(pathName, s.getPathName())) {
					return s;
				}
			}

			return null;
		}
	}

	/**
	 * <p>
	 * Additional servlets methods enumeration.
	 * </p>
	 * <p>
	 * If a new servlet is served by the application, its methods must be added here in order to be accessed by
	 * client-side.
	 * Methods names must be unique.
	 * </p>
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static enum ServletMethod {

		// --
		// File Servlet.
		// --

		/**
		 * <p>
		 * Uploads a file.
		 * </p>
		 * <p>
		 * Expected request parameter(s):
		 * <ul>
		 * <li>{@link RequestParameter#ID} : File name.</li>
		 * </ul>
		 * </p>
		 */
		UPLOAD("upload"),

		/**
		 * <p>
		 * Uploads the new logo of an {@link org.sigmah.server.domain.Organization}.
		 * </p>
		 * <p>
		 * Expected request parameter(s):
		 * <ul>
		 * <li>{@link RequestParameter#ID} : The organization id.</li>
		 * </ul>
		 * </p>
		 */
		UPLOAD_ORGANIZATION_LOGO("uploadOrganizationLogo"),

		/**
		 * <p>
		 * Downloads a file version.
		 * </p>
		 * <p>
		 * Expected request parameter(s):
		 * <ul>
		 * <li>{@link RequestParameter#ID} : Id of the file <b>version</b> to download.</li>
		 * </ul>
		 * </p>
		 */
		DOWNLOAD_FILE("downloadFile"),

		/**
		 * <p>
		 * Downloads a <b>logo</b> file.
		 * </p>
		 * <p>
		 * Expected request parameter(s):
		 * <ul>
		 * <li>{@link RequestParameter#ID} : Name of the logo file to download.</li>
		 * </ul>
		 * </p>
		 */
		DOWNLOAD_LOGO("downloadLogo"),

		/**
		 * <p>
		 * Downloads an <b>archive</b> file.
		 * </p>
		 * <p>
		 * Expected request parameter(s):
		 * <ul>
		 * <li>{@link RequestParameter#ID} : Name of the archive file to download.</li>
		 * </ul>
		 * </p>
		 */
		DOWNLOAD_ARCHIVE("downloadArchive"),

		// --
		// Export Servlet.
		// --

		/**
		 * <p>
		 * Exports an {@link org.sigmah.server.domain.OrgUnit} {@code XLS} or {@code ODS} synthesis.
		 * </p>
		 * <p>
		 * Expected request parameter(s):
		 * <ul>
		 * <li>{@link RequestParameter#ID} : The OrgUnit id.</li>
		 * </ul>
		 * </p>
		 */
		EXPORT_ORG_UNIT("exportOrgUnit"),

		/**
		 * <p>
		 * Exports a project data.
		 * </p>
		 * <p>
		 * Expected request parameter(s):
		 * <ul>
		 * <li>{@link RequestParameter#ID} : Project id.</li>
		 * </ul>
		 * </p>
		 */
		EXPORT_PROJECT("exportProject"),

		/**
		 * <p>
		 * Exports a project LogFrame.
		 * </p>
		 * <p>
		 * Expected request parameter(s):
		 * <ul>
		 * <li>{@link RequestParameter#ID} : Project id.</li>
		 * </ul>
		 * </p>
		 */
		EXPORT_PROJECT_LOGFRAME("exportProjectLogFrame"),

		/**
		 * <p>
		 * Exports all indicators of a project.
		 * </p>
		 * <p>
		 * Expected request parameter(s):
		 * <ul>
		 * <li>{@link RequestParameter#ID} : Project id.</li>
		 * </ul>
		 * </p>
		 */
		EXPORT_PROJECT_INDICATORS("exportProjectIndicators"),

		/**
		 * <p>
		 * Exports a Project or OrgUnit report.
		 * </p>
		 * <p>
		 * Expected request parameter(s):
		 * <ul>
		 * <li>{@link RequestParameter#ID} : Report id.</li>
		 * </ul>
		 * </p>
		 */
		EXPORT_REPORT("exportReport"),

		/**
		 * <p> Global Export Projets. </p>
		 * <p>
		 * Expected request parameter(s):
		 * <ul>
		 * <li>{@link RequestParameter#ID} : Organization id.</li>
		 * <li>{@link RequestParameter#GLOBAL_EXPORT_ID} : Global Export id.</li>
		 * <li>{@link RequestParameter#VERSION} : {@link ExportDataVersion} .</li>
		 * </ul>
		 */
		EXPORT_GLOBAL("exportGlobal"),

		/**
		 * <p> Model Gategory Export.</p>
		 * <p>
		 * Expected request parameter(s):
		 * <ul>
		 * <li>{@link RequestParameter#ID} : Category id.</li>
		 * </ul>
		 */

		EXPORT_MODEL_CATEGORY("exportCategoryModel"),

		/**
		 * <p> Model Report Export.</p>
		 * <p>
		 * Expected request parameter(s):
		 * <ul>
		 * <li>{@link RequestParameter#ID} : Report id.</li>
		 * </ul>
		 */

		EXPORT_MODEL_REPORT("exportReportModel"),

		/**
		 * <p> Model Project Export</p>
		 * <p>
		 * Expected request parameter(s):
		 * <ul>
		 * <li>{@link RequestParameter#ID} : Project id.</li>
		 * </ul>
		 */

		EXPORT_MODEL_PROJECT("exportProjectModel"),

		/**
		 * <p> Model OrgUnit Export.</p>
		 * <p>
		 * Expected request parameter(s):
		 * <ul>
		 * <li>{@link RequestParameter#ID} : OrgUnit id.</li>
		 * </ul>
		 */

		EXPORT_MODEL_ORGUNIT("exportOrgUnitModel"),

		/**
		 * <p> Model Gategory Import.</p>
		 * <p>
		 * Expected request parameter(s):
		 * <ul>
		 * <li></li>
		 * </ul>
		 */

		IMPORT_MODEL_CATEGORY("importCategoryModel"),

		/**
		 * <p> Model Report Import.</p>
		 * <p>
		 * Expected request parameter(s):
		 * <ul>
		 * <li></li>
		 * </ul>
		 */

		IMPORT_MODEL_REPORT("importReportModel"),

		/**
		 * <p> Model Project Import.</p>
		 * <p>
		 * Expected request parameter(s):
		 * <ul>
		 * <li></li>
		 * </ul>
		 */

		IMPORT_MODEL_PROJECT("importProjectModel"),

		/**
		 * <p> Model OrgUnit Import</p>
		 * <p>
		 * Expected request parameter(s):
		 * <ul>
		 * <li></li>
		 * </ul>
		 */

		IMPORT_MODEL_ORGUNIT("importOrgUnitModel"),
		
		/**
		 * <p>Prepare the importation a file containing values by storing it on
		 * the server.</p>
		 * 
		 * Expected request parameter:
		 * <ul>
		 * <li>{@link org.sigmah.shared.dto.value.FileUploadUtils#DOCUMENT_CONTENT}: Content of the file to store.</li>
		 * </ul>
		 */
		IMPORT_STORE_FILE("storeFile")

		// Add other servlets methods here.

		;

		/**
		 * The servlet {@code java} method name.
		 */
		private final String name;

		/**
		 * Whether the servlet method UI destination is a new {@code popup windows}.
		 */
		private final boolean popup;

		private ServletMethod(final String name) {
			this(name, false);
		}

		private ServletMethod(final String name, final boolean popup) {
			assert ClientUtils.isNotBlank(name) : "The servlet method name is required.";
			this.name = name;
			this.popup = popup;
		}

		/**
		 * Gets the servlet method name.
		 * 
		 * @return the servlet method name.
		 */
		public String getName() {
			return name;
		}

		/**
		 * Returns if the servlet method UI destination is a new {@code popup windows}.
		 * 
		 * @return {@code true} if the servlet method UI destination is a new {@code popup windows}, {@code false} if the UI
		 *         destination is the main screen.
		 */
		public boolean isPopup() {
			return popup;
		}

		/**
		 * Returns the {@code ServletMethod} value corresponding to the given {@code methodName}.
		 * 
		 * @param methodName
		 *          The method name.
		 * @return the {@code ServletMethod} value corresponding to the given {@code methodName}, or {@code null}.
		 */
		public static ServletMethod fromMethodName(final String methodName) {

			if (methodName == null) {
				return null;
			}

			for (final ServletMethod servletMethod : ServletMethod.values()) {
				if (ClientUtils.equals(methodName, servletMethod.getName())) {
					return servletMethod;
				}
			}

			return null;
		}
	}

	/**
	 * Builds the given {@code errorCode} corresponding error message.
	 * 
	 * @param errorCode
	 *          The HTTP error code.
	 * @return The given {@code errorCode} corresponding error message.
	 */
	public static String buildErrorResponse(final int errorCode) {
		return String.valueOf(ERROR_RESPONSE_PREFIX) + errorCode;
	}

	/**
	 * Check if the given message indicates a server error.
	 * 
	 * @param message
	 *          Message returned by a servlet.
	 * @return <code>true</code> if an error happened, <code>false</code> otherwise.
	 */
	public static boolean isErrorResponse(String message) {
		return message != null && !message.isEmpty() && message.charAt(0) == ERROR_RESPONSE_PREFIX;
	}

	/**
	 * Parse the error message and return the embeded error code as an integer. If the message does not denote an error,
	 * 200 is returned.
	 * 
	 * @param message
	 *          Message returned by a servlet.
	 * @return The error code if available, 200 otherwise.
	 */
	public static int getErrorCode(String message) {
		if (!isErrorResponse(message)) {
			return Response.SC_OK;
		} else {
			return Integer.parseInt(message.substring(1));
		}
	}

	/**
	 * Prefix of every error message.
	 */
	private static final char ERROR_RESPONSE_PREFIX = '#';

	/**
	 * Servlet parameter key referencing authentication token used to secure servlet calls.
	 */
	public static final String AUTHENTICATION_TOKEN = "_s_at";

	/**
	 * Servlet parameter key referencing method to execute.
	 * 
	 * @see ServletMethod
	 */
	public static final String SERVLET_METHOD = "_s_me";

	/**
	 * Servlet parameter key referencing origin page token.
	 */
	public static final String ORIGIN_PAGE_TOKEN = "_s_op";

	/**
	 * Servlet parameter key referencing random value.
	 * Avoids cache related issues.
	 */
	static final String RANDOM = "_s_ra";

	/**
	 * Servlet parameter key referencing ajax request flag.
	 * Allows server-side servlet to detect ajax call from regular access.
	 */
	public static final String AJAX = "_s_aj";

	/**
	 * Utility class <em>private</em> constructor.
	 */
	private ServletConstants() {
		// Provides only static methods.
	}

}
