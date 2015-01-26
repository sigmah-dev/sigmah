package org.sigmah.server.servlet.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.sigmah.shared.util.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class providing methods for {@code javax.servlet.http.HttpServletResponse} stream.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class ResponseHelper {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ResponseHelper.class);

	/**
	 * Data transfer buffer size.
	 */
	public static final int BUFFER_SIZE = 32 * 1024;

	private ResponseHelper() {
		// Utility class providing static methods only.
	}

	// -----------------------------------------------------------
	//
	// RESPONSE HEADERS UTILITY METHODS.
	//
	// -----------------------------------------------------------

	/**
	 * Response header {@code Content-disposition} possible values.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static enum ContentDisposition {
		INLINE("inline"),
		ATTACHMENT("attachment");

		private final String value;

		private ContentDisposition(final String value) {
			this.value = value;
		}
	}

	/**
	 * Initializes the given {@code response} headers and content-type for download.
	 * 
	 * @param response
	 *          The response stream.
	 * @param filename
	 *          The exported file name (without extension).
	 * @param fileType
	 *          The exported file type (default file type is used if {@code null}).
	 */
	public static final void setResponseForDownload(final HttpServletResponse response, String filename, FileType fileType) {
		setResponseForDownload(response, filename, fileType, (Long) null, null);
	}

	/**
	 * Initializes the given {@code response} headers and content-type for download.
	 * 
	 * @param response
	 *          The response stream.
	 * @param filename
	 *          The exported file name (without extension).
	 * @param fileType
	 *          The exported file type ({@code null} to set a default file type).
	 * @param contentDisposition
	 *          The {@code ContentDisposition} value.
	 */
	public static final void setResponseForDownload(HttpServletResponse response, String filename, FileType fileType, ContentDisposition contentDisposition) {
		setResponseForDownload(response, filename, fileType, (Long) null, contentDisposition);
	}

	/**
	 * Initializes the given {@code response} headers and content-type for download.
	 * 
	 * @param response
	 *          The response stream.
	 * @param filename
	 *          The exported file name (without extension).
	 * @param fileType
	 *          The exported file type ({@code null} to set a default file type).
	 * @param contentLength
	 *          The content length ({@code null} to ignore).
	 * @param contentDisposition
	 *          The {@code ContentDisposition} value.
	 */
	public static final void setResponseForDownload(HttpServletResponse response, String filename, FileType fileType, Integer contentLength,
			ContentDisposition contentDisposition) {
		setResponseForDownload(response, filename, fileType, contentLength != null ? new Long(contentLength) : null, contentDisposition);
	}

	/**
	 * Initializes the given {@code response} headers and content-type for download.
	 * 
	 * @param response
	 *          The response stream.
	 * @param filename
	 *          The exported file name (with or without extension). If no extension, the {@code fileType} corresponding
	 *          extension is automatically added.
	 * @param fileType
	 *          The exported file type ({@code null} to set a default file type).
	 * @param contentLength
	 *          The content length ({@code null} to ignore).
	 * @param contentDisposition
	 *          The {@code ContentDisposition} value.
	 */
	public static final void setResponseForDownload(HttpServletResponse response, String filename, FileType fileType, Long contentLength,
			ContentDisposition contentDisposition) {

		if (StringUtils.isBlank(filename)) {
			filename = "sigmah-download"; // Default downloaded file name.
		}
		if (fileType == null) {
			fileType = FileType._DEFAULT;
		}
		if (!FileType.isExtension(fileType, filename)) {
			filename += fileType.getExtension();
		}

		if (contentDisposition == null) {
			contentDisposition = ContentDisposition.ATTACHMENT;
		}

		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		response.setHeader("Pragma", "public");
		response.addHeader("Content-disposition", contentDisposition.value + "; filename=\"" + filename + "\"");

		response.setContentType(fileType.getContentType());
		if (contentLength != null) {
			response.setContentLength(contentLength.intValue());
		}
	}

	// -----------------------------------------------------------
	//
	// DOWNLOAD UTILITY METHODS.
	//
	// -----------------------------------------------------------

	/**
	 * Executes download process.
	 * 
	 * @param response
	 *          The HTTP response stream (required).
	 * @param inputStream
	 *          The content input stream (required).
	 * @param contentType
	 *          The downloaded file content-type ({@code null} to set a default content-type).
	 * @param filename
	 *          The downloaded file name without extension ({@code null} to set a default file name).
	 * @param contentLength
	 *          The content length ({@code null} to ignore).
	 * @throws IOException
	 */
	public static final void executeDownload(HttpServletResponse response, InputStream inputStream, String contentType, String filename, Long contentLength)
			throws IOException {

		executeDownload(response, inputStream, contentType, filename, contentLength, null);
	}

	/**
	 * Executes download process.
	 * 
	 * @param response
	 *          The HTTP response stream (required).
	 * @param inputStream
	 *          The content input stream (required).
	 * @param contentType
	 *          The downloaded file content-type ({@code null} to set a default content-type).
	 * @param filename
	 *          The downloaded file name without extension ({@code null} to set a default file name).
	 * @param contentLength
	 *          The content length ({@code null} to ignore).
	 * @param contentDisposition
	 *          The {@code ContentDisposition} value.
	 * @throws IOException
	 */
	public static final void executeDownload(HttpServletResponse response, InputStream inputStream, String contentType, String filename, Long contentLength,
			ContentDisposition contentDisposition) throws IOException {

		if (LOG.isDebugEnabled()) {
			LOG.debug("[DOWNLOAD] DOWNLOADING FILE '{}' WITH CONTENT-TYPE '{}' AND LENGTH {}.", filename, contentType, contentLength);
		}

		if (response == null || inputStream == null) {
			throw new IllegalArgumentException("Missing required data to process file download.");
		}

		ResponseHelper.setResponseForDownload(response, filename, FileType.fromContentType(contentType), contentLength, contentDisposition);

		// Download stream.
		try (final BufferedInputStream input = new BufferedInputStream(inputStream);
				final BufferedOutputStream output = new BufferedOutputStream(response.getOutputStream());) {

			final byte[] buffer = new byte[BUFFER_SIZE];
			for (int length = 0; (length = input.read(buffer)) > 0;) {
				output.write(buffer, 0, length);
			}
		}
	}

}
