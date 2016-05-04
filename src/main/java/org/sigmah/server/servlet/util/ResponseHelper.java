package org.sigmah.server.servlet.util;

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


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;
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
		ATTACHMENT("attachment"),
		BASE64(null);

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
		if(contentDisposition.value != null) {
			response.addHeader("Content-disposition", contentDisposition.value + "; filename=\"" + filename + "\"");
		}

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

		final FileType fileType;
		if(contentDisposition == ContentDisposition.BASE64) {
			fileType = FileType.HTML;
		} else {
			fileType = FileType.fromContentType(contentType);
		}
		
		ResponseHelper.setResponseForDownload(response, filename, fileType, contentLength, contentDisposition);

		// Download stream.
		try (final BufferedInputStream input = new BufferedInputStream(inputStream);
				final BufferedOutputStream output = new BufferedOutputStream(response.getOutputStream());) {

			if(contentDisposition == ContentDisposition.BASE64) {
				// Base64 download.
				final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				streamToStream(input, buffer);
				
				final String url = "data:" + (contentType != null ? contentType : FileType._DEFAULT.getContentType()) + ";base64,";
				output.write(url.getBytes("ASCII"));
				Base64.encode(buffer.toByteArray(), output);
				
			} else {
				streamToStream(input, output);
			}
		}
	}

	/**
	 * Read the content of the given input stream and write all its content to
	 * the given output stream.
	 *
	 * The streams are not closed by this method.
	 * 
	 * @param input Source data.
	 * @param output Destination.
	 * @throws IOException If read or write operation failed.
	 */
	private static void streamToStream(final InputStream input, final OutputStream output) throws IOException {
		// Normal download.
		final byte[] buffer = new byte[BUFFER_SIZE];
		for (int length; (length = input.read(buffer)) > 0;) {
			output.write(buffer, 0, length);
		}
	}
	
}
