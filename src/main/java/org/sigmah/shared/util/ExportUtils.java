package org.sigmah.shared.util;

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

import java.util.HashMap;
import java.util.Map;

/**
 * Constants to manage exports.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class ExportUtils {

	private ExportUtils() {
		// Provides only static methods.
	}

	/**
	 * Defines the different types of entity supported by the export.
	 * 
	 * @author Tom Miette (tmiette@ideia.fr)
	 */
	public static enum ExportType {

		/**
		 * Log frame.
		 */
		PROJECT_LOG_FRAME,

		/**
		 * Project report.
		 */
		PROJECT_REPORT,

		/**
		 * INDICATORS
		 */
		PROJECT_INDICATOR_LIST,

		/**
		 * PROJECT SYNTHESIS
		 */
		PROJECT_SYNTHESIS,

		/**
		 * PROJECT SYNTHESIS with Log frame
		 */
		PROJECT_SYNTHESIS_LOGFRAME,

		/**
		 * PROJECT SYNTHESIS with indicators
		 */
		PROJECT_SYNTHESIS_INDICATORS,

		/**
		 * PROJECT SYNTHESIS with logframe and indicators
		 */
		PROJECT_SYNTHESIS_LOGFRAME_INDICATORS,

		/**
		 * ORGUNIT SYNTHESIS
		 */
		ORGUNIT_SYNTHESIS,

		/**
		 * Global export
		 */
		GLOBAL_EXPORT;

		/**
		 * Returns the given {@code name} corresponding {@link ExportType}.
		 * 
		 * @param name
		 *          The export type name (case insensitive).
		 * @return The {@link ExportType} instance, or {@code null}.
		 */
		public static ExportType valueOfOrNull(final String name) {
			try {

				return ExportType.valueOf(name.toUpperCase());

			} catch (final Exception e) {
				return null;
			}
		}
	}

	/**
	 * Defines the export formats.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static enum ExportFormat {

		/**
		 * MS Excel 2003.
		 */
		XLS(FileType.XLS),

		/**
		 * Open document spreadsheet.
		 */
		ODS(FileType.ODS),

		/**
		 * MS Word (rtf).
		 */
		MS_WORD(FileType.RTF);

		private final FileType fileType;

		private ExportFormat(final FileType fileType) {
			this.fileType = fileType;
		}

		/**
		 * Returns the given {@code name} corresponding {@link ExportFormat}.
		 * 
		 * @param name
		 *          The export format name (case insensitive).
		 * @return The {@link ExportFormat} instance, or {@code null}.
		 */
		public static ExportFormat valueOfOrNull(final String name) {
			try {

				return ExportFormat.valueOf(name.toUpperCase());

			} catch (final Exception e) {
				return null;
			}
		}

		/**
		 * Returns the given {@code format} corresponding content-type value.
		 * 
		 * @param format
		 *          The export format instance.
		 * @return The given {@code format} corresponding content-type value, or {@code null}.
		 */
		public static String getContentType(final ExportFormat format) {
			if (format == null) {
				return null;
			}
			return format.fileType.getContentType();
		}

		/**
		 * Returns the given {@code format} corresponding extension value (with separator).
		 * 
		 * @param format
		 *          The export format instance.
		 * @return The given {@code format} corresponding extension value (with separator), or {@code null}.
		 */
		public static String getExtension(final ExportFormat format) {
			if (format == null) {
				return null;
			}
			return format.fileType.getExtension();
		}

	}

	/**
	 * Defines the export data versions.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static enum ExportDataVersion {

		LIVE_DATA,
		BACKED_UP_DATA;

		/**
		 * Returns the given {@code name} corresponding {@link ExportDataVersion}.
		 * 
		 * @param name
		 *          The export data version name (case insensitive).
		 * @return The {@link ExportDataVersion} instance, or {@code null}.
		 */
		public static ExportDataVersion valueOfOrNull(final String name) {
			try {

				return ExportDataVersion.valueOf(name.toUpperCase());

			} catch (final Exception e) {
				return null;
			}
		}
	}

	/*
	 * Maps to specify document type and extension
	 */
	private final static Map<ExportFormat, String> contentTypeMap = new HashMap<ExportFormat, String>();
	private final static Map<ExportFormat, String> extensionMap = new HashMap<ExportFormat, String>();

	static {
		contentTypeMap.put(ExportFormat.XLS, "application/vnd.ms-excel");
		contentTypeMap.put(ExportFormat.ODS, "application/vnd.oasis.opendocument.spreadsheet");
		contentTypeMap.put(ExportFormat.MS_WORD, "application/msword");

		extensionMap.put(ExportFormat.XLS, ".xls");
		extensionMap.put(ExportFormat.ODS, ".ods");
		extensionMap.put(ExportFormat.MS_WORD, ".rtf");
	}

	public static String getContentType(ExportFormat format) {
		return contentTypeMap.get(format);
	}

	public static String getExtension(ExportFormat format) {
		return extensionMap.get(format);
	}

}
