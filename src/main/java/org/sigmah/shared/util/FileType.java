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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.sigmah.client.util.ClientUtils;

/**
 * File types enumeration.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public enum FileType {

	_DEFAULT(null, "application/octet-stream"),
	CSS(FileExtension.CSS, "text/css"),
	CSV(FileExtension.CSV, "text/csv"),
	DOC(FileExtension.DOC, "application/msword"),
	DOCX(FileExtension.DOCX, "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
	DOCXML(FileExtension.XML, "application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"),
	EML(FileExtension.EML, "message/rfc822"),
	GIF(FileExtension.GIF, "image/gif"),
	HTML(FileExtension.HTML, "text/html"),
	JPEG(FileExtension.JPEG, "image/jpeg"),
	JPG(FileExtension.JPG, "image/jpeg"),
	JS(FileExtension.JS, "application/javascript"),
	MSG(FileExtension.MSG, "application/vnd.ms-outlook"),
	ODS(FileExtension.ODS, "application/vnd.oasis.opendocument.spreadsheet"),
	PDF(FileExtension.PDF, "application/pdf"),
	PNG(FileExtension.PNG, "image/png"),
	PPS(FileExtension.PPS, "application/pps"),
	PPSX(FileExtension.PPSX, "application/vnd.openxmlformats-officedocument.presentationml.slideshow"),
	PPT(FileExtension.PPT, "application/vnd.ms-powerpoint"),
	PPTX(FileExtension.PPTX, "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
	RTF(FileExtension.RTF, "application/rtf"),
	TIF(FileExtension.TIF, "application/x-tif"),
	TIFF(FileExtension.TIFF, "image/tiff"),
	TXT(FileExtension.TXT, "text/plain"),
	URL(FileExtension.URL, "text/url"),
	XHTML(FileExtension.XHTML, "application/xhtml+xml"),
	XLS(FileExtension.XLS, "application/vnd.ms-excel"),
	XLSX(FileExtension.XLSX, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
	XML(FileExtension.XML, "application/xml"),
	ZIP(FileExtension.ZIP, "application/zip"), ;

	private final FileExtension extension;
	private final String contentType;

	private FileType(FileExtension extension, String contentType) {
		this.extension = extension;
		this.contentType = contentType == null ? "" : contentType;
	}

	/**
	 * Returns the {@code FileType} corresponding label.
	 * 
	 * @return the {@code FileType} corresponding label.
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Returns the current {@code FileType} extension value (with separator).
	 * 
	 * <pre>
	 * FileType._DEFAULT.getExtension() -> "";
	 * FileType.TXT.getExtension() -> ".txt";
	 * FileType.DOCXML.getExtension() -> ".xml";
	 * FileType.XML.getExtension() -> ".xml";
	 * FileType.PDF.getExtension() -> ".pdf";
	 * </pre>
	 * 
	 * @return the current {@code FileType} extension value (with extension separator).
	 */
	public String getExtension() {
		return getExtension(true);
	}

	/**
	 * Returns the current {@code FileType} extension value (with separator).
	 * 
	 * <pre>
	 * FileType._DEFAULT.getExtension(true) -> "";
	 * FileType._DEFAULT.getExtension(false) -> "";
	 * FileType.TXT.getExtension(true) -> ".txt";
	 * FileType.TXT.getExtension(false) -> "txt";
	 * FileType.PDF.getExtension(true) -> ".pdf";
	 * FileType.PDF.getExtension(false) -> "pdf";
	 * </pre>
	 * 
	 * @param addExtensionSeparator
	 *          {@code true} to add extension separator.
	 * @return the current {@code FileType} extension value (with extension separator).
	 */
	public String getExtension(boolean addExtensionSeparator) {
		if (extension == null) {
			return "";
		}
		if (addExtensionSeparator) {
			return '.' + extension.name().toLowerCase();
		} else {
			return extension.name().toLowerCase();
		}
	}

	// ----------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ----------------------------------------------------------------------

	/**
	 * File extensions enumeration.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	private static enum FileExtension {
		CSS,
		CSV,
		DOC,
		DOCX,
		EML,
		GIF,
		HTML,
		JPEG,
		JPG,
		JS,
		MSG,
		ODS,
		PDF,
		PNG,
		PPS,
		PPSX,
		PPT,
		PPTX,
		RTF,
		TIF,
		TIFF,
		TXT,
		URL,
		XHTML,
		XLS,
		XLSX,
		XML(FileType.XML), // Multiple FileTypes reference this extension.
		ZIP;

		private final FileType associatedFileType;

		private FileExtension() {
			this(null);
		}

		private FileExtension(FileType associatedFileType) {
			this.associatedFileType = associatedFileType;
		}

		public FileType getAssociatedFileType() {
			if (associatedFileType != null) {
				return associatedFileType;
			}
			try {
				return FileType.valueOf(name());
			} catch (Exception e) {
				return null;
			}
		}
	}

	/**
	 * Retrieves the {@code FileType} from the given {@code contentType}.
	 * 
	 * @param contentType
	 *          The content-type label.
	 * @return the {@code FileType} from the given {@code contentType}, or {@code null} if no known file type can be
	 *         parsed.
	 */
	public static final FileType fromContentType(final String contentType) {
		return fromContentType(contentType, null);
	}

	/**
	 * Retrieves the {@code FileType} from the given {@code contentType}.<br>
	 * If no known file type can be parsed, returns the given {@code defaultType}.
	 * 
	 * @param contentType
	 *          The content-type label.
	 * @param defaultType
	 *          The default type returned if no known file type can be parsed.
	 * @return the {@code FileType} from the given {@code contentType}, or {@code defaultType} if no known file type can
	 *         be parsed.
	 */
	public static final FileType fromContentType(final String contentType, final FileType defaultType) {
		if (ClientUtils.isBlank(contentType)) {
			return defaultType;
		}
		for (final FileType type : values()) {
			if (type.contentType.equalsIgnoreCase(contentType.trim())) {
				return type;
			}
		}
		return defaultType;
	}

	/**
	 * Retrieves the {@code FileType} from the given {@code extension}.
	 * 
	 * <pre>
	 * fromExtension(null) -> null
	 * fromExtension("") -> null
	 * fromExtension("pdf") -> FileType.PDF
	 * fromExtension(".pdf") -> FileType.PDF
	 * fromExtension("  .pdf  ") -> FileType.PDF
	 * fromExtension("toto") -> null
	 * </pre>
	 * 
	 * @param extension
	 *          The file extension.
	 * @return the {@code FileType} from the given {@code extension}, or {@code null} if no known file type can be parsed.
	 */
	public static final FileType fromExtension(final String extension) {
		return fromExtension(extension, null);
	}

	/**
	 * Retrieves the {@code FileType} from the given {@code extension}.<br>
	 * If no known file type can be parsed, returns the given {@code defaultType}.
	 * 
	 * <pre>
	 * fromExtension(null) -> (defaultType)
	 * fromExtension("") -> (defaultType)
	 * fromExtension("pdf") -> FileType.PDF
	 * fromExtension(".pdf") -> FileType.PDF
	 * fromExtension("  .pdf  ") -> FileType.PDF
	 * fromExtension("toto") -> (defaultType)
	 * </pre>
	 * 
	 * @param extension
	 *          The file extension.
	 * @param defaultType
	 *          The default type returned if no known file type can be parsed.
	 * @return the {@code FileType} from the given {@code extension}, or {@code defaultType} if no known file type can be
	 *         parsed.
	 */
	public static final FileType fromExtension(final String extension, final FileType defaultType) {

		if (ClientUtils.isBlank(extension)) {
			return defaultType;
		}

		final FileExtension fileExtension;
		try {
			fileExtension = FileExtension.valueOf(extension.trim().replaceAll("\\.", "").toUpperCase());
		} catch (Exception e) {
			return defaultType;
		}

		return fileExtension.getAssociatedFileType();
	}

	/**
	 * Builds the {@code String} describing given {@code fileTypes} extensions.<br/>
	 * If {@code fileTypes} is {@code null} or empty, returns empty String.
	 * 
	 * <pre>
	 * asExtensions(false, FileType.PDF, FileType.CSV) -> "*.pdf;*.csv"
	 * asExtensions(true, FileType.PDF, FileType.CSV) -> "pdf, csv"
	 * </pre>
	 * 
	 * @param description
	 *          {@code true} to add {@code *.} before each file type extension.<br/>
	 *          description = {@code false} -> {@code *.pdf}<br/>
	 *          description = {@code true} -> {@code pdf}
	 * @param fileTypes
	 *          The {@code FileType} instances.
	 * @return the {@code String} describing given {@code fileTypes} extensions.
	 */
	public static final String asExtensions(final boolean description, final FileType... fileTypes) {
		final StringBuilder builder = new StringBuilder();
		if (fileTypes == null) {
			return builder.toString();
		}
		for (final FileType type : fileTypes) {
			if (type == null || ClientUtils.isBlank(type.getExtension())) {
				continue;
			}
			if (builder.length() > 0) {
				builder.append(description ? ", " : ';');
			}
			if (!description) {
				builder.append('*');
			}
			builder.append(type.getExtension(!description));
		}
		return builder.toString();
	}

	/**
	 * Retrieves the {@code FileType} collection corresponding to the given {@code extensions} (separated by {@code ","}
	 * ).<br/>
	 * Unknown extensions are ignored.
	 * 
	 * <pre>
	 * fromString(" csv,  jpeg,pdf, truc, txt") -> [FileType.CSV, FileType.JPEG, FileType.PDF, FileType.TXT]
	 * </pre>
	 * 
	 * @param extensions
	 *          The file extension(s) (separated by {@code ","} if more than one).
	 * @return the {@code FileType} collection corresponding to the given {@code extensions} (never {@code null}).
	 *         Returned collection is sorted with enum order.
	 */
	public final static List<FileType> fromString(final String extensions) {
		final List<FileType> fileTypes = new ArrayList<FileType>();
		if (ClientUtils.isBlank(extensions)) {
			return fileTypes;
		}
		for (final String extension : extensions.split(",")) {
			final FileType fileType = fromExtension(ClientUtils.trimToEmpty(extension));
			if (fileType != null) {
				fileTypes.add(fileType);
			}
		}
		Collections.sort(fileTypes);
		return fileTypes;
	}

	/**
	 * Returns if the given {@code filename} extension correspond to {@code type} extension.
	 * 
	 * @param type
	 *          The file type.
	 * @param filename
	 *          The file name.
	 * @return {@code true} if the given {@code filename} extension correspond to {@code type} extension.
	 */
	public static boolean isExtension(FileType type, String filename) {
		if (filename == null || type == null) {
			return false;
		}
		return filename.toLowerCase().endsWith(type.getExtension());
	}

	// ---------------------------------------------------------------------------------------------------
	//
	// EXTENSIONS UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------

	/**
	 * The extension separator character.
	 */
	public static final char EXTENSION_SEPARATOR = '.';

	/**
	 * The Unix separator character.
	 */
	private static final char UNIX_SEPARATOR = '/';

	/**
	 * The Windows separator character.
	 */
	private static final char WINDOWS_SEPARATOR = '\\';

	/**
	 * Gets the extension of a filename.
	 * <p>
	 * This method returns the textual part of the filename after the last dot. There must be no directory separator after
	 * the dot.
	 * 
	 * <pre>
	 * foo.txt      --> "txt"
	 * a/b/c.jpg    --> "jpg"
	 * a/b.txt/c    --> ""
	 * a/b/c        --> ""
	 * </pre>
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 *
	 * @param filename
	 *          the filename to retrieve the extension of.
	 * @return the extension of the file or an empty string if none exists or <code>null</code> if the filename is
	 *         <code>null</code>.
	 */
	public static String getExtension(final String filename) {
		if (filename == null) {
			return null;
		}
		int index = indexOfExtension(filename);
		if (index == -1) {
			return "";
		} else {
			return filename.substring(index + 1);
		}
	}

	/**
	 * Returns the index of the last directory separator character.
	 * <p>
	 * This method will handle a file in either Unix or Windows format. The position of the last forward or backslash is
	 * returned.
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 * 
	 * @param filename
	 *          the filename to find the last path separator in, null returns -1
	 * @return the index of the last separator character, or -1 if there is no such character
	 */
	public static int indexOfLastSeparator(String filename) {
		if (filename == null) {
			return -1;
		}
		int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
		int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
		return Math.max(lastUnixPos, lastWindowsPos);
	}

	/**
	 * Returns the index of the last extension separator character, which is a dot.
	 * <p>
	 * This method also checks that there is no directory separator after the last dot. To do this it uses
	 * {@link #indexOfLastSeparator(String)} which will handle a file in either Unix or Windows format.
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 * 
	 * @param filename
	 *          the filename to find the last path separator in, null returns -1
	 * @return the index of the last separator character, or -1 if there is no such character
	 */
	public static int indexOfExtension(String filename) {
		if (filename == null) {
			return -1;
		}
		int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);
		int lastSeparator = indexOfLastSeparator(filename);
		return lastSeparator > extensionPos ? -1 : extensionPos;
	}

}
